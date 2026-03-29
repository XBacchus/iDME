package com.idme.miniapp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idme.miniapp.service.EquipmentProductionDateStore;
import com.idme.miniapp.service.XdmGatewayService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiAdapterIntegrationTest {

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
    void shouldListPartsWithUnifiedResponse() throws Exception {
        JsonNode partListResponse = success(List.of(Map.of(
            "id", "1001",
            "partCode", "P-1001",
            "partName", "中心轮组件",
            "specModel", "CL-100",
            "stockQty", "20",
            "supplier", "供应商A",
            "versionNo", "V1.0"
        )));

        Mockito.when(xdmGatewayService.proxy(Mockito.eq("/dynamic/api/Part/list"), Mockito.eq(HttpMethod.POST), Mockito.any()))
            .thenReturn(partListResponse);

        mockMvc.perform(get("/api/parts")
                .param("keyword", "")
                .param("page", "1")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.records[0].partNo").value("P-1001"))
            .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void shouldCreateEquipmentWithUnifiedResponse() throws Exception {
        JsonNode createResponse = success(List.of(Map.of(
            "id", "E1001",
            "equipmentCode", "EQ-01",
            "equipmentName", "数控车床",
            "manufacturer", "厂家A"
        )));
        Mockito.when(xdmGatewayService.proxy(Mockito.eq("/dynamic/api/Equipment/create"), Mockito.eq(HttpMethod.POST), Mockito.any()))
            .thenReturn(createResponse);

        String body = """
            {
              "code": "EQ-01",
              "name": "数控车床",
              "manufacturer": "厂家A"
            }
            """;

        mockMvc.perform(post("/api/equipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.id").value("E1001"))
            .andExpect(jsonPath("$.data.equipmentCode").value("EQ-01"))
            .andExpect(jsonPath("$.data.equipmentName").value("数控车床"))
            .andExpect(jsonPath("$.data.code").value("EQ-01"));
    }

    @Test
    void shouldRetryCreateWithoutProductionDateWhenXdmRejectsDate() throws Exception {
        JsonNode failResponse = objectMapper.valueToTree(Map.of(
            "result", "FAIL",
            "data", List.of(),
            "errors", List.of(),
            "error_msg", "The value of parameter 'params.productionDate' does not meet requirements."
        ));
        JsonNode successResponse = success(List.of(Map.of(
            "id", "E2001",
            "equipmentCode", "EQ-02",
            "equipmentName", "加工中心"
        )));
        Mockito.when(xdmGatewayService.proxy(Mockito.eq("/dynamic/api/Equipment/create"), Mockito.eq(HttpMethod.POST), Mockito.any()))
            .thenReturn(failResponse)
            .thenReturn(successResponse);

        String body = """
            {
              "equipmentCode": "EQ-02",
              "equipmentName": "加工中心",
              "manufacturer": "厂家B",
              "productionDate": "2026-03-13"
            }
            """;

        mockMvc.perform(post("/api/equipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.equipmentCode").value("EQ-02"))
            .andExpect(jsonPath("$.data.productionDate").value("2026-03-13"));

        ArgumentCaptor<JsonNode> captor = ArgumentCaptor.forClass(JsonNode.class);
        Mockito.verify(xdmGatewayService, Mockito.times(2))
            .proxy(Mockito.eq("/dynamic/api/Equipment/create"), Mockito.eq(HttpMethod.POST), captor.capture());
        List<JsonNode> requests = captor.getAllValues();
        Assertions.assertTrue(requests.get(0).path("params").has("productionDate"));
        Assertions.assertFalse(requests.get(1).path("params").has("productionDate"));
    }

    @Test
    void shouldSendNeedSetNullAttrsWhenClearingEquipmentTextFields() throws Exception {
        JsonNode updateResponse = success(List.of(Map.of(
            "id", "E3001",
            "equipmentCode", "EQ-03",
            "equipmentName", "立车"
        )));
        Mockito.when(xdmGatewayService.proxy(Mockito.eq("/dynamic/api/Equipment/update"), Mockito.eq(HttpMethod.POST), Mockito.any()))
            .thenReturn(updateResponse);

        String body = """
            {
              "equipmentCode": "EQ-03",
              "equipmentName": "立车",
              "manufacturer": "厂家C",
              "technicalParams": "",
              "sparePartsInfo": ""
            }
            """;

        mockMvc.perform(put("/api/equipments/E3001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<JsonNode> captor = ArgumentCaptor.forClass(JsonNode.class);
        Mockito.verify(xdmGatewayService, Mockito.times(1))
            .proxy(Mockito.eq("/dynamic/api/Equipment/update"), Mockito.eq(HttpMethod.POST), captor.capture());
        JsonNode attrs = captor.getValue().path("params").path("needSetNullAttrs");
        Assertions.assertTrue(attrs.isArray());
        List<String> values = List.of(attrs.get(0).asText(), attrs.get(1).asText());
        Assertions.assertTrue(values.contains("technicalParams"));
        Assertions.assertTrue(values.contains("sparePartsInfo"));
    }

    @Test
    void shouldRejectCreatePartWhenRequiredFieldsMissing() throws Exception {
        String body = """
            {
              "partNo": "P-REQ-001",
              "partName": "测试物料",
              "stockQty": 5
            }
            """;

        mockMvc.perform(post("/api/parts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("规格型号不能为空"));
    }

    @Test
    void shouldPreserveStockQtyWhenPartUpdatePayloadIsPartial() throws Exception {
        JsonNode getResponse = success(List.of(Map.of(
            "id", "P1001",
            "partCode", "P-1001",
            "partName", "中心轮组件",
            "specModel", "CL-100",
            "stockQty", 10,
            "supplier", "供应商A",
            "versionNo", "V1.0"
        )));
        JsonNode updateResponse = success(List.of(Map.of(
            "id", "P1001",
            "partCode", "P-1001",
            "partName", "中心轮组件",
            "specModel", "CL-100",
            "stockQty", 10,
            "supplier", "供应商B",
            "versionNo", "V1.0"
        )));
        Mockito.when(xdmGatewayService.proxy(Mockito.eq("/dynamic/api/Part/get"), Mockito.eq(HttpMethod.POST), Mockito.any()))
            .thenReturn(getResponse);
        Mockito.when(xdmGatewayService.proxy(Mockito.eq("/dynamic/api/Part/update"), Mockito.eq(HttpMethod.POST), Mockito.any()))
            .thenReturn(updateResponse);

        String body = """
            {
              "supplier": "供应商B"
            }
            """;

        mockMvc.perform(put("/api/parts/P1001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.stockQty").value(10))
            .andExpect(jsonPath("$.data.supplier").value("供应商B"));

        ArgumentCaptor<JsonNode> captor = ArgumentCaptor.forClass(JsonNode.class);
        Mockito.verify(xdmGatewayService, Mockito.times(1))
            .proxy(Mockito.eq("/dynamic/api/Part/update"), Mockito.eq(HttpMethod.POST), captor.capture());
        Assertions.assertEquals(10L, captor.getValue().path("params").path("stockQty").asLong());
    }

    private JsonNode success(Object data) {
        return objectMapper.valueToTree(Map.of(
            "result", "SUCCESS",
            "data", data,
            "errors", List.of()
        ));
    }
}
