package com.idme.miniapp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idme.miniapp.service.XdmGatewayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ManufacturingControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private XdmGatewayService xdmGatewayService;

    @BeforeEach
    void setUp() throws Exception {
        JsonNode defaultResponse = objectMapper.readTree("{\"data\":{\"records\":[]}}");
        when(xdmGatewayService.proxy(any(String.class), any(HttpMethod.class), any(JsonNode.class)))
            .thenReturn(defaultResponse);
    }

    @Test
    void createPartShouldProxyToDynamicApi() throws Exception {
        mockMvc.perform(post("/api/parts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"partNo\":\"P-1001\",\"partName\":\"Bolt\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        verify(xdmGatewayService).proxy(eq("/dynamic/api/Part/create"), eq(HttpMethod.POST),
            argThat(jsonFieldEquals("partCode", "P-1001")));
    }

    @Test
    void listPartsShouldProxyToQueryApi() throws Exception {
        mockMvc.perform(get("/api/parts")
                .param("keyword", "bolt")
                .param("page", "2")
                .param("size", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        verify(xdmGatewayService).proxy(eq("/dynamic/api/Part/query"), eq(HttpMethod.POST),
            argThat(node -> node.path("pageNo").asInt() == 2 && node.path("pageSize").asInt() == 5));
    }

    @Test
    void getPartShouldProxyToGetApi() throws Exception {
        mockMvc.perform(get("/api/parts/P-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        verify(xdmGatewayService).proxy(eq("/dynamic/api/Part/get"), eq(HttpMethod.POST),
            argThat(jsonFieldEquals("id", "P-1")));
    }

    @Test
    void updatePartShouldProxyToUpdateApi() throws Exception {
        mockMvc.perform(put("/api/parts/P-2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"partName\":\"Updated\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        verify(xdmGatewayService).proxy(eq("/dynamic/api/Part/update"), eq(HttpMethod.POST),
            argThat(jsonFieldEquals("id", "P-2")));
    }

    @Test
    void deletePartShouldProxyToDeleteApi() throws Exception {
        mockMvc.perform(delete("/api/parts/P-3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        verify(xdmGatewayService).proxy(eq("/dynamic/api/Part/delete"), eq(HttpMethod.POST),
            argThat(jsonFieldEquals("id", "P-3")));
    }

    @Test
    void listCategoriesShouldProxyToCategoryQueryApi() throws Exception {
        mockMvc.perform(get("/api/parts/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        verify(xdmGatewayService).proxy(eq("/dynamic/api/PartCategory/query"), eq(HttpMethod.POST), any(JsonNode.class));
    }

    @Test
    void updateBomShouldProxyToRelationCreateApi() throws Exception {
        mockMvc.perform(put("/api/parts/P-4/bom")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"children\":[{\"childId\":\"C-1\",\"quantity\":2}]}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        verify(xdmGatewayService, atLeastOnce()).proxy(eq("/dynamic/api/Part_Part/create"), eq(HttpMethod.POST),
            argThat(node -> "P-4".equals(node.path("source.id").asText()) && "C-1".equals(node.path("target.id").asText())));
    }

    @Test
    void getEquipmentsShouldProxyToEquipmentQueryApi() throws Exception {
        mockMvc.perform(get("/api/equipments").param("keyword", "eq"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        verify(xdmGatewayService).proxy(eq("/dynamic/api/Equipment/query"), eq(HttpMethod.POST), any(JsonNode.class));
    }

    @Test
    void createEquipmentShouldProxyToEquipmentCreateApi() throws Exception {
        mockMvc.perform(post("/api/equipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"equipmentNo\":\"EQ-1\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        verify(xdmGatewayService).proxy(eq("/dynamic/api/Equipment/create"), eq(HttpMethod.POST),
            argThat(jsonFieldEquals("equipmentCode", "EQ-1")));
    }

    @Test
    void listProceduresShouldProxyToProcedureQueryApi() throws Exception {
        mockMvc.perform(get("/api/procedures"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        verify(xdmGatewayService).proxy(eq("/dynamic/api/WorkingProcedure/query"), eq(HttpMethod.POST), any(JsonNode.class));
    }

    @Test
    void createWorkingPlanShouldProxyToWorkingPlanCreateApi() throws Exception {
        mockMvc.perform(post("/api/working-plans")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"planCode\":\"WP-1\",\"planName\":\"Route A\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        verify(xdmGatewayService).proxy(eq("/dynamic/api/WorkingPlan/create"), eq(HttpMethod.POST),
            argThat(jsonFieldEquals("planCode", "WP-1")));
    }

    @Test
    void getWorkingPlanShouldProxyToWorkingPlanGetApi() throws Exception {
        mockMvc.perform(get("/api/working-plans/WP-2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        verify(xdmGatewayService).proxy(eq("/dynamic/api/WorkingPlan/get"), eq(HttpMethod.POST),
            argThat(jsonFieldEquals("id", "WP-2")));
    }

    @Test
    void updateWorkingPlanShouldProxyToWorkingPlanUpdateApi() throws Exception {
        mockMvc.perform(put("/api/working-plans/WP-3")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"planName\":\"Updated\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        verify(xdmGatewayService).proxy(eq("/dynamic/api/WorkingPlan/update"), eq(HttpMethod.POST),
            argThat(jsonFieldEquals("id", "WP-3")));
    }

    @Test
    void deleteWorkingPlanShouldProxyToWorkingPlanDeleteApi() throws Exception {
        mockMvc.perform(delete("/api/working-plans/WP-4"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        verify(xdmGatewayService).proxy(eq("/dynamic/api/WorkingPlan/delete"), eq(HttpMethod.POST),
            argThat(jsonFieldEquals("id", "WP-4")));
    }

    @Test
    void compareVersionRequiresParams() throws Exception {
        mockMvc.perform(get("/api/parts/P-5/versions/compare"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400));
    }

    private static ArgumentMatcher<JsonNode> jsonFieldEquals(String key, String expected) {
        return node -> node != null && expected.equals(node.path(key).asText());
    }
}
