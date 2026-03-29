package com.idme.miniapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WorkingPlanOperationTimeStore {

    private static final Logger log = LoggerFactory.getLogger(WorkingPlanOperationTimeStore.class);
    private static final TypeReference<Map<String, String>> MAP_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final Path storePath = Path.of("data", "working-plan-operation-times.json");
    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    public WorkingPlanOperationTimeStore(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        loadFromDisk();
    }

    public synchronized String get(String workingPlanId) {
        if (!StringUtils.hasText(workingPlanId)) {
            return "";
        }
        return cache.getOrDefault(workingPlanId, "");
    }

    public synchronized void put(String workingPlanId, String operationTime) {
        if (!StringUtils.hasText(workingPlanId)) {
            return;
        }
        if (StringUtils.hasText(operationTime)) {
            cache.put(workingPlanId, operationTime.trim());
        } else {
            cache.remove(workingPlanId);
        }
        persistQuietly();
    }

    public synchronized void mergeFromXdm(String workingPlanId, String operationTime) {
        if (!StringUtils.hasText(workingPlanId) || !StringUtils.hasText(operationTime)) {
            return;
        }
        String normalized = operationTime.trim();
        String current = cache.get(workingPlanId);
        if (normalized.equals(current)) {
            return;
        }
        cache.put(workingPlanId, normalized);
        persistQuietly();
    }

    public synchronized void remove(String workingPlanId) {
        if (!StringUtils.hasText(workingPlanId)) {
            return;
        }
        cache.remove(workingPlanId);
        persistQuietly();
    }

    private void loadFromDisk() {
        if (!Files.exists(storePath)) {
            return;
        }
        try {
            Map<String, String> loaded = objectMapper.readValue(storePath.toFile(), MAP_TYPE);
            loaded.forEach((id, time) -> {
                if (StringUtils.hasText(id) && StringUtils.hasText(time)) {
                    cache.put(id.trim(), time.trim());
                }
            });
        } catch (IOException ex) {
            log.warn("加载工艺路线操作时间缓存失败: {}", storePath, ex);
        }
    }

    private void persistQuietly() {
        try {
            if (storePath.getParent() != null) {
                Files.createDirectories(storePath.getParent());
            }
            Map<String, String> stable = new LinkedHashMap<>(new TreeMap<>(cache));
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(storePath.toFile(), stable);
        } catch (IOException ex) {
            log.warn("持久化工艺路线操作时间缓存失败: {}", storePath, ex);
        }
    }
}
