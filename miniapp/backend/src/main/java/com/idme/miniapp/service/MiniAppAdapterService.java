package com.idme.miniapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class MiniAppAdapterService {

    private static final String ENTITY_PART = "Part";
    private static final String ENTITY_EQUIPMENT = "Equipment";
    private static final String ENTITY_WORKING_PLAN = "WorkingPlan";
    private static final String ENTITY_WORKING_PROCEDURE = "WorkingProcedure";
    private static final String REL_PART_BOM = "Part_Part";
    private static final String REL_PLAN_PROCEDURE = "WorkingPlan_WorkingProcedure";

    private final XdmRuntimeService xdmRuntimeService;
    private final ObjectMapper objectMapper;
    private final EquipmentProductionDateStore equipmentProductionDateStore;

    private final AtomicLong categoryIdSeed = new AtomicLong(1000);
    private final ConcurrentHashMap<String, CategoryNode> categories = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, VersionMeta>> versionMetas = new ConcurrentHashMap<>();

    public MiniAppAdapterService(
        XdmRuntimeService xdmRuntimeService,
        ObjectMapper objectMapper,
        EquipmentProductionDateStore equipmentProductionDateStore
    ) {
        this.xdmRuntimeService = xdmRuntimeService;
        this.objectMapper = objectMapper;
        this.equipmentProductionDateStore = equipmentProductionDateStore;
    }

    public Map<String, Object> listParts(String keyword, String categoryId, int page, int size) {
        List<Map<String, Object>> all = loadAllPartsMapped();
        List<Map<String, Object>> filtered = all.stream()
            .filter(item -> matchesKeyword(item, keyword, "partNo", "partName"))
            .filter(item -> !StringUtils.hasText(categoryId) || categoryId.equals(asString(item.get("categoryId"))))
            .collect(Collectors.toList());

        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int fromIndex = Math.min((safePage - 1) * safeSize, filtered.size());
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("records", filtered.subList(fromIndex, toIndex));
        data.put("total", filtered.size());
        data.put("size", safeSize);
        data.put("current", safePage);
        data.put("pages", filtered.isEmpty() ? 0 : (int) Math.ceil(filtered.size() / (double) safeSize));
        return data;
    }

    public Map<String, Object> getPart(String id) {
        return mapPart(xdmRuntimeService.get(ENTITY_PART, id));
    }

    public Map<String, Object> createPart(Map<String, Object> payload) {
        return mapPart(xdmRuntimeService.create(ENTITY_PART, buildPartPayload(payload, null)));
    }

    public Map<String, Object> updatePart(String id, Map<String, Object> payload) {
        return mapPart(xdmRuntimeService.update(ENTITY_PART, buildPartPayload(payload, id)));
    }

    public void deletePart(String id) {
        xdmRuntimeService.delete(ENTITY_PART, id);
    }

    public List<Map<String, Object>> getPartBom(String rootPartId) {
        List<JsonNode> allRelations = xdmRuntimeService.listRelation(REL_PART_BOM);
        Map<String, List<JsonNode>> grouped = allRelations.stream()
            .filter(rel -> rootPartId.equals(rel.path("source").path("id").asText()))
            .collect(Collectors.groupingBy(rel -> rel.path("source").path("id").asText()));

        Map<String, JsonNode> partIndex = xdmRuntimeService.list(ENTITY_PART).stream()
            .collect(Collectors.toMap(node -> node.path("id").asText(), node -> node, (a, b) -> a));
        return buildBomChildren(rootPartId, grouped, partIndex, new LinkedHashSet<>());
    }

    public void updatePartBom(String rootPartId, List<Map<String, Object>> bomTree) {
        List<BomEdge> edges = new ArrayList<>();
        Set<String> parentIds = new LinkedHashSet<>();
        flattenBomEdges(rootPartId, bomTree, edges, parentIds);

        List<JsonNode> existing = xdmRuntimeService.listRelation(REL_PART_BOM).stream()
            .filter(rel -> parentIds.contains(rel.path("source").path("id").asText()))
            .collect(Collectors.toList());
        for (JsonNode rel : existing) {
            xdmRuntimeService.deleteRelation(REL_PART_BOM, rel.path("id").asText());
        }

        for (BomEdge edge : edges) {
            Map<String, Object> relation = new LinkedHashMap<>();
            relation.put("source", Map.of("id", edge.sourceId));
            relation.put("target", Map.of("id", edge.targetId));
            relation.put("name", "BOM-" + edge.sourceId + "-" + edge.targetId);
            relation.put("description", toJson(Map.of("quantity", edge.quantity)));
            xdmRuntimeService.createRelation(REL_PART_BOM, relation);
        }
    }

    public List<Map<String, Object>> listCategories() {
        seedCategoriesFromParts();
        return buildCategoryTree();
    }

    public Map<String, Object> createCategory(Map<String, Object> body) {
        String name = asString(body.get("name"));
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        String parentId = asString(body.get("parentId"));
        CategoryNode node = new CategoryNode(String.valueOf(categoryIdSeed.incrementAndGet()), name.trim(), parentId);
        categories.put(node.id, node);
        return toCategoryMap(node);
    }

    public Map<String, Object> updateCategory(String id, Map<String, Object> body) {
        CategoryNode current = categories.get(id);
        if (current == null) {
            throw new IllegalArgumentException("分类不存在: " + id);
        }
        String newName = asString(body.get("name"));
        if (StringUtils.hasText(newName)) {
            current.name = newName.trim();
        }
        return toCategoryMap(current);
    }

    public void deleteCategory(String id) {
        if (!categories.containsKey(id)) {
            return;
        }
        Deque<String> queue = new ArrayDeque<>();
        queue.add(id);
        while (!queue.isEmpty()) {
            String currentId = queue.removeFirst();
            categories.values().stream()
                .filter(node -> Objects.equals(currentId, node.parentId))
                .map(node -> node.id)
                .forEach(queue::addLast);
            categories.remove(currentId);
        }
    }

    public List<Map<String, Object>> listPartVersions(String id) {
        JsonNode current = xdmRuntimeService.get(ENTITY_PART, id);
        String partCode = current.path("partCode").asText();
        if (!StringUtils.hasText(partCode)) {
            return List.of();
        }

        List<JsonNode> sameCode = xdmRuntimeService.list(ENTITY_PART).stream()
            .filter(node -> partCode.equals(node.path("partCode").asText()))
            .sorted(Comparator.comparing(node -> node.path("createTime").asText(""), Comparator.reverseOrder()))
            .collect(Collectors.toList());

        Map<String, JsonNode> latestByVersion = new LinkedHashMap<>();
        for (JsonNode node : sameCode) {
            String version = normalizeVersion(node.path("versionNo").asText());
            latestByVersion.putIfAbsent(version, node);
        }

        String publishedVersion = normalizeVersion(current.path("versionNo").asText());
        ConcurrentHashMap<String, VersionMeta> metaMap = versionMetas.computeIfAbsent(partCode, k -> new ConcurrentHashMap<>());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, JsonNode> entry : latestByVersion.entrySet()) {
            String version = entry.getKey();
            JsonNode node = entry.getValue();
            VersionMeta meta = metaMap.get(version);

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("version", version);
            row.put("description", meta != null && StringUtils.hasText(meta.description) ? meta.description : "版本 " + version);
            row.put("status", meta != null && StringUtils.hasText(meta.status) ? meta.status
                : (version.equals(publishedVersion) ? "published" : "draft"));
            row.put("createdBy", node.path("creator").asText(""));
            row.put("createdAt", node.path("createTime").asText(""));
            row.put("sourceId", node.path("id").asText(""));
            result.add(row);
        }
        return result;
    }

    public Map<String, Object> createPartVersion(String id, Map<String, Object> body) {
        JsonNode current = xdmRuntimeService.get(ENTITY_PART, id);
        String partCode = current.path("partCode").asText();
        String newVersion = nextVersion(listPartVersions(id).stream()
            .map(v -> asString(v.get("version")))
            .collect(Collectors.toList()));

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("partCode", partCode);
        payload.put("partName", current.path("partName").asText(""));
        payload.put("specModel", current.path("specModel").asText(""));
        payload.put("stockQty", asLong(current.path("stockQty")));
        payload.put("supplier", current.path("supplier").asText(""));
        payload.put("categoryPath", current.path("categoryPath").asText(null));
        payload.put("versionNo", newVersion);

        JsonNode created = xdmRuntimeService.create(ENTITY_PART, payload);
        versionMetas.computeIfAbsent(partCode, k -> new ConcurrentHashMap<>())
            .put(newVersion, new VersionMeta(asString(body.get("description")), "draft"));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("version", newVersion);
        result.put("description", asString(body.get("description")));
        result.put("status", "draft");
        result.put("createdBy", created.path("creator").asText(""));
        result.put("createdAt", created.path("createTime").asText(""));
        return result;
    }

    public Map<String, Object> comparePartVersions(String id, String v1, String v2) {
        JsonNode current = xdmRuntimeService.get(ENTITY_PART, id);
        String partCode = current.path("partCode").asText();
        List<JsonNode> sameCode = xdmRuntimeService.list(ENTITY_PART).stream()
            .filter(node -> partCode.equals(node.path("partCode").asText()))
            .collect(Collectors.toList());

        JsonNode version1 = findPartByVersion(sameCode, v1);
        JsonNode version2 = findPartByVersion(sameCode, v2);
        if (version1 == null || version2 == null) {
            throw new IllegalArgumentException("版本不存在: " + v1 + " 或 " + v2);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("v1", mapPartSnapshot(version1));
        result.put("v2", mapPartSnapshot(version2));
        return result;
    }

    public void rollbackPartVersion(String id, String version) {
        JsonNode current = xdmRuntimeService.get(ENTITY_PART, id);
        String partCode = current.path("partCode").asText();
        List<JsonNode> sameCode = xdmRuntimeService.list(ENTITY_PART).stream()
            .filter(node -> partCode.equals(node.path("partCode").asText()))
            .collect(Collectors.toList());
        JsonNode target = findPartByVersion(sameCode, version);
        if (target == null) {
            throw new IllegalArgumentException("版本不存在: " + version);
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", id);
        payload.put("partCode", target.path("partCode").asText(""));
        payload.put("partName", target.path("partName").asText(""));
        payload.put("specModel", target.path("specModel").asText(""));
        payload.put("stockQty", asLong(target.path("stockQty")));
        payload.put("supplier", target.path("supplier").asText(""));
        payload.put("categoryPath", target.path("categoryPath").asText(null));
        payload.put("versionNo", normalizeVersion(target.path("versionNo").asText()));
        xdmRuntimeService.update(ENTITY_PART, payload);
    }

    public void updatePartVersionStatus(String id, String version, String status) {
        JsonNode current = xdmRuntimeService.get(ENTITY_PART, id);
        String partCode = current.path("partCode").asText();
        ConcurrentHashMap<String, VersionMeta> metaMap = versionMetas.computeIfAbsent(partCode, k -> new ConcurrentHashMap<>());
        VersionMeta currentMeta = metaMap.computeIfAbsent(version, k -> new VersionMeta("", "draft"));
        currentMeta.status = StringUtils.hasText(status) ? status : "draft";
        if ("published".equalsIgnoreCase(currentMeta.status)) {
            metaMap.forEach((k, v) -> {
                if (!k.equals(version)) {
                    v.status = "draft";
                }
            });
        }
    }

    public List<Map<String, Object>> listEquipments(String keyword) {
        return xdmRuntimeService.list(ENTITY_EQUIPMENT).stream()
            .map(this::mapEquipment)
            .filter(item -> matchesKeyword(item, keyword,
                "equipmentCode", "equipmentName", "code", "name",
                "manufacturer", "brand", "specModel", "model", "supplier", "location"))
            .collect(Collectors.toList());
    }

    public Map<String, Object> getEquipment(String id) {
        return mapEquipment(xdmRuntimeService.get(ENTITY_EQUIPMENT, id));
    }

    public Map<String, Object> createEquipment(Map<String, Object> body) {
        Map<String, Object> payload = buildEquipmentPayload(body, null);
        try {
            Map<String, Object> item = mapEquipment(xdmRuntimeService.create(ENTITY_EQUIPMENT, payload));
            overrideProductionDateFromRequest(item, body);
            return item;
        } catch (IllegalStateException ex) {
            if (shouldRetryWithoutProductionDate(payload, ex)) {
                Map<String, Object> fallbackPayload = new LinkedHashMap<>(payload);
                fallbackPayload.remove("productionDate");
                Map<String, Object> item = mapEquipment(xdmRuntimeService.create(ENTITY_EQUIPMENT, fallbackPayload));
                overrideProductionDateFromRequest(item, body);
                return item;
            }
            throw ex;
        }
    }

    public Map<String, Object> updateEquipment(String id, Map<String, Object> body) {
        Map<String, Object> payload = buildEquipmentPayload(body, id);
        try {
            Map<String, Object> item = mapEquipment(xdmRuntimeService.update(ENTITY_EQUIPMENT, payload));
            overrideProductionDateFromRequest(item, body);
            return item;
        } catch (IllegalStateException ex) {
            if (shouldRetryWithoutProductionDate(payload, ex)) {
                Map<String, Object> fallbackPayload = new LinkedHashMap<>(payload);
                fallbackPayload.remove("productionDate");
                Map<String, Object> item = mapEquipment(xdmRuntimeService.update(ENTITY_EQUIPMENT, fallbackPayload));
                overrideProductionDateFromRequest(item, body);
                return item;
            }
            throw ex;
        }
    }

    public void deleteEquipment(String id) {
        xdmRuntimeService.delete(ENTITY_EQUIPMENT, id);
        equipmentProductionDateStore.remove(id);
    }

    public List<Map<String, Object>> listWorkingPlans(String keyword) {
        return xdmRuntimeService.list(ENTITY_WORKING_PLAN).stream()
            .map(this::mapWorkingPlan)
            .filter(item -> matchesKeyword(item, keyword, "code", "name"))
            .collect(Collectors.toList());
    }

    public Map<String, Object> getWorkingPlan(String id) {
        return mapWorkingPlan(xdmRuntimeService.get(ENTITY_WORKING_PLAN, id));
    }

    public Map<String, Object> createWorkingPlan(Map<String, Object> body) {
        return mapWorkingPlan(xdmRuntimeService.create(ENTITY_WORKING_PLAN, buildWorkingPlanPayload(body, null)));
    }

    public Map<String, Object> updateWorkingPlan(String id, Map<String, Object> body) {
        return mapWorkingPlan(xdmRuntimeService.update(ENTITY_WORKING_PLAN, buildWorkingPlanPayload(body, id)));
    }

    public void deleteWorkingPlan(String id) {
        xdmRuntimeService.delete(ENTITY_WORKING_PLAN, id);
    }

    public List<Map<String, Object>> getWorkingPlanProcesses(String workingPlanId) {
        List<JsonNode> relations = xdmRuntimeService.listRelation(REL_PLAN_PROCEDURE).stream()
            .filter(rel -> workingPlanId.equals(rel.path("source").path("id").asText()))
            .sorted(Comparator.comparingInt(this::extractRelationOrder))
            .collect(Collectors.toList());

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < relations.size(); i++) {
            result.add(mapProcess(relations.get(i), i + 1));
        }
        return result;
    }

    public List<Map<String, Object>> updateWorkingPlanProcesses(String workingPlanId, List<Map<String, Object>> processes) {
        List<JsonNode> existing = xdmRuntimeService.listRelation(REL_PLAN_PROCEDURE).stream()
            .filter(rel -> workingPlanId.equals(rel.path("source").path("id").asText()))
            .collect(Collectors.toList());
        for (JsonNode rel : existing) {
            xdmRuntimeService.deleteRelation(REL_PLAN_PROCEDURE, rel.path("id").asText());
        }

        for (int i = 0; i < processes.size(); i++) {
            Map<String, Object> process = processes.get(i);
            String processId = asString(process.get("id"));
            JsonNode procedure = (StringUtils.hasText(processId) && existsProcedure(processId))
                ? xdmRuntimeService.get(ENTITY_WORKING_PROCEDURE, processId)
                : createProcedureFromProcess(process, i + 1);

            Map<String, Object> relation = new LinkedHashMap<>();
            relation.put("source", Map.of("id", workingPlanId));
            relation.put("target", Map.of("id", procedure.path("id").asText()));
            relation.put("name", "PLAN_PROC_" + (i + 1));
            relation.put("description", toJson(Map.of(
                "order", i + 1,
                "location", asString(process.get("location")),
                "status", normalizeStatus(asString(process.get("status")))
            )));
            xdmRuntimeService.createRelation(REL_PLAN_PROCEDURE, relation);
        }
        return getWorkingPlanProcesses(workingPlanId);
    }

    public List<Map<String, Object>> listProcedures() {
        List<Map<String, Object>> data = xdmRuntimeService.list(ENTITY_WORKING_PROCEDURE).stream()
            .map(this::mapProcedure)
            .collect(Collectors.toList());
        return data.isEmpty() ? defaultProcedureList() : data;
    }

    public Map<String, Object> updateProcedure(String id, Map<String, Object> body) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", id);
        putIfText(payload, "procedureName", firstText(body, "procedureName", "name"));
        putIfText(payload, "procedureCode", firstText(body, "procedureCode", "code"));
        putIfText(payload, "productionStep", firstText(body, "productionStep", "description"));
        putIfText(payload, "operatorName", firstText(body, "operatorName", "operator"));
        return mapProcedure(xdmRuntimeService.update(ENTITY_WORKING_PROCEDURE, payload));
    }

    private List<Map<String, Object>> loadAllPartsMapped() {
        List<Map<String, Object>> all = xdmRuntimeService.list(ENTITY_PART).stream()
            .map(this::mapPart)
            .collect(Collectors.toList());
        seedCategories(all);
        return all;
    }

    private Map<String, Object> buildPartPayload(Map<String, Object> body, String id) {
        Map<String, Object> payload = new LinkedHashMap<>();
        if (StringUtils.hasText(id)) {
            payload.put("id", id);
        }
        putIfText(payload, "partCode", firstText(body, "partCode", "partNo"));
        putIfText(payload, "partName", firstText(body, "partName"));
        putIfText(payload, "specModel", firstText(body, "specModel", "specification"));
        putIfText(payload, "supplier", firstText(body, "supplier"));
        payload.put("stockQty", asLong(body.get("stockQty")));
        String version = firstText(body, "versionNo", "version");
        payload.put("versionNo", StringUtils.hasText(version) ? version : "V1.0");
        String categoryPath = resolveCategoryPath(body);
        if (StringUtils.hasText(categoryPath)) {
            payload.put("categoryPath", categoryPath);
        }
        return payload;
    }

    private Map<String, Object> buildEquipmentPayload(Map<String, Object> body, String id) {
        Map<String, Object> payload = new LinkedHashMap<>();
        boolean isUpdate = StringUtils.hasText(id);
        if (StringUtils.hasText(id)) {
            payload.put("id", id);
        }
        putIfText(payload, "equipmentCode", firstText(body, "equipmentCode", "code"));
        putIfText(payload, "equipmentName", firstText(body, "equipmentName", "name"));
        putIfText(payload, "manufacturer", firstText(body, "manufacturer"));
        putTextWithNullSupport(payload, body, isUpdate, "specModel", "specModel", "model");
        putTextWithNullSupport(payload, body, isUpdate, "brand", "brand");
        putTextWithNullSupport(payload, body, isUpdate, "supplier", "supplier");
        putTextWithNullSupport(payload, body, isUpdate, "location", "location");
        putTextWithNullSupport(payload, body, isUpdate, "depreciationMethod", "depreciationMethod", "depreciation");
        putTextWithNullSupport(payload, body, isUpdate, "technicalParams", "technicalParams");
        putTextWithNullSupport(payload, body, isUpdate, "sparePartsInfo", "sparePartsInfo", "spareParts");

        String productionDate = firstText(body, "productionDate");
        if (StringUtils.hasText(productionDate)) {
            payload.put("productionDate", productionDate);
        }

        Object serviceLife = firstValue(body, "serviceLifeYears", "serviceLife");
        if (serviceLife != null) {
            payload.put("serviceLifeYears", asLong(serviceLife));
        }
        return payload;
    }

    private Map<String, Object> buildWorkingPlanPayload(Map<String, Object> body, String id) {
        Map<String, Object> payload = new LinkedHashMap<>();
        if (StringUtils.hasText(id)) {
            payload.put("id", id);
        }
        putIfText(payload, "planCode", firstText(body, "planCode", "code"));
        putIfText(payload, "planName", firstText(body, "planName", "name"));
        putIfText(payload, "versionNo", firstText(body, "versionNo", "version"));
        putIfText(payload, "belongProduct", firstText(body, "belongProduct", "product"));
        putIfText(payload, "planDescription", firstText(body, "planDescription", "description"));
        putIfText(payload, "operatorName", firstText(body, "operatorName", "operator"));
        putIfText(payload, "equipmentUsage", firstText(body, "equipmentUsage", "equipment"));
        putIfText(payload, "operationTime", firstText(body, "operationTime"));
        return payload;
    }

    private Map<String, Object> mapPart(JsonNode node) {
        String categoryPath = nullIfBlank(node.path("categoryPath").asText(null));
        String categoryName = extractCategoryName(categoryPath);
        String categoryId = findCategoryIdByName(categoryName);

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", node.path("id").asText(""));
        item.put("partNo", firstText(node, "partCode", "partNo"));
        item.put("partName", firstText(node, "partName"));
        item.put("specification", firstText(node, "specModel", "specification"));
        item.put("stockQty", asLong(node.path("stockQty")));
        item.put("supplier", firstText(node, "supplier"));
        item.put("categoryName", categoryName);
        item.put("categoryId", categoryId);
        item.put("version", normalizeVersion(node.path("versionNo").asText()));
        item.put("createdBy", node.path("creator").asText(""));
        item.put("createdAt", node.path("createTime").asText(""));
        return item;
    }

    private Map<String, Object> mapEquipment(JsonNode node) {
        String id = node.path("id").asText("");
        String equipmentCode = firstText(node, "equipmentCode", "code");
        String equipmentName = firstText(node, "equipmentName", "name");
        String specModel = firstText(node, "specModel", "model");
        long serviceLifeYears = asLong(node.path("serviceLifeYears"));
        String depreciationMethod = firstText(node, "depreciationMethod");
        String sparePartsInfo = firstText(node, "sparePartsInfo");
        String productionDate = firstText(node, "productionDate");
        if (StringUtils.hasText(id)) {
            if (StringUtils.hasText(productionDate)) {
                equipmentProductionDateStore.mergeFromXdm(id, productionDate);
            } else {
                productionDate = equipmentProductionDateStore.get(id);
            }
        }

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", id);
        item.put("equipmentCode", equipmentCode);
        item.put("equipmentName", equipmentName);
        item.put("manufacturer", firstText(node, "manufacturer"));
        item.put("brand", firstText(node, "brand"));
        item.put("specModel", specModel);
        item.put("supplier", firstText(node, "supplier"));
        item.put("productionDate", productionDate);
        item.put("serviceLifeYears", serviceLifeYears);
        item.put("depreciationMethod", depreciationMethod);
        item.put("location", firstText(node, "location"));
        item.put("technicalParams", firstText(node, "technicalParams"));
        item.put("sparePartsInfo", sparePartsInfo);
        item.put("status", firstText(node, "status"));

        // 历史字段兼容，避免影响已接入页面。
        item.put("code", equipmentCode);
        item.put("name", equipmentName);
        item.put("model", specModel);
        item.put("serviceLife", serviceLifeYears);
        item.put("depreciation", depreciationMethod);
        item.put("spareParts", sparePartsInfo);

        if (!StringUtils.hasText(asString(item.get("status")))) {
            item.put("status", "idle");
        }
        return item;
    }

    private boolean shouldRetryWithoutProductionDate(Map<String, Object> payload, IllegalStateException ex) {
        if (!payload.containsKey("productionDate")) {
            return false;
        }
        String message = ex.getMessage();
        if (!StringUtils.hasText(message)) {
            return false;
        }
        String lower = message.toLowerCase(Locale.ROOT);
        return lower.contains("productiondate") || lower.contains("params.productiondate");
    }

    private void overrideProductionDateFromRequest(Map<String, Object> item, Map<String, Object> body) {
        if (!hasAnyField(body, "productionDate")) {
            return;
        }
        String id = asString(item.get("id"));
        if (!StringUtils.hasText(id)) {
            return;
        }
        String productionDate = firstText(body, "productionDate");
        if (StringUtils.hasText(productionDate)) {
            equipmentProductionDateStore.put(id, productionDate);
            item.put("productionDate", productionDate);
        } else {
            equipmentProductionDateStore.remove(id);
            item.put("productionDate", "");
        }
    }

    private Map<String, Object> mapWorkingPlan(JsonNode node) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", node.path("id").asText(""));
        item.put("code", firstText(node, "planCode", "code"));
        item.put("name", firstText(node, "planName", "name"));
        item.put("version", normalizeVersion(node.path("versionNo").asText()));
        item.put("product", firstText(node, "belongProduct", "product"));
        item.put("description", firstText(node, "planDescription", "description"));
        item.put("operator", firstText(node, "operatorName", "operator"));
        item.put("equipment", firstText(node, "equipmentUsage", "equipment"));
        item.put("operationTime", firstText(node, "operationTime"));
        return item;
    }

    private Map<String, Object> mapProcedure(JsonNode node) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", node.path("id").asText(""));
        item.put("name", firstText(node, "procedureName", "name"));
        item.put("procedureName", firstText(node, "procedureName", "name"));
        item.put("procedureCode", firstText(node, "procedureCode", "code"));
        item.put("description", firstText(node, "productionStep", "description"));
        item.put("standardTime", null);
        item.put("order", 0);
        return item;
    }

    private Map<String, Object> mapProcess(JsonNode relation, int fallbackOrder) {
        JsonNode target = relation.path("target");
        String targetId = target.path("id").asText();
        JsonNode procedure = target;
        if (!target.has("procedureName") && StringUtils.hasText(targetId)) {
            try {
                procedure = xdmRuntimeService.get(ENTITY_WORKING_PROCEDURE, targetId);
            } catch (Exception ignored) {
            }
        }

        Map<String, Object> descMeta = parseJsonObject(relation.path("description").asText(""));
        int order = asInt(descMeta.get("order"), fallbackOrder);
        String location = firstNonBlank(
            asString(descMeta.get("location")),
            firstText(procedure, "productionStep", "location"),
            ""
        );
        String status = normalizeStatus(firstNonBlank(asString(descMeta.get("status")), "pending"));

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", procedure.path("id").asText(targetId));
        item.put("num", String.format(Locale.ROOT, "%02d", order));
        item.put("name", firstText(procedure, "procedureName", "name"));
        item.put("location", location);
        item.put("status", status);
        return item;
    }

    private void flattenBomEdges(
        String parentId,
        List<Map<String, Object>> nodes,
        List<BomEdge> edges,
        Set<String> parentIds
    ) {
        parentIds.add(parentId);
        for (Map<String, Object> node : nodes) {
            String childId = asString(node.get("id"));
            if (!StringUtils.hasText(childId)) {
                childId = asString(node.get("childPartId"));
            }
            if (!StringUtils.hasText(childId)) {
                continue;
            }
            int quantity = asInt(firstValue(node, "quantity"), 1);
            edges.add(new BomEdge(parentId, childId, quantity));
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> children = (List<Map<String, Object>>) node.get("children");
            if (children != null && !children.isEmpty()) {
                flattenBomEdges(childId, children, edges, parentIds);
            }
        }
    }

    private List<Map<String, Object>> buildBomChildren(
        String sourceId,
        Map<String, List<JsonNode>> relationIndex,
        Map<String, JsonNode> partIndex,
        Set<String> visiting
    ) {
        if (!visiting.add(sourceId)) {
            return List.of();
        }
        List<JsonNode> relations = relationIndex.getOrDefault(sourceId, List.of());
        List<Map<String, Object>> children = new ArrayList<>();
        for (JsonNode relation : relations) {
            JsonNode target = relation.path("target");
            String childId = target.path("id").asText();
            JsonNode childPart = partIndex.getOrDefault(childId, target);
            Map<String, Object> child = new LinkedHashMap<>();
            child.put("id", childId);
            child.put("partNo", firstText(childPart, "partCode", "partNo"));
            child.put("partName", firstText(childPart, "partName", "name"));
            child.put("version", normalizeVersion(childPart.path("versionNo").asText()));
            child.put("quantity", extractBomQuantity(relation));
            child.put("children", buildBomChildren(childId, relationIndex, partIndex, visiting));
            children.add(child);
        }
        visiting.remove(sourceId);
        return children;
    }

    private int extractBomQuantity(JsonNode relation) {
        Map<String, Object> meta = parseJsonObject(relation.path("description").asText(""));
        return asInt(meta.get("quantity"), 1);
    }

    private Map<String, Object> mapPartSnapshot(JsonNode node) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("version", normalizeVersion(node.path("versionNo").asText()));
        item.put("partName", firstText(node, "partName"));
        item.put("specification", firstText(node, "specModel", "specification"));
        item.put("stockQty", asLong(node.path("stockQty")));
        item.put("supplier", firstText(node, "supplier"));
        return item;
    }

    private JsonNode findPartByVersion(List<JsonNode> parts, String version) {
        String normalized = normalizeVersion(version);
        for (JsonNode part : parts) {
            if (normalized.equals(normalizeVersion(part.path("versionNo").asText()))) {
                return part;
            }
        }
        return null;
    }

    private void seedCategoriesFromParts() {
        List<Map<String, Object>> parts = xdmRuntimeService.list(ENTITY_PART).stream()
            .map(this::mapPart)
            .collect(Collectors.toList());
        seedCategories(parts);
    }

    private synchronized void seedCategories(List<Map<String, Object>> parts) {
        for (Map<String, Object> part : parts) {
            String categoryName = asString(part.get("categoryName"));
            if (!StringUtils.hasText(categoryName)) {
                continue;
            }
            if (!hasCategoryName(categoryName.trim())) {
                CategoryNode node = new CategoryNode(String.valueOf(categoryIdSeed.incrementAndGet()), categoryName.trim(), null);
                categories.put(node.id, node);
            }
        }
    }

    private boolean hasCategoryName(String categoryName) {
        return categories.values().stream().anyMatch(node -> categoryName.equals(node.name));
    }

    private String resolveCategoryPath(Map<String, Object> body) {
        String categoryId = asString(body.get("categoryId"));
        if (StringUtils.hasText(categoryId)) {
            CategoryNode node = categories.get(categoryId);
            if (node != null) {
                return buildCategoryPath(node);
            }
        }
        return firstText(body, "categoryPath", "categoryName");
    }

    private String buildCategoryPath(CategoryNode node) {
        List<String> path = new ArrayList<>();
        CategoryNode cursor = node;
        while (cursor != null) {
            path.add(cursor.name);
            cursor = StringUtils.hasText(cursor.parentId) ? categories.get(cursor.parentId) : null;
        }
        java.util.Collections.reverse(path);
        return String.join("/", path);
    }

    private List<Map<String, Object>> buildCategoryTree() {
        Map<String, List<CategoryNode>> childrenIndex = new HashMap<>();
        for (CategoryNode node : categories.values()) {
            childrenIndex.computeIfAbsent(node.parentId, key -> new ArrayList<>()).add(node);
        }
        childrenIndex.values().forEach(list -> list.sort(Comparator.comparing(n -> n.id)));
        return buildCategoryChildren(null, childrenIndex);
    }

    private List<Map<String, Object>> buildCategoryChildren(String parentId, Map<String, List<CategoryNode>> childrenIndex) {
        List<CategoryNode> nodes = childrenIndex.getOrDefault(parentId, List.of());
        List<Map<String, Object>> result = new ArrayList<>();
        for (CategoryNode node : nodes) {
            Map<String, Object> item = toCategoryMap(node);
            item.put("children", buildCategoryChildren(node.id, childrenIndex));
            result.add(item);
        }
        return result;
    }

    private Map<String, Object> toCategoryMap(CategoryNode node) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", node.id);
        item.put("name", node.name);
        item.put("parentId", node.parentId);
        return item;
    }

    private String findCategoryIdByName(String categoryName) {
        if (!StringUtils.hasText(categoryName)) {
            return null;
        }
        return categories.values().stream()
            .filter(node -> categoryName.equals(node.name))
            .map(node -> node.id)
            .findFirst()
            .orElse(null);
    }

    private String extractCategoryName(String categoryPath) {
        if (!StringUtils.hasText(categoryPath)) {
            return null;
        }
        int idx = categoryPath.lastIndexOf('/');
        return idx >= 0 ? categoryPath.substring(idx + 1) : categoryPath;
    }

    private String normalizeVersion(String value) {
        if (!StringUtils.hasText(value)) {
            return "V1.0";
        }
        return value;
    }

    private String nextVersion(List<String> versions) {
        int max = 0;
        for (String version : versions) {
            if (!StringUtils.hasText(version)) {
                continue;
            }
            String candidate = version.trim().toUpperCase(Locale.ROOT);
            if (candidate.startsWith("V")) {
                candidate = candidate.substring(1);
            }
            int dot = candidate.indexOf('.');
            if (dot > 0) {
                candidate = candidate.substring(0, dot);
            }
            try {
                max = Math.max(max, Integer.parseInt(candidate));
            } catch (NumberFormatException ignored) {
            }
        }
        return "V" + (max + 1) + ".0";
    }

    private int extractRelationOrder(JsonNode relation) {
        Map<String, Object> meta = parseJsonObject(relation.path("description").asText(""));
        return asInt(meta.get("order"), Integer.MAX_VALUE);
    }

    private boolean existsProcedure(String id) {
        try {
            xdmRuntimeService.get(ENTITY_WORKING_PROCEDURE, id);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private JsonNode createProcedureFromProcess(Map<String, Object> process, int order) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("procedureName", firstText(process, "procedureName", "name"));
        String code = firstText(process, "procedureCode", "code");
        if (!StringUtils.hasText(code)) {
            code = "PROC-" + Instant.now().toEpochMilli() + "-" + order;
        }
        payload.put("procedureCode", code);
        payload.put("productionStep", firstText(process, "location", "productionStep"));
        payload.put("operatorName", firstText(process, "operator", "operatorName"));
        return xdmRuntimeService.create(ENTITY_WORKING_PROCEDURE, payload);
    }

    private List<Map<String, Object>> defaultProcedureList() {
        List<Map<String, Object>> defaults = new ArrayList<>();
        String[] names = new String[]{"毛坯制造", "粗加工", "精加工", "检测", "入库"};
        for (int i = 0; i < names.length; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", "default-" + (i + 1));
            row.put("name", names[i]);
            row.put("procedureName", names[i]);
            row.put("procedureCode", "PROC-" + String.format(Locale.ROOT, "%02d", i + 1));
            row.put("description", "");
            row.put("standardTime", null);
            row.put("order", i + 1);
            defaults.add(row);
        }
        return defaults;
    }

    private String firstText(JsonNode node, String... fields) {
        for (String field : fields) {
            String val = nullIfBlank(node.path(field).asText(null));
            if (StringUtils.hasText(val)) {
                return val;
            }
        }
        return "";
    }

    private String firstText(Map<String, Object> body, String... fields) {
        for (String field : fields) {
            String val = asString(body.get(field));
            if (StringUtils.hasText(val)) {
                return val;
            }
        }
        return null;
    }

    private Object firstValue(Map<String, Object> body, String... fields) {
        for (String field : fields) {
            if (body.containsKey(field) && body.get(field) != null) {
                return body.get(field);
            }
        }
        return null;
    }

    private boolean matchesKeyword(Map<String, Object> item, String keyword, String... fields) {
        if (!StringUtils.hasText(keyword)) {
            return true;
        }
        String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
        for (String field : fields) {
            String value = asString(item.get(field));
            if (StringUtils.hasText(value) && value.toLowerCase(Locale.ROOT).contains(lowerKeyword)) {
                return true;
            }
        }
        return false;
    }

    private void putIfText(Map<String, Object> map, String key, String value) {
        if (StringUtils.hasText(value)) {
            map.put(key, value);
        }
    }

    private void putTextWithNullSupport(
        Map<String, Object> payload,
        Map<String, Object> body,
        boolean isUpdate,
        String targetField,
        String... sourceFields
    ) {
        String value = firstText(body, sourceFields);
        if (StringUtils.hasText(value)) {
            payload.put(targetField, value);
            return;
        }
        if (isUpdate && hasAnyField(body, sourceFields)) {
            addNeedSetNullAttr(payload, targetField);
        }
    }

    private boolean hasAnyField(Map<String, Object> body, String... fields) {
        for (String field : fields) {
            if (body.containsKey(field)) {
                return true;
            }
        }
        return false;
    }

    private void addNeedSetNullAttr(Map<String, Object> payload, String fieldName) {
        @SuppressWarnings("unchecked")
        List<String> attrs = (List<String>) payload.computeIfAbsent("needSetNullAttrs", key -> new ArrayList<String>());
        if (!attrs.contains(fieldName)) {
            attrs.add(fieldName);
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            throw new IllegalStateException("JSON 序列化失败", ex);
        }
    }

    private Map<String, Object> parseJsonObject(String raw) {
        if (!StringUtils.hasText(raw)) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(raw, new TypeReference<>() {
            });
        } catch (Exception ex) {
            return Map.of();
        }
    }

    private String normalizeStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return "pending";
        }
        String s = status.toLowerCase(Locale.ROOT);
        if (List.of("active", "pending", "checking", "completed").contains(s)) {
            return s;
        }
        return "pending";
    }

    private String asString(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : text;
    }

    private long asLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value).trim());
        } catch (Exception ex) {
            return 0L;
        }
    }

    private long asLong(JsonNode value) {
        if (value == null || value.isMissingNode() || value.isNull()) {
            return 0L;
        }
        if (value.isNumber()) {
            return value.asLong();
        }
        try {
            return Long.parseLong(value.asText("0").trim());
        } catch (Exception ex) {
            return 0L;
        }
    }

    private int asInt(Object value, int fallback) {
        if (value == null) {
            return fallback;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value).trim());
        } catch (Exception ex) {
            return fallback;
        }
    }

    private String nullIfBlank(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return "";
    }

    private static final class CategoryNode {
        private final String id;
        private final String parentId;
        private volatile String name;

        private CategoryNode(String id, String name, String parentId) {
            this.id = id;
            this.name = name;
            this.parentId = parentId;
        }
    }

    private static final class VersionMeta {
        private volatile String description;
        private volatile String status;

        private VersionMeta(String description, String status) {
            this.description = description;
            this.status = status;
        }
    }

    private static final class BomEdge {
        private final String sourceId;
        private final String targetId;
        private final int quantity;

        private BomEdge(String sourceId, String targetId, int quantity) {
            this.sourceId = sourceId;
            this.targetId = targetId;
            this.quantity = quantity;
        }
    }
}
