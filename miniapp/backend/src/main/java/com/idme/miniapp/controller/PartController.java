package com.idme.miniapp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.idme.miniapp.dto.ApiResponse;
import com.idme.miniapp.service.PartService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parts")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    @PostMapping
    public ApiResponse<JsonNode> createPart(@RequestBody(required = false) JsonNode body) {
        return ApiResponse.success(partService.createPart(body));
    }

    @GetMapping
    public ApiResponse<JsonNode> listParts(@RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) Long categoryId,
                                           @RequestParam(required = false) Integer page,
                                           @RequestParam(required = false) Integer size) {
        return ApiResponse.success(partService.queryParts(keyword, categoryId, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<JsonNode> getPart(@PathVariable String id) {
        return ApiResponse.success(partService.getPartById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<JsonNode> updatePart(@PathVariable String id,
                                            @RequestBody(required = false) JsonNode body) {
        return ApiResponse.success(partService.updatePart(id, body));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<JsonNode> deletePart(@PathVariable String id) {
        return ApiResponse.success(partService.deletePart(id));
    }

    @GetMapping("/categories")
    public ApiResponse<JsonNode> getCategories() {
        return ApiResponse.success(partService.getCategories());
    }

    @PostMapping("/categories")
    public ApiResponse<JsonNode> createCategory(@RequestBody(required = false) JsonNode body) {
        return ApiResponse.success(partService.createCategory(body));
    }

    @PutMapping("/categories/{id}")
    public ApiResponse<JsonNode> updateCategory(@PathVariable String id,
                                                @RequestBody(required = false) JsonNode body) {
        return ApiResponse.success(partService.updateCategory(id, body));
    }

    @DeleteMapping("/categories/{id}")
    public ApiResponse<JsonNode> deleteCategory(@PathVariable String id) {
        return ApiResponse.success(partService.deleteCategory(id));
    }

    @GetMapping("/{id}/bom")
    public ApiResponse<JsonNode> getPartBom(@PathVariable String id) {
        return ApiResponse.success(partService.getPartBom(id));
    }

    @PutMapping("/{id}/bom")
    public ApiResponse<JsonNode> updatePartBom(@PathVariable String id,
                                               @RequestBody(required = false) JsonNode body) {
        return ApiResponse.success(partService.updatePartBom(id, body));
    }

    @GetMapping("/{id}/versions")
    public ApiResponse<JsonNode> getVersions(@PathVariable String id) {
        return ApiResponse.success(partService.getVersions(id));
    }

    @PostMapping("/{id}/versions")
    public ApiResponse<JsonNode> createVersion(@PathVariable String id,
                                               @RequestBody(required = false) JsonNode body) {
        return ApiResponse.success(partService.createVersion(id, body));
    }

    @GetMapping("/{id}/versions/compare")
    public ApiResponse<JsonNode> compareVersions(@PathVariable String id,
                                                 @RequestParam String v1,
                                                 @RequestParam String v2) {
        return ApiResponse.success(partService.compareVersions(id, v1, v2));
    }
}
