package com.idme.miniapp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.idme.miniapp.dto.ApiResponse;
import com.idme.miniapp.service.WorkingPlanService;
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
@RequestMapping("/api/working-plans")
public class WorkingPlanController {

    private final WorkingPlanService workingPlanService;

    public WorkingPlanController(WorkingPlanService workingPlanService) {
        this.workingPlanService = workingPlanService;
    }

    @PostMapping
    public ApiResponse<JsonNode> createWorkingPlan(@RequestBody(required = false) JsonNode body) {
        return ApiResponse.success(workingPlanService.createWorkingPlan(body));
    }

    @GetMapping
    public ApiResponse<JsonNode> listWorkingPlans(@RequestParam(required = false) String keyword,
                                                  @RequestParam(required = false) Integer page,
                                                  @RequestParam(required = false) Integer size) {
        return ApiResponse.success(workingPlanService.listWorkingPlans(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<JsonNode> getWorkingPlan(@PathVariable String id) {
        return ApiResponse.success(workingPlanService.getWorkingPlanDetail(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<JsonNode> updateWorkingPlan(@PathVariable String id,
                                                   @RequestBody(required = false) JsonNode body) {
        return ApiResponse.success(workingPlanService.updateWorkingPlan(id, body));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<JsonNode> deleteWorkingPlan(@PathVariable String id) {
        return ApiResponse.success(workingPlanService.deleteWorkingPlan(id));
    }
}
