package com.idme.miniapp.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idme.miniapp.service.EquipmentProductionDateStore;
import com.idme.miniapp.service.XdmGatewayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private XdmGatewayService xdmGatewayService;

    @MockBean
    private EquipmentProductionDateStore equipmentProductionDateStore;

    @BeforeEach
    void setUp() {
        Mockito.reset(xdmGatewayService, equipmentProductionDateStore);
        Mockito.when(equipmentProductionDateStore.get(Mockito.anyString())).thenReturn("");
    }

    @Test
    void shouldReturnPartPagedResult() throws Exception {
        JsonNode partListResponse = successResponse(List.of(Map.of(
            "id", "p-1",
            "partCode", "P-001",
            "partName", "Part-A",
            "specModel", "M-1",
            "stockQty", "12",
            "supplier", "Supplier-A",
            "versionNo", "V1.0"
        )));
        Mockito.when(xdmGatewayService.proxy(
            Mockito.eq("/dynamic/api/Part/list"),
            Mockito.eq(HttpMethod.POST),
            Mockito.any(JsonNode.class)
        )).thenReturn(partListResponse);

        mockMvc.perform(get("/api/parts").param("page", "1").param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.records[0].partNo").value("P-001"))
            .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void shouldReturnEquipmentListCompatibleWithFrontend() throws Exception {
        JsonNode equipmentListResponse = successResponse(List.of(Map.of(
            "id", "e-1",
            "equipmentCode", "EQ-001",
            "equipmentName", "CNC",
            "manufacturer", "Factory-A",
            "specModel", "CNC-100"
        )));
        Mockito.when(xdmGatewayService.proxy(
            Mockito.eq("/dynamic/api/Equipment/list"),
            Mockito.eq(HttpMethod.POST),
            Mockito.any(JsonNode.class)
        )).thenReturn(equipmentListResponse);

        mockMvc.perform(get("/api/equipments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data[0].equipmentCode").value("EQ-001"))
            .andExpect(jsonPath("$.data[0].equipmentName").value("CNC"))
            .andExpect(jsonPath("$.data[0].code").value("EQ-001"))
            .andExpect(jsonPath("$.data[0].name").value("CNC"));
    }

    @Test
    void shouldConvertXdmFailureToServerErrorResponse() throws Exception {
        JsonNode failed = objectMapper.valueToTree(Map.of(
            "result", "FAILED",
            "data", List.of(),
            "errors", List.of("boom")
        ));
        Mockito.when(xdmGatewayService.proxy(
            Mockito.eq("/dynamic/api/WorkingPlan/list"),
            Mockito.eq(HttpMethod.POST),
            Mockito.any(JsonNode.class)
        )).thenReturn(failed);

        mockMvc.perform(get("/api/working-plans"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code").value(500));
    }

    private JsonNode successResponse(List<Map<String, Object>> data) {
        return objectMapper.valueToTree(Map.of(
            "result", "SUCCESS",
            "data", data,
            "errors", List.of()
        ));
    }
}
