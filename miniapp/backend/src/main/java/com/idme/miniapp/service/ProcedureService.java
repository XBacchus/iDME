package com.idme.miniapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.idme.miniapp.service.support.XdmNodeHelper;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ProcedureService {

    private final XdmGatewayService gateway;
    private final ObjectMapper mapper;

    public ProcedureService(XdmGatewayService gateway, ObjectMapper mapper) {
        this.gateway = gateway;
        this.mapper = mapper;
    }

    public JsonNode listProcedures() {
        ObjectNode body = mapper.createObjectNode();
        body.set("condition", mapper.createObjectNode());
        body.put("pageNo", 1);
        body.put("pageSize", 200);

        JsonNode xdmResult = gateway.proxy("/dynamic/api/WorkingProcedure/query", HttpMethod.POST, body);
        ArrayNode records = XdmNodeHelper.extractRecords(mapper, xdmResult);
        if (records.isEmpty()) {
            return defaultProcedures();
        }

        List<ObjectNode> mapped = new ArrayList<>();
        records.forEach(item -> mapped.add(mapFromXdm(item)));
        mapped.sort(Comparator.comparingInt(item -> item.path("order").asInt(0)));

        ArrayNode result = mapper.createArrayNode();
        mapped.forEach(result::add);
        return result;
    }

    private ArrayNode defaultProcedures() {
        ArrayNode defaults = mapper.createArrayNode();
        defaults.add(defaultProcedure("PROC-1", "毛坯制造", 1));
        defaults.add(defaultProcedure("PROC-2", "粗加工", 2));
        defaults.add(defaultProcedure("PROC-3", "精加工", 3));
        defaults.add(defaultProcedure("PROC-4", "检测", 4));
        defaults.add(defaultProcedure("PROC-5", "入库", 5));
        return defaults;
    }

    private ObjectNode defaultProcedure(String code, String name, int order) {
        ObjectNode node = mapper.createObjectNode();
        node.put("procedureCode", code);
        node.put("procedureName", name);
        node.put("order", order);
        return node;
    }

    private ObjectNode mapFromXdm(JsonNode source) {
        ObjectNode mapped = mapper.createObjectNode();
        putIfHasText(mapped, "id", XdmNodeHelper.text(source, "id", "_id"));
        putIfHasText(mapped, "procedureCode", XdmNodeHelper.text(source, "procedureCode", "code"));
        putIfHasText(mapped, "procedureName", XdmNodeHelper.text(source, "procedureName", "name"));
        mapped.put("order", (int) XdmNodeHelper.longValue(source, 0, "order", "sortNo"));
        return mapped;
    }

    private void putIfHasText(ObjectNode node, String field, String value) {
        if (value != null && !value.isBlank()) {
            node.put(field, value);
        }
    }
}
