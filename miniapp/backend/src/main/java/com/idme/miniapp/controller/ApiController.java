package com.idme.miniapp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.idme.miniapp.service.XdmGatewayService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final XdmGatewayService xdmGatewayService;

    public ApiController(XdmGatewayService xdmGatewayService) {
        this.xdmGatewayService = xdmGatewayService;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "UP");
        result.put("service", "miniapp-backend");
        result.put("time", OffsetDateTime.now().toString());
        return result;
    }

    @GetMapping("/xdm/health")
    public JsonNode xdmHealth() {
        return xdmGatewayService.health();
    }

    @GetMapping("/xdm/me")
    public JsonNode xdmMe() {
        return xdmGatewayService.me();
    }

    @PostMapping("/xdm/proxy")
    public JsonNode xdmProxy(@Valid @RequestBody ProxyRequest request) {
        HttpMethod method = HttpMethod.valueOf(request.getMethod().toUpperCase());
        return xdmGatewayService.proxy(request.getPath(), method, request.getBody());
    }

    public static class ProxyRequest {

        @NotBlank
        private String path;

        private String method = "GET";

        private JsonNode body;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public JsonNode getBody() {
            return body;
        }

        public void setBody(JsonNode body) {
            this.body = body;
        }
    }
}
