package com.idme.miniapp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.idme.miniapp.service.XdmGatewayService;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class ManufacturingApiController {

    private static final String PARTS_PATH = "/api/parts";
    private static final String EQUIPMENTS_PATH = "/api/equipments";
    private static final String WORKING_PLANS_PATH = "/api/working-plans";

    private final XdmGatewayService xdmGatewayService;

    public ManufacturingApiController(XdmGatewayService xdmGatewayService) {
        this.xdmGatewayService = xdmGatewayService;
    }

    @PostMapping({"/parts", "/parts/"})
    public JsonNode createPart(@RequestBody(required = false) JsonNode body) {
        return xdmGatewayService.proxy(PARTS_PATH, HttpMethod.POST, body);
    }

    @GetMapping("/parts")
    public JsonNode listParts(@RequestParam MultiValueMap<String, String> queryParams) {
        return xdmGatewayService.proxy(withQuery(PARTS_PATH, queryParams), HttpMethod.GET, null);
    }

    @DeleteMapping("/parts/{id}")
    public JsonNode deletePart(@PathVariable String id) {
        return xdmGatewayService.proxy(PARTS_PATH + "/" + encodePathSegment(id), HttpMethod.DELETE, null);
    }

    @GetMapping("/parts/categories")
    public JsonNode getPartCategories() {
        return xdmGatewayService.proxy(PARTS_PATH + "/categories", HttpMethod.GET, null);
    }

    @PostMapping("/parts/{id}/bom")
    public JsonNode addPartBomRelation(@PathVariable String id, @RequestBody(required = false) JsonNode body) {
        return xdmGatewayService.proxy(PARTS_PATH + "/" + encodePathSegment(id) + "/bom", HttpMethod.POST, body);
    }

    @PostMapping("/equipments")
    public JsonNode createEquipment(@RequestBody(required = false) JsonNode body) {
        return xdmGatewayService.proxy(EQUIPMENTS_PATH, HttpMethod.POST, body);
    }

    @PostMapping("/working-plans")
    public JsonNode createWorkingPlan(@RequestBody(required = false) JsonNode body) {
        return xdmGatewayService.proxy(WORKING_PLANS_PATH, HttpMethod.POST, body);
    }

    @GetMapping("/working-plans/{id}")
    public JsonNode getWorkingPlanDetail(@PathVariable String id) {
        return xdmGatewayService.proxy(WORKING_PLANS_PATH + "/" + encodePathSegment(id), HttpMethod.GET, null);
    }

    @PostMapping("/working-plans/{id}/procedures")
    public JsonNode linkProcedure(@PathVariable String id, @RequestBody(required = false) JsonNode body) {
        return xdmGatewayService.proxy(WORKING_PLANS_PATH + "/" + encodePathSegment(id) + "/procedures",
            HttpMethod.POST, body);
    }

    private String withQuery(String path, MultiValueMap<String, String> queryParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(path);
        queryParams.forEach((key, values) -> {
            if (values == null || values.isEmpty()) {
                builder.queryParam(key);
                return;
            }
            values.forEach(value -> builder.queryParam(key, value));
        });
        return builder.build(true).toUriString();
    }

    private String encodePathSegment(String value) {
        return UriUtils.encodePathSegment(value, StandardCharsets.UTF_8);
    }
}
