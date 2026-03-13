package com.idme.miniapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.idme.miniapp.service.support.XdmNodeHelper;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class WorkingPlanService {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final XdmGatewayService gateway;
    private final ObjectMapper mapper;

    public WorkingPlanService(XdmGatewayService gateway, ObjectMapper mapper) {
        this.gateway = gateway;
        this.mapper = mapper;
    }

    public JsonNode createWorkingPlan(JsonNode payload) {
        ObjectNode body = mapper.createObjectNode();
        mapToXdm(payload, body);
        JsonNode xdmResult = gateway.proxy("/dynamic/api/WorkingPlan/create", HttpMethod.POST, body);
        JsonNode created = XdmNodeHelper.extractSingle(xdmResult);
        return created.isMissingNode() || created.isNull() || created.isEmpty()
            ? mapFromXdm(body)
            : mapFromXdm(created);
    }

    public JsonNode listWorkingPlans(String keyword, Integer page, Integer size) {
        int current = normalizePositive(page, DEFAULT_PAGE);
        int pageSize = normalizePositive(size, DEFAULT_PAGE_SIZE);

        ObjectNode body = mapper.createObjectNode();
        ObjectNode condition = mapper.createObjectNode();
        if (StringUtils.hasText(keyword)) {
            ArrayNode or = mapper.createArrayNode();
            or.add(mapper.createObjectNode().put("planCode", keyword));
            or.add(mapper.createObjectNode().put("planName", keyword));
            condition.set("$or", or);
        }
        body.set("condition", condition);
        body.put("pageNo", current);
        body.put("pageSize", pageSize);

        JsonNode xdmResult = gateway.proxy("/dynamic/api/WorkingPlan/query", HttpMethod.POST, body);
        ArrayNode records = mapper.createArrayNode();
        XdmNodeHelper.extractRecords(mapper, xdmResult).forEach(item -> records.add(mapFromXdm(item)));

        long total = XdmNodeHelper.longValue(xdmResult, records.size(), "/data/total", "/total", "/count");
        int pages = pageSize == 0 ? 0 : (int) Math.ceil((double) total / pageSize);

        ObjectNode pageData = mapper.createObjectNode();
        pageData.set("records", records);
        pageData.put("total", total);
        pageData.put("size", pageSize);
        pageData.put("current", current);
        pageData.put("pages", pages);
        return pageData;
    }

    public JsonNode getWorkingPlanDetail(String id) {
        ObjectNode getBody = mapper.createObjectNode();
        getBody.put("id", id);
        JsonNode planResult = gateway.proxy("/dynamic/api/WorkingPlan/get", HttpMethod.POST, getBody);

        ObjectNode detail = mapFromXdm(XdmNodeHelper.extractSingle(planResult));
        if (!detail.hasNonNull("id")) {
            detail.put("id", id);
        }
        detail.set("procedures", queryLinkedProcedures(id));
        return detail;
    }

    public JsonNode updateWorkingPlan(String id, JsonNode payload) {
        ObjectNode body = mapper.createObjectNode();
        body.put("id", id);
        mapToXdm(payload, body);
        JsonNode xdmResult = gateway.proxy("/dynamic/api/WorkingPlan/update", HttpMethod.POST, body);
        JsonNode updated = XdmNodeHelper.extractSingle(xdmResult);
        return updated.isMissingNode() || updated.isNull() || updated.isEmpty()
            ? mapFromXdm(body)
            : mapFromXdm(updated);
    }

    public JsonNode deleteWorkingPlan(String id) {
        ObjectNode body = mapper.createObjectNode();
        body.put("id", id);
        gateway.proxy("/dynamic/api/WorkingPlan/delete", HttpMethod.POST, body);
        ObjectNode result = mapper.createObjectNode();
        result.put("id", id);
        result.put("deleted", true);
        return result;
    }

    private ArrayNode queryLinkedProcedures(String planId) {
        ObjectNode relationBody = mapper.createObjectNode();
        ObjectNode condition = mapper.createObjectNode();
        condition.put("source.id", planId);
        relationBody.set("condition", condition);

        JsonNode relationResult = gateway.proxy("/dynamic/api/WorkingPlan_WorkingProcedure/query",
            HttpMethod.POST, relationBody);

        ArrayNode procedures = mapper.createArrayNode();
        for (JsonNode relation : XdmNodeHelper.extractRecords(mapper, relationResult)) {
            String procedureId = XdmNodeHelper.text(relation, "target.id", "/target/id", "procedureId");
            if (!StringUtils.hasText(procedureId)) {
                continue;
            }
            ObjectNode getBody = mapper.createObjectNode();
            getBody.put("id", procedureId);
            JsonNode procedureResult = gateway.proxy("/dynamic/api/WorkingProcedure/get", HttpMethod.POST, getBody);
            JsonNode procedure = XdmNodeHelper.extractSingle(procedureResult);
            ObjectNode mapped = mapper.createObjectNode();
            mapped.put("id", procedureId);
            putIfNotBlank(mapped, "procedureCode", XdmNodeHelper.text(procedure, "procedureCode", "code"));
            putIfNotBlank(mapped, "procedureName", XdmNodeHelper.text(procedure, "procedureName", "name"));
            mapped.put("order", (int) XdmNodeHelper.longValue(procedure, 0, "order", "sortNo"));
            procedures.add(mapped);
        }
        return procedures;
    }

    private void mapToXdm(JsonNode source, ObjectNode target) {
        if (source == null || source.isNull()) {
            return;
        }
        copyIfPresent(source, target, "planName", "planName");
        copyIfPresent(source, target, "planCode", "planCode");
        copyIfPresent(source, target, "versionNo", "versionNo");
        copyIfPresent(source, target, "belongProduct", "belongProduct");
        copyIfPresent(source, target, "partId", "partId");
    }

    private ObjectNode mapFromXdm(JsonNode source) {
        ObjectNode mapped = mapper.createObjectNode();
        putIfNotBlank(mapped, "id", XdmNodeHelper.text(source, "id", "_id"));
        putIfNotBlank(mapped, "planName", XdmNodeHelper.text(source, "planName"));
        putIfNotBlank(mapped, "planCode", XdmNodeHelper.text(source, "planCode"));
        putIfNotBlank(mapped, "partId", XdmNodeHelper.text(source, "partId"));
        putIfNotBlank(mapped, "status", XdmNodeHelper.text(source, "status"));
        putIfNotBlank(mapped, "versionNo", XdmNodeHelper.text(source, "versionNo"));
        putIfNotBlank(mapped, "belongProduct", XdmNodeHelper.text(source, "belongProduct"));
        return mapped;
    }

    private void copyIfPresent(JsonNode source, ObjectNode target, String sourceField, String targetField) {
        JsonNode value = XdmNodeHelper.value(source, sourceField);
        if (!value.isMissingNode() && !value.isNull()) {
            target.set(targetField, value);
        }
    }

    private void putIfNotBlank(ObjectNode target, String key, String value) {
        if (StringUtils.hasText(value)) {
            target.put(key, value);
        }
    }

    private int normalizePositive(Integer value, int fallback) {
        return value == null || value <= 0 ? fallback : value;
    }
}
