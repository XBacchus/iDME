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
public class EquipmentProductionDateStore {

    private static final Logger log = LoggerFactory.getLogger(EquipmentProductionDateStore.class);
    private static final TypeReference<Map<String, String>> MAP_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final Path storePath = Path.of("data", "equipment-production-dates.json");
    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    public EquipmentProductionDateStore(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        loadFromDisk();
    }

    public synchronized String get(String equipmentId) {
        if (!StringUtils.hasText(equipmentId)) {
            return "";
        }
        return cache.getOrDefault(equipmentId, "");
    }

    public synchronized void put(String equipmentId, String productionDate) {
        if (!StringUtils.hasText(equipmentId)) {
            return;
        }
        if (StringUtils.hasText(productionDate)) {
            cache.put(equipmentId, productionDate.trim());
        } else {
            cache.remove(equipmentId);
        }
        persistQuietly();
    }

    public synchronized void mergeFromXdm(String equipmentId, String productionDate) {
        if (!StringUtils.hasText(equipmentId) || !StringUtils.hasText(productionDate)) {
            return;
        }
        String normalized = productionDate.trim();
        String current = cache.get(equipmentId);
        if (normalized.equals(current)) {
            return;
        }
        cache.put(equipmentId, normalized);
        persistQuietly();
    }

    public synchronized void remove(String equipmentId) {
        if (!StringUtils.hasText(equipmentId)) {
            return;
        }
        cache.remove(equipmentId);
        persistQuietly();
    }

    private void loadFromDisk() {
        if (!Files.exists(storePath)) {
            return;
        }
        try {
            Map<String, String> loaded = objectMapper.readValue(storePath.toFile(), MAP_TYPE);
            loaded.forEach((id, date) -> {
                if (StringUtils.hasText(id) && StringUtils.hasText(date)) {
                    cache.put(id.trim(), date.trim());
                }
            });
        } catch (IOException ex) {
            log.warn("加载设备生产日期缓存失败: {}", storePath, ex);
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
            log.warn("持久化设备生产日期缓存失败: {}", storePath, ex);
        }
    }
}
