package com.idme.miniapp.controller;

import com.idme.miniapp.dto.ApiResponse;
import com.idme.miniapp.service.MiniAppAdapterService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parts")
public class PartController {

    private final MiniAppAdapterService adapterService;

    public PartController(MiniAppAdapterService adapterService) {
        this.adapterService = adapterService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String categoryId,
        @RequestParam(required = false) String categoryIds,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(adapterService.listParts(keyword, categoryId, categoryIds, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> get(@PathVariable String id) {
        return ApiResponse.success(adapterService.getPart(id));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        return ApiResponse.success(adapterService.createPart(body));
    }

    @PostMapping("/")
    public ApiResponse<Map<String, Object>> createWithTrailingSlash(@RequestBody Map<String, Object> body) {
        return ApiResponse.success(adapterService.createPart(body));
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> update(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(adapterService.updatePart(id, body));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Object> delete(@PathVariable String id) {
        adapterService.deletePart(id);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/{id}/bom")
    public ApiResponse<List<Map<String, Object>>> getBom(@PathVariable String id) {
        return ApiResponse.success(adapterService.getPartBom(id));
    }

    @PutMapping("/{id}/bom")
    public ApiResponse<Object> updateBom(@PathVariable String id, @RequestBody List<Map<String, Object>> body) {
        adapterService.updatePartBom(id, body);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/bom")
    public ApiResponse<Object> addBomItem(@PathVariable String id, @RequestBody Map<String, Object> body) {
        Object componentId = body.get("componentId");
        Object quantity = body.get("quantity");
        if (componentId == null) {
            throw new IllegalArgumentException("componentId 不能为空");
        }
        Map<String, Object> item = new java.util.LinkedHashMap<>();
        item.put("id", componentId);
        item.put("quantity", quantity == null ? 1 : quantity);
        adapterService.updatePartBom(id, java.util.List.of(item));
        return ApiResponse.success(null);
    }

    @GetMapping("/categories")
    public ApiResponse<List<Map<String, Object>>> listCategories() {
        return ApiResponse.success(adapterService.listCategories());
    }

    @PostMapping("/categories")
    public ApiResponse<Map<String, Object>> createCategory(@RequestBody Map<String, Object> body) {
        return ApiResponse.success(adapterService.createCategory(body));
    }

    @PutMapping("/categories/{id}")
    public ApiResponse<Map<String, Object>> updateCategory(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(adapterService.updateCategory(id, body));
    }

    @DeleteMapping("/categories/{id}")
    public ApiResponse<Object> deleteCategory(@PathVariable String id) {
        adapterService.deleteCategory(id);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/{id}/versions")
    public ApiResponse<List<Map<String, Object>>> listVersions(@PathVariable String id) {
        return ApiResponse.success(adapterService.listPartVersions(id));
    }

    @PostMapping("/{id}/versions")
    public ApiResponse<Map<String, Object>> createVersion(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(adapterService.createPartVersion(id, body));
    }

    @GetMapping("/{id}/versions/compare")
    public ApiResponse<Map<String, Object>> compareVersions(
        @PathVariable String id,
        @RequestParam String v1,
        @RequestParam String v2
    ) {
        return ApiResponse.success(adapterService.comparePartVersions(id, v1, v2));
    }

    @PostMapping("/{id}/versions/{version}/rollback")
    public ApiResponse<Object> rollbackVersion(@PathVariable String id, @PathVariable String version) {
        adapterService.rollbackPartVersion(id, version);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/versions/{version}/status")
    public ApiResponse<Object> updateVersionStatus(
        @PathVariable String id,
        @PathVariable String version,
        @RequestBody(required = false) Map<String, Object> body
    ) {
        String status = body == null ? null : (String) body.get("status");
        adapterService.updatePartVersionStatus(id, version, status);
        return ApiResponse.success(null);
    }
}
