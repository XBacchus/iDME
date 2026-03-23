package com.idme.miniapp.controller;

import com.idme.miniapp.dto.ApiResponse;
import com.idme.miniapp.service.MiniAppAdapterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/procedures")
public class ProcedureController {

    private final MiniAppAdapterService adapterService;

    public ProcedureController(MiniAppAdapterService adapterService) {
        this.adapterService = adapterService;
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list() {
        return ApiResponse.success(adapterService.listProcedures());
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> update(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(adapterService.updateProcedure(id, body));
    }
}
