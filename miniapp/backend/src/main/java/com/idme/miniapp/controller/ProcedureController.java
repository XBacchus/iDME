package com.idme.miniapp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.idme.miniapp.dto.ApiResponse;
import com.idme.miniapp.service.ProcedureService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/procedures")
public class ProcedureController {

    private final ProcedureService procedureService;

    public ProcedureController(ProcedureService procedureService) {
        this.procedureService = procedureService;
    }

    @GetMapping
    public ApiResponse<JsonNode> listProcedures() {
        return ApiResponse.success(procedureService.listProcedures());
    }
}
