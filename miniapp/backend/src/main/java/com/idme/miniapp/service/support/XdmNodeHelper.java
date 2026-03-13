package com.idme.miniapp.service.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;

import java.util.Arrays;
import java.util.List;

public final class XdmNodeHelper {

    private static final List<String> DEFAULT_RECORDS_PATHS = List.of(
        "/data/records",
        "/records",
        "/data/list",
        "/list"
    );

    private XdmNodeHelper() {
    }

    public static JsonNode value(JsonNode root, String path) {
        if (root == null || path == null || path.isBlank()) {
            return MissingNode.getInstance();
        }
        if (path.startsWith("/")) {
            JsonNode pointerNode = root.at(path);
            if (!pointerNode.isMissingNode()) {
                return pointerNode;
            }
        }

        JsonNode direct = root.path(path);
        if (!direct.isMissingNode()) {
            return direct;
        }

        if (path.contains(".")) {
            JsonNode nested = root;
            for (String segment : path.split("\\.")) {
                nested = nested.path(segment);
                if (nested.isMissingNode()) {
                    break;
                }
            }
            if (!nested.isMissingNode()) {
                return nested;
            }
        }
        return MissingNode.getInstance();
    }

    public static String text(JsonNode root, String... paths) {
        for (String path : paths) {
            JsonNode node = value(root, path);
            if (node.isMissingNode() || node.isNull()) {
                continue;
            }
            String text = node.asText();
            if (text != null && !text.isBlank() && !"null".equalsIgnoreCase(text)) {
                return text;
            }
        }
        return "";
    }

    public static long longValue(JsonNode root, long defaultValue, String... paths) {
        for (String path : paths) {
            JsonNode node = value(root, path);
            if (!node.isMissingNode() && node.isNumber()) {
                return node.asLong();
            }
            if (!node.isMissingNode() && node.isTextual()) {
                try {
                    return Long.parseLong(node.asText().trim());
                } catch (NumberFormatException ignored) {
                    // noop
                }
            }
        }
        return defaultValue;
    }

    public static ArrayNode extractRecords(ObjectMapper mapper, JsonNode root) {
        for (String path : DEFAULT_RECORDS_PATHS) {
            JsonNode node = value(root, path);
            if (node.isArray()) {
                return (ArrayNode) node;
            }
        }

        JsonNode data = value(root, "/data");
        if (data.isArray()) {
            return (ArrayNode) data;
        }
        if (root != null && root.isArray()) {
            return (ArrayNode) root;
        }
        return mapper.createArrayNode();
    }

    public static JsonNode extractSingle(JsonNode root, String... candidatePaths) {
        List<String> paths = candidatePaths == null || candidatePaths.length == 0
            ? List.of("/data", "/result")
            : Arrays.asList(candidatePaths);

        for (String path : paths) {
            JsonNode node = value(root, path);
            if (!node.isMissingNode() && !node.isNull()) {
                if (node.isArray()) {
                    return node.size() > 0 ? node.get(0) : MissingNode.getInstance();
                }
                if (node.isObject()) {
                    JsonNode records = value(node, "records");
                    if (records.isArray() && records.size() > 0) {
                        return records.get(0);
                    }
                }
                return node;
            }
        }

        if (root != null && root.isArray() && root.size() > 0) {
            return root.get(0);
        }
        return root == null ? MissingNode.getInstance() : root;
    }
}
