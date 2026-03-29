package com.idme.miniapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idme.miniapp.config.XdmProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class XdmRuntimeService {

    private final ObjectMapper objectMapper;
    private final XdmGatewayService xdmGatewayService;
    private final XdmProperties xdmProperties;

    public XdmRuntimeService(
        ObjectMapper objectMapper,
        XdmGatewayService xdmGatewayService,
        XdmProperties xdmProperties
    ) {
        this.objectMapper = objectMapper;
        this.xdmGatewayService = xdmGatewayService;
        this.xdmProperties = xdmProperties;
    }

    public List<JsonNode> list(String entityType) {
        return executeList("/dynamic/api/" + entityType + "/list", buildEntityQueryParam(entityType));
    }

    public JsonNode get(String entityType, String id) {
        return executeFirst("/dynamic/api/" + entityType + "/get", Map.of("id", id));
    }

    public JsonNode create(String entityType, Map<String, Object> params) {
        return executeFirst("/dynamic/api/" + entityType + "/create", params);
    }

    public JsonNode update(String entityType, Map<String, Object> params) {
        return executeFirst("/dynamic/api/" + entityType + "/update", params);
    }

    public void delete(String entityType, String id) {
        executeList("/dynamic/api/" + entityType + "/delete", Map.of("id", id));
    }

    public List<JsonNode> listRelation(String relationType) {
        return list(relationType);
    }

    public JsonNode createRelation(String relationType, Map<String, Object> params) {
        return create(relationType, params);
    }

    public void updateRelation(String relationType, Map<String, Object> params) {
        executeList("/dynamic/api/" + relationType + "/update", params);
    }

    public void deleteRelation(String relationType, String relationId) {
        delete(relationType, relationId);
    }

    public List<JsonNode> executeList(String path, Map<String, Object> params) {
        JsonNode response = xdmGatewayService.proxy(path, HttpMethod.POST, objectMapper.valueToTree(buildParamVo(params)));
        validateResponse(path, response);
        JsonNode data = response.path("data");
        List<JsonNode> result = new ArrayList<>();
        if (data.isArray()) {
            data.forEach(result::add);
        } else if (!data.isMissingNode() && !data.isNull()) {
            result.add(data);
        }
        return result;
    }

    public JsonNode executeFirst(String path, Map<String, Object> params) {
        List<JsonNode> data = executeList(path, params);
        if (data.isEmpty()) {
            throw new IllegalStateException("xDM-F 返回为空: " + path);
        }
        return data.get(0);
    }

    private Map<String, Object> buildEntityQueryParam(String entityType) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("entityType", entityType);
        return params;
    }

    private Map<String, Object> buildParamVo(Map<String, Object> params) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (StringUtils.hasText(xdmProperties.getApplicationId())) {
            body.put("applicationId", xdmProperties.getApplicationId());
        } else {
            body.put("applicationId", "");
        }
        body.put("params", params);
        return body;
    }

    private void validateResponse(String path, JsonNode response) {
        String result = response.path("result").asText();
        if (!"SUCCESS".equalsIgnoreCase(result)) {
            throw new IllegalStateException("xDM-F 调用失败: " + path + ", result=" + result + ", response=" + response);
        }
        JsonNode errors = response.path("errors");
        if (errors.isArray() && !errors.isEmpty()) {
            throw new IllegalStateException("xDM-F 返回错误: " + path + ", errors=" + errors);
        }
    }
}
