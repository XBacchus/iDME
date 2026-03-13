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
public class EquipmentService {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final XdmGatewayService gateway;
    private final ObjectMapper mapper;

    public EquipmentService(XdmGatewayService gateway, ObjectMapper mapper) {
        this.gateway = gateway;
        this.mapper = mapper;
    }

    public JsonNode createEquipment(JsonNode payload) {
        ObjectNode body = mapper.createObjectNode();
        mapToXdm(payload, body);
        JsonNode xdmResult = gateway.proxy("/dynamic/api/Equipment/create", HttpMethod.POST, body);
        JsonNode created = XdmNodeHelper.extractSingle(xdmResult);
        return created.isMissingNode() || created.isNull() || created.isEmpty()
            ? mapFromXdm(body)
            : mapFromXdm(created);
    }

    public JsonNode listEquipments(String keyword, Integer page, Integer size) {
        int current = normalizePositive(page, DEFAULT_PAGE);
        int pageSize = normalizePositive(size, DEFAULT_PAGE_SIZE);

        ObjectNode body = mapper.createObjectNode();
        ObjectNode condition = mapper.createObjectNode();
        if (StringUtils.hasText(keyword)) {
            ArrayNode or = mapper.createArrayNode();
            or.add(mapper.createObjectNode().put("equipmentCode", keyword));
            or.add(mapper.createObjectNode().put("equipmentName", keyword));
            condition.set("$or", or);
        }
        body.set("condition", condition);
        body.put("pageNo", current);
        body.put("pageSize", pageSize);

        JsonNode xdmResult = gateway.proxy("/dynamic/api/Equipment/query", HttpMethod.POST, body);
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

    public JsonNode getEquipment(String id) {
        ObjectNode body = mapper.createObjectNode();
        body.put("id", id);
        JsonNode xdmResult = gateway.proxy("/dynamic/api/Equipment/get", HttpMethod.POST, body);
        return mapFromXdm(XdmNodeHelper.extractSingle(xdmResult));
    }

    public JsonNode updateEquipment(String id, JsonNode payload) {
        ObjectNode body = mapper.createObjectNode();
        body.put("id", id);
        mapToXdm(payload, body);
        JsonNode xdmResult = gateway.proxy("/dynamic/api/Equipment/update", HttpMethod.POST, body);
        JsonNode updated = XdmNodeHelper.extractSingle(xdmResult);
        return updated.isMissingNode() || updated.isNull() || updated.isEmpty()
            ? mapFromXdm(body)
            : mapFromXdm(updated);
    }

    public JsonNode deleteEquipment(String id) {
        ObjectNode body = mapper.createObjectNode();
        body.put("id", id);
        gateway.proxy("/dynamic/api/Equipment/delete", HttpMethod.POST, body);
        ObjectNode result = mapper.createObjectNode();
        result.put("id", id);
        result.put("deleted", true);
        return result;
    }

    private void mapToXdm(JsonNode source, ObjectNode target) {
        if (source == null || source.isNull()) {
            return;
        }
        copyIfPresent(source, target, "equipmentNo", "equipmentCode");
        copyIfPresent(source, target, "equipmentName", "equipmentName");
        copyIfPresent(source, target, "model", "specModel");
        copyIfPresent(source, target, "manufacturer", "manufacturer");
        copyIfPresent(source, target, "location", "location");
        copyIfPresent(source, target, "brand", "brand");
        copyIfPresent(source, target, "supplier", "supplier");
        copyIfPresent(source, target, "productionDate", "productionDate");
        copyIfPresent(source, target, "serviceLifeYears", "serviceLifeYears");
        copyIfPresent(source, target, "depreciationMethod", "depreciationMethod");
        copyIfPresent(source, target, "technicalParams", "technicalParams");
        copyIfPresent(source, target, "sparePartsInfo", "sparePartsInfo");
    }

    private ObjectNode mapFromXdm(JsonNode source) {
        ObjectNode mapped = mapper.createObjectNode();
        putIfNotBlank(mapped, "id", XdmNodeHelper.text(source, "id", "_id"));
        putIfNotBlank(mapped, "equipmentNo", XdmNodeHelper.text(source, "equipmentCode", "equipmentNo"));
        putIfNotBlank(mapped, "equipmentName", XdmNodeHelper.text(source, "equipmentName"));
        putIfNotBlank(mapped, "model", XdmNodeHelper.text(source, "specModel", "model"));
        putIfNotBlank(mapped, "manufacturer", XdmNodeHelper.text(source, "manufacturer"));
        putIfNotBlank(mapped, "location", XdmNodeHelper.text(source, "location"));
        putIfNotBlank(mapped, "brand", XdmNodeHelper.text(source, "brand"));
        putIfNotBlank(mapped, "supplier", XdmNodeHelper.text(source, "supplier"));
        putIfNotBlank(mapped, "productionDate", XdmNodeHelper.text(source, "productionDate"));
        putIfNotBlank(mapped, "serviceLifeYears", XdmNodeHelper.text(source, "serviceLifeYears"));
        putIfNotBlank(mapped, "depreciationMethod", XdmNodeHelper.text(source, "depreciationMethod"));
        putIfNotBlank(mapped, "technicalParams", XdmNodeHelper.text(source, "technicalParams"));
        putIfNotBlank(mapped, "sparePartsInfo", XdmNodeHelper.text(source, "sparePartsInfo"));
        putIfNotBlank(mapped, "status", XdmNodeHelper.text(source, "status"));
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
