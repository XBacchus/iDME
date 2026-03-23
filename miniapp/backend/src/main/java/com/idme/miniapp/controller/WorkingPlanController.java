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
@RequestMapping("/api/working-plans")
public class WorkingPlanController {

    private final MiniAppAdapterService adapterService;

    public WorkingPlanController(MiniAppAdapterService adapterService) {
        this.adapterService = adapterService;
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(@RequestParam(required = false) String keyword) {
        return ApiResponse.success(adapterService.listWorkingPlans(keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> get(@PathVariable String id) {
        return ApiResponse.success(adapterService.getWorkingPlan(id));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        return ApiResponse.success(adapterService.createWorkingPlan(body));
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> update(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(adapterService.updateWorkingPlan(id, body));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Object> delete(@PathVariable String id) {
        adapterService.deleteWorkingPlan(id);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/{id}/processes")
    public ApiResponse<List<Map<String, Object>>> getProcesses(@PathVariable String id) {
        return ApiResponse.success(adapterService.getWorkingPlanProcesses(id));
    }

    @PutMapping("/{id}/processes")
    public ApiResponse<List<Map<String, Object>>> updateProcesses(
        @PathVariable String id,
        @RequestBody List<Map<String, Object>> body
    ) {
        return ApiResponse.success(adapterService.updateWorkingPlanProcesses(id, body));
    }

    @PostMapping("/{id}/procedures")
    public ApiResponse<List<Map<String, Object>>> addProcedure(
        @PathVariable String id,
        @RequestBody Map<String, Object> body
    ) {
        Map<String, Object> process = new java.util.LinkedHashMap<>();
        process.put("id", body.get("procedureId"));
        process.put("name", body.getOrDefault("name", ""));
        process.put("location", body.getOrDefault("location", ""));
        process.put("status", body.getOrDefault("status", "pending"));

        List<Map<String, Object>> merged = new java.util.ArrayList<>(adapterService.getWorkingPlanProcesses(id));
        merged.add(process);
        return ApiResponse.success(adapterService.updateWorkingPlanProcesses(id, merged));
    }
}
