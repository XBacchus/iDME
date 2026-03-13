package com.idme.miniapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.idme.miniapp.service.support.XdmNodeHelper;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Service
public class PartService {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final XdmGatewayService gateway;
    private final ObjectMapper mapper;

    public PartService(XdmGatewayService gateway, ObjectMapper mapper) {
        this.gateway = gateway;
        this.mapper = mapper;
    }

    public JsonNode queryParts(String keyword, Long categoryId, Integer page, Integer size) {
        int current = normalizePositive(page, DEFAULT_PAGE);
        int pageSize = normalizePositive(size, DEFAULT_PAGE_SIZE);

        ObjectNode body = mapper.createObjectNode();
        ObjectNode condition = mapper.createObjectNode();

        if (StringUtils.hasText(keyword)) {
            ArrayNode or = mapper.createArrayNode();
            or.add(mapper.createObjectNode().put("partCode", keyword));
            or.add(mapper.createObjectNode().put("partName", keyword));
            condition.set("$or", or);
        }

        if (categoryId != null) {
            condition.put("categoryId", categoryId);
        }

        body.set("condition", condition);
        body.put("pageNo", current);
        body.put("pageSize", pageSize);

        JsonNode xdmResult = gateway.proxy("/dynamic/api/Part/query", HttpMethod.POST, body);
        ArrayNode records = mapper.createArrayNode();
        XdmNodeHelper.extractRecords(mapper, xdmResult).forEach(item -> records.add(mapPart(item)));

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

    public JsonNode getPartById(String id) {
        ObjectNode body = mapper.createObjectNode();
        body.put("id", id);
        JsonNode xdmResult = gateway.proxy("/dynamic/api/Part/get", HttpMethod.POST, body);
        return mapPart(XdmNodeHelper.extractSingle(xdmResult));
    }

    public JsonNode createPart(JsonNode partData) {
        ObjectNode body = mapper.createObjectNode();
        mapPartToXdm(partData, body);
        JsonNode xdmResult = gateway.proxy("/dynamic/api/Part/create", HttpMethod.POST, body);
        JsonNode created = XdmNodeHelper.extractSingle(xdmResult);
        if (created.isMissingNode() || created.isNull() || created.isEmpty()) {
            ObjectNode fallback = mapper.createObjectNode();
            fallback.setAll(body);
            fallback.put("id", XdmNodeHelper.text(xdmResult, "id", "/data/id", "/result/id"));
            return mapPart(fallback);
        }
        return mapPart(created);
    }

    public JsonNode updatePart(String id, JsonNode partData) {
        ObjectNode body = mapper.createObjectNode();
        body.put("id", id);
        mapPartToXdm(partData, body);
        JsonNode xdmResult = gateway.proxy("/dynamic/api/Part/update", HttpMethod.POST, body);
        JsonNode updated = XdmNodeHelper.extractSingle(xdmResult);
        if (updated.isMissingNode() || updated.isNull() || updated.isEmpty()) {
            return mapPart(body);
        }
        return mapPart(updated);
    }

    public JsonNode deletePart(String id) {
        ObjectNode body = mapper.createObjectNode();
        body.put("id", id);
        gateway.proxy("/dynamic/api/Part/delete", HttpMethod.POST, body);
        ObjectNode result = mapper.createObjectNode();
        result.put("id", id);
        result.put("deleted", true);
        return result;
    }

    public JsonNode getPartBom(String id) {
        ObjectNode root = asObject(getPartById(id));
        if (root.isEmpty()) {
            root.put("id", id);
        }
        Set<String> visited = new HashSet<>();
        visited.add(id);
        root.set("children", loadBomChildren(id, visited));
        return root;
    }

    public JsonNode updatePartBom(String id, JsonNode bomData) {
        ArrayNode children = extractChildren(bomData);

        ObjectNode queryBody = mapper.createObjectNode();
        ObjectNode condition = mapper.createObjectNode();
        condition.put("source.id", id);
        queryBody.set("condition", condition);
        JsonNode existsResult = gateway.proxy("/dynamic/api/Part_Part/query", HttpMethod.POST, queryBody);
        XdmNodeHelper.extractRecords(mapper, existsResult).forEach(item -> {
            String relationId = XdmNodeHelper.text(item, "id", "_id");
            if (StringUtils.hasText(relationId)) {
                ObjectNode deleteBody = mapper.createObjectNode();
                deleteBody.put("id", relationId);
                gateway.proxy("/dynamic/api/Part_Part/delete", HttpMethod.POST, deleteBody);
            }
        });

        int updatedCount = 0;
        for (JsonNode child : children) {
            String childId = XdmNodeHelper.text(child, "childId", "componentId", "id", "target.id", "/target/id");
            if (!StringUtils.hasText(childId)) {
                continue;
            }
            ObjectNode createBody = mapper.createObjectNode();
            createBody.put("source.id", id);
            createBody.put("target.id", childId);
            JsonNode quantityNode = XdmNodeHelper.value(child, "quantity");
            if (!quantityNode.isMissingNode() && !quantityNode.isNull()) {
                createBody.set("quantity", quantityNode);
            }
            gateway.proxy("/dynamic/api/Part_Part/create", HttpMethod.POST, createBody);
            updatedCount++;
        }

        ObjectNode result = mapper.createObjectNode();
        result.put("partId", id);
        result.put("updatedCount", updatedCount);
        return result;
    }

    public JsonNode getCategories() {
        ObjectNode body = mapper.createObjectNode();
        body.set("condition", mapper.createObjectNode());
        JsonNode xdmResult = gateway.proxy("/dynamic/api/PartCategory/query", HttpMethod.POST, body);
        ArrayNode categories = mapper.createArrayNode();
        XdmNodeHelper.extractRecords(mapper, xdmResult).forEach(item -> categories.add(mapCategory(item)));
        return categories;
    }

    public JsonNode createCategory(JsonNode categoryData) {
        ObjectNode body = mapper.createObjectNode();
        mapCategoryToXdm(categoryData, body);
        JsonNode xdmResult = gateway.proxy("/dynamic/api/PartCategory/create", HttpMethod.POST, body);
        JsonNode created = XdmNodeHelper.extractSingle(xdmResult);
        return created.isMissingNode() || created.isNull() || created.isEmpty()
            ? mapCategory(body)
            : mapCategory(created);
    }

    public JsonNode updateCategory(String id, JsonNode categoryData) {
        ObjectNode body = mapper.createObjectNode();
        body.put("id", id);
        mapCategoryToXdm(categoryData, body);
        JsonNode xdmResult = gateway.proxy("/dynamic/api/PartCategory/update", HttpMethod.POST, body);
        JsonNode updated = XdmNodeHelper.extractSingle(xdmResult);
        return updated.isMissingNode() || updated.isNull() || updated.isEmpty()
            ? mapCategory(body)
            : mapCategory(updated);
    }

    public JsonNode deleteCategory(String id) {
        ObjectNode body = mapper.createObjectNode();
        body.put("id", id);
        gateway.proxy("/dynamic/api/PartCategory/delete", HttpMethod.POST, body);
        ObjectNode result = mapper.createObjectNode();
        result.put("id", id);
        result.put("deleted", true);
        return result;
    }

    public JsonNode getVersions(String id) {
        ObjectNode body = mapper.createObjectNode();
        ObjectNode condition = mapper.createObjectNode();
        condition.put("partId", id);
        body.set("condition", condition);
        JsonNode xdmResult = gateway.proxy("/dynamic/api/PartVersion/query", HttpMethod.POST, body);
        ArrayNode versions = mapper.createArrayNode();
        XdmNodeHelper.extractRecords(mapper, xdmResult).forEach(item -> versions.add(mapVersion(item)));
        return versions;
    }

    public JsonNode createVersion(String id, JsonNode versionData) {
        ObjectNode body = mapper.createObjectNode();
        body.put("partId", id);
        copyIfPresent(versionData, body, "version", "versionNo");
        copyIfPresent(versionData, body, "versionNo", "versionNo");
        copyIfPresent(versionData, body, "remark", "remark");
        JsonNode xdmResult = gateway.proxy("/dynamic/api/PartVersion/create", HttpMethod.POST, body);
        JsonNode created = XdmNodeHelper.extractSingle(xdmResult);
        return created.isMissingNode() || created.isNull() || created.isEmpty()
            ? mapVersion(body)
            : mapVersion(created);
    }

    public JsonNode compareVersions(String id, String v1, String v2) {
        ArrayNode versions = (ArrayNode) getVersions(id);
        JsonNode left = findVersion(versions, v1);
        JsonNode right = findVersion(versions, v2);
        if (left.isMissingNode() || right.isMissingNode()) {
            throw new IllegalArgumentException("Version compare requires both versions to exist");
        }

        ArrayNode differences = mapper.createArrayNode();
        compareField(differences, "versionNo", left, right);
        compareField(differences, "partCode", left, right);
        compareField(differences, "partName", left, right);
        compareField(differences, "specModel", left, right);
        compareField(differences, "categoryPath", left, right);
        compareField(differences, "stockQty", left, right);
        compareField(differences, "supplier", left, right);
        compareField(differences, "remark", left, right);

        ObjectNode result = mapper.createObjectNode();
        result.put("partId", id);
        result.put("v1", v1);
        result.put("v2", v2);
        result.set("differences", differences);
        return result;
    }

    private void compareField(ArrayNode differences, String field, JsonNode left, JsonNode right) {
        String leftValue = XdmNodeHelper.text(left, field);
        String rightValue = XdmNodeHelper.text(right, field);
        if (!leftValue.equals(rightValue)) {
            ObjectNode diff = mapper.createObjectNode();
            diff.put("field", field);
            diff.put("left", leftValue);
            diff.put("right", rightValue);
            differences.add(diff);
        }
    }

    private JsonNode findVersion(ArrayNode versions, String versionNo) {
        for (JsonNode version : versions) {
            String current = XdmNodeHelper.text(version, "versionNo", "version");
            if (versionNo.equals(current)) {
                return version;
            }
        }
        return JsonNodeFactory.instance.missingNode();
    }

    private ArrayNode extractChildren(JsonNode bomData) {
        if (bomData != null && bomData.isArray()) {
            return (ArrayNode) bomData;
        }
        if (bomData != null && bomData.path("children").isArray()) {
            return (ArrayNode) bomData.path("children");
        }
        throw new IllegalArgumentException("BOM payload must include children array");
    }

    private ArrayNode loadBomChildren(String sourceId, Set<String> visited) {
        ObjectNode body = mapper.createObjectNode();
        ObjectNode condition = mapper.createObjectNode();
        condition.put("source.id", sourceId);
        body.set("condition", condition);
        JsonNode relationResult = gateway.proxy("/dynamic/api/Part_Part/query", HttpMethod.POST, body);

        ArrayNode children = mapper.createArrayNode();
        for (JsonNode relation : XdmNodeHelper.extractRecords(mapper, relationResult)) {
            String targetId = XdmNodeHelper.text(relation, "target.id", "/target/id");
            if (!StringUtils.hasText(targetId) || visited.contains(targetId)) {
                continue;
            }

            visited.add(targetId);
            ObjectNode child = asObject(getPartById(targetId));
            if (child.isEmpty()) {
                child.put("id", targetId);
            }

            JsonNode quantity = XdmNodeHelper.value(relation, "quantity");
            if (!quantity.isMissingNode() && !quantity.isNull()) {
                child.set("quantity", quantity);
            }
            child.set("children", loadBomChildren(targetId, visited));
            children.add(child);
            visited.remove(targetId);
        }
        return children;
    }

    private void mapPartToXdm(JsonNode source, ObjectNode target) {
        if (source == null || source.isNull()) {
            return;
        }
        copyIfPresent(source, target, "partNo", "partCode");
        copyIfPresent(source, target, "partName", "partName");
        copyIfPresent(source, target, "specification", "specModel");
        copyIfPresent(source, target, "stockQty", "stockQty");
        copyIfPresent(source, target, "supplier", "supplier");
        copyIfPresent(source, target, "categoryName", "categoryPath");
        copyIfPresent(source, target, "version", "versionNo");
        copyIfPresent(source, target, "versionNo", "versionNo");
    }

    private ObjectNode mapPart(JsonNode source) {
        ObjectNode mapped = mapper.createObjectNode();
        putIfNotBlank(mapped, "id", XdmNodeHelper.text(source, "id", "_id"));
        putIfNotBlank(mapped, "partNo", XdmNodeHelper.text(source, "partCode", "partNo"));
        putIfNotBlank(mapped, "partName", XdmNodeHelper.text(source, "partName"));
        putIfNotBlank(mapped, "specification", XdmNodeHelper.text(source, "specModel", "specification"));
        putIfNotBlank(mapped, "stockQty", XdmNodeHelper.text(source, "stockQty"));
        putIfNotBlank(mapped, "supplier", XdmNodeHelper.text(source, "supplier"));
        putIfNotBlank(mapped, "categoryName", XdmNodeHelper.text(source, "categoryPath", "categoryName"));
        putIfNotBlank(mapped, "version", XdmNodeHelper.text(source, "versionNo", "version"));
        return mapped;
    }

    private void mapCategoryToXdm(JsonNode source, ObjectNode target) {
        if (source == null || source.isNull()) {
            return;
        }
        copyIfPresent(source, target, "name", "categoryName");
        copyIfPresent(source, target, "categoryName", "categoryName");
        copyIfPresent(source, target, "parentId", "parentId");
        copyIfPresent(source, target, "path", "categoryPath");
    }

    private ObjectNode mapCategory(JsonNode source) {
        ObjectNode mapped = mapper.createObjectNode();
        putIfNotBlank(mapped, "id", XdmNodeHelper.text(source, "id", "_id"));
        putIfNotBlank(mapped, "name", XdmNodeHelper.text(source, "categoryName", "name"));
        putIfNotBlank(mapped, "parentId", XdmNodeHelper.text(source, "parentId", "parent.id", "/parent/id"));
        putIfNotBlank(mapped, "path", XdmNodeHelper.text(source, "categoryPath", "path"));
        return mapped;
    }

    private ObjectNode mapVersion(JsonNode source) {
        ObjectNode mapped = mapper.createObjectNode();
        putIfNotBlank(mapped, "id", XdmNodeHelper.text(source, "id", "_id"));
        putIfNotBlank(mapped, "partId", XdmNodeHelper.text(source, "partId"));
        putIfNotBlank(mapped, "versionNo", XdmNodeHelper.text(source, "versionNo", "version"));
        putIfNotBlank(mapped, "remark", XdmNodeHelper.text(source, "remark"));
        putIfNotBlank(mapped, "createdAt", XdmNodeHelper.text(source, "createdAt", "createTime"));
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

    private ObjectNode asObject(JsonNode node) {
        if (node != null && node.isObject()) {
            return (ObjectNode) node;
        }
        return mapper.createObjectNode();
    }
}
