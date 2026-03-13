package com.idme.miniapp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.idme.miniapp.dto.ApiResponse;
import com.idme.miniapp.service.EquipmentService;
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
@RequestMapping("/api/equipments")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @PostMapping
    public ApiResponse<JsonNode> createEquipment(@RequestBody(required = false) JsonNode body) {
        return ApiResponse.success(equipmentService.createEquipment(body));
    }

    @GetMapping
    public ApiResponse<JsonNode> listEquipments(@RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) Integer page,
                                                @RequestParam(required = false) Integer size) {
        return ApiResponse.success(equipmentService.listEquipments(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<JsonNode> getEquipment(@PathVariable String id) {
        return ApiResponse.success(equipmentService.getEquipment(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<JsonNode> updateEquipment(@PathVariable String id,
                                                 @RequestBody(required = false) JsonNode body) {
        return ApiResponse.success(equipmentService.updateEquipment(id, body));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<JsonNode> deleteEquipment(@PathVariable String id) {
        return ApiResponse.success(equipmentService.deleteEquipment(id));
    }
}
