package com.idme.miniapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idme.miniapp.config.XdmProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.HttpCookie;
import java.net.URI;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

@Service
public class XdmGatewayService {

    private static final Duration SESSION_TTL = Duration.ofMinutes(30);

    private final ObjectMapper objectMapper;
    private final XdmProperties props;
    private final Object lock = new Object();

    private volatile SessionHolder session;

    public XdmGatewayService(ObjectMapper objectMapper, XdmProperties props) {
        this.objectMapper = objectMapper;
        this.props = props;
    }

    public JsonNode health() {
        return publicRequest("/v1/health", HttpMethod.GET, null);
    }

    public JsonNode me() {
        return authedRequest("/rdm/basic/api/rest/me", HttpMethod.GET, null);
    }

    public JsonNode proxy(String path, HttpMethod method, JsonNode body) {
        return authedRequest(path, method, body);
    }

    private JsonNode publicRequest(String path, HttpMethod method, JsonNode body) {
        RestTemplate template = new RestTemplate();
        HttpEntity<String> entity = body == null
            ? new HttpEntity<>(new HttpHeaders())
            : new HttpEntity<>(body.toString(), jsonHeaders());

        ResponseEntity<String> response = template.exchange(buildUri(path), method, entity, String.class);
        return parseJson(response.getBody());
    }

    private JsonNode authedRequest(String path, HttpMethod method, JsonNode body) {
        SessionHolder current = ensureSession();
        try {
            return executeWithSession(current, path, method, body);
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().value() == 401) {
                synchronized (lock) {
                    session = null;
                }
                SessionHolder retry = ensureSession();
                return executeWithSession(retry, path, method, body);
            }
            throw ex;
        }
    }

    private JsonNode executeWithSession(SessionHolder holder, String path, HttpMethod method, JsonNode body) {
        HttpHeaders headers = baseHeaders(holder);
        HttpEntity<String> entity = body == null
            ? new HttpEntity<>(headers)
            : new HttpEntity<>(body.toString(), mergeJson(headers));

        ResponseEntity<String> response = holder.restTemplate.exchange(buildUri(path), method, entity, String.class);
        return parseJson(response.getBody());
    }

    private SessionHolder ensureSession() {
        SessionHolder current = session;
        if (current != null && !isExpired(current)) {
            return current;
        }

        synchronized (lock) {
            current = session;
            if (current != null && !isExpired(current)) {
                return current;
            }
            session = loginNewSession();
            return session;
        }
    }

    private boolean isExpired(SessionHolder holder) {
        return Instant.now().isAfter(holder.loginAt.plus(SESSION_TTL));
    }

    private SessionHolder loginNewSession() {
        SessionHolder holder = SessionFactory.newSession(props.getBaseUrl());

        // 先请求验证码接口，获取 SESSION 和 XSRF-TOKEN Cookie。
        holder.restTemplate.exchange(buildUri("/v1/login/verifycode"), HttpMethod.GET,
            new HttpEntity<>(new HttpHeaders()), String.class);

        String xsrfToken = SessionFactory.readCookie(holder, "XSRF-TOKEN");
        String csrfToken = randomHex(24);

        SessionFactory.putCookie(holder, "csrf_header", "X-CSRF-TOKEN");
        SessionFactory.putCookie(holder, "csrf_token", csrfToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-requested-with", "XMLHttpRequest");
        headers.set("accept-tensecontext", "runtime");
        headers.set("x-dme-timezone", props.getTimezone());
        headers.set("x-xsrf-token", xsrfToken);
        headers.set("x-csrf-token", csrfToken);
        headers.set("tenantid", props.getTenantId());
        headers.set("applicationid", props.getApplicationId());
        headers.set("x-user-id", "");
        headers.set("modifier", "");

        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("username", props.getUsername());
        loginBody.put("password", props.getPassword());
        loginBody.put("verifyCode", props.getVerifyCode());

        HttpEntity<String> entity = new HttpEntity<>(toJson(loginBody), headers);
        ResponseEntity<String> response = holder.restTemplate.exchange(buildUri("/v1/login/login"),
            HttpMethod.POST, entity, String.class);

        JsonNode body = parseJson(response.getBody());
        String errorCode = body.path("error_code").asText();
        if (!"PUB-000000".equals(errorCode)) {
            throw new IllegalStateException("xDM-F 登录失败: " + body);
        }

        holder.xsrfToken = xsrfToken;
        holder.csrfToken = csrfToken;
        holder.loginAt = Instant.now();

        return holder;
    }

    private HttpHeaders baseHeaders(SessionHolder holder) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-requested-with", "XMLHttpRequest");
        headers.set("accept-tensecontext", "runtime");
        headers.set("x-dme-timezone", props.getTimezone());
        headers.set("x-xsrf-token", holder.xsrfToken);
        headers.set("x-csrf-token", holder.csrfToken);
        headers.set("tenantid", props.getTenantId());
        headers.set("applicationid", props.getApplicationId());
        headers.set("x-user-id", "");
        headers.set("modifier", "");
        return headers;
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private HttpHeaders mergeJson(HttpHeaders base) {
        HttpHeaders merged = new HttpHeaders();
        merged.putAll(base);
        merged.setContentType(MediaType.APPLICATION_JSON);
        return merged;
    }

    private URI buildUri(String path) {
        String base = props.getBaseUrl();
        String normalizedBase = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        return URI.create(normalizedBase + normalizedPath);
    }

    private String randomHex(int bytes) {
        byte[] raw = new byte[bytes];
        new SecureRandom().nextBytes(raw);
        return HexFormat.of().withUpperCase().formatHex(raw);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            throw new IllegalStateException("JSON 序列化失败", ex);
        }
    }

    private JsonNode parseJson(String value) {
        try {
            return objectMapper.readTree(value == null ? "{}" : value);
        } catch (Exception ex) {
            throw new IllegalStateException("JSON 解析失败", ex);
        }
    }

    private static final class SessionHolder {
        private final RestTemplate restTemplate;
        private final URI baseUri;
        private final java.net.CookieManager cookieManager;

        private String xsrfToken;
        private String csrfToken;
        private Instant loginAt;

        private SessionHolder(RestTemplate restTemplate, URI baseUri, java.net.CookieManager cookieManager) {
            this.restTemplate = restTemplate;
            this.baseUri = baseUri;
            this.cookieManager = cookieManager;
        }
    }

    private static final class SessionFactory {

        private static SessionHolder newSession(String baseUrl) {
            URI baseUri = URI.create(baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl);
            java.net.CookieManager cookieManager = new java.net.CookieManager();
            cookieManager.setCookiePolicy(java.net.CookiePolicy.ACCEPT_ALL);

            java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .connectTimeout(Duration.ofSeconds(15))
                .build();

            org.springframework.http.client.JdkClientHttpRequestFactory factory =
                new org.springframework.http.client.JdkClientHttpRequestFactory(client);
            factory.setReadTimeout(Duration.ofSeconds(30));

            RestTemplate restTemplate = new RestTemplate(factory);
            return new SessionHolder(restTemplate, baseUri, cookieManager);
        }

        private static String readCookie(SessionHolder holder, String name) {
            return holder.cookieManager.getCookieStore().get(holder.baseUri).stream()
                .filter(c -> name.equals(c.getName()))
                .map(HttpCookie::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("缺少 Cookie: " + name));
        }

        private static void putCookie(SessionHolder holder, String name, String value) {
            HttpCookie cookie = new HttpCookie(name, value);
            cookie.setPath(holder.baseUri.getPath());
            holder.cookieManager.getCookieStore().add(holder.baseUri, cookie);
        }
    }
}
