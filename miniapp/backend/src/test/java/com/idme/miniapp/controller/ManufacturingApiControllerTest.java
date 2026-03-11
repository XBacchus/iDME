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
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ManufacturingApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private XdmGatewayService xdmGatewayService;

    @BeforeEach
    void setUp() throws Exception {
        JsonNode okBody = objectMapper.readTree("{\"ok\":true}");
        when(xdmGatewayService.proxy(any(String.class), any(HttpMethod.class), nullable(JsonNode.class)))
            .thenReturn(okBody);
    }

    @Test
    void createPartShouldProxy() throws Exception {
        mockMvc.perform(post("/api/parts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"partNo\":\"P-1001\"}"))
            .andExpect(status().isOk());

        verify(xdmGatewayService).proxy(eq("/api/parts"), eq(HttpMethod.POST),
            argThat(jsonFieldEquals("partNo", "P-1001")));
    }

    @Test
    void createPartWithTrailingSlashShouldProxy() throws Exception {
        mockMvc.perform(post("/api/parts/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"partNo\":\"P-1002\"}"))
            .andExpect(status().isOk());

        verify(xdmGatewayService).proxy(eq("/api/parts"), eq(HttpMethod.POST),
            argThat(jsonFieldEquals("partNo", "P-1002")));
    }

    @Test
    void listPartsShouldProxyWithSearchQuery() throws Exception {
        mockMvc.perform(get("/api/parts")
                .param("keyword", "bolt")
                .param("page", "1")
                .param("size", "20"))
            .andExpect(status().isOk());

        verify(xdmGatewayService).proxy(
            argThat(path -> path.startsWith("/api/parts?")
                && path.contains("keyword=bolt")
                && path.contains("page=1")
                && path.contains("size=20")),
            eq(HttpMethod.GET),
            eq(null));
    }

    @Test
    void deletePartShouldProxy() throws Exception {
        mockMvc.perform(delete("/api/parts/P-3001"))
            .andExpect(status().isOk());

        verify(xdmGatewayService).proxy(eq("/api/parts/P-3001"), eq(HttpMethod.DELETE), eq(null));
    }

    @Test
    void getPartCategoriesShouldProxy() throws Exception {
        mockMvc.perform(get("/api/parts/categories"))
            .andExpect(status().isOk());

        verify(xdmGatewayService).proxy(eq("/api/parts/categories"), eq(HttpMethod.GET), eq(null));
    }

    @Test
    void addPartBomRelationShouldProxy() throws Exception {
        mockMvc.perform(post("/api/parts/P-1001/bom")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"componentId\":\"C-2001\",\"quantity\":2}"))
            .andExpect(status().isOk());

        verify(xdmGatewayService).proxy(eq("/api/parts/P-1001/bom"), eq(HttpMethod.POST),
            argThat(jsonFieldEquals("componentId", "C-2001")));
    }

    @Test
    void createEquipmentShouldProxy() throws Exception {
        mockMvc.perform(post("/api/equipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"equipmentNo\":\"EQ-01\"}"))
            .andExpect(status().isOk());

        verify(xdmGatewayService).proxy(eq("/api/equipments"), eq(HttpMethod.POST),
            argThat(jsonFieldEquals("equipmentNo", "EQ-01")));
    }

    @Test
    void createWorkingPlanShouldProxy() throws Exception {
        mockMvc.perform(post("/api/working-plans")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"WP-1\"}"))
            .andExpect(status().isOk());

        verify(xdmGatewayService).proxy(eq("/api/working-plans"), eq(HttpMethod.POST),
            argThat(jsonFieldEquals("name", "WP-1")));
    }

    @Test
    void getWorkingPlanDetailShouldProxy() throws Exception {
        mockMvc.perform(get("/api/working-plans/WP-1"))
            .andExpect(status().isOk());

        verify(xdmGatewayService).proxy(eq("/api/working-plans/WP-1"), eq(HttpMethod.GET), eq(null));
    }

    @Test
    void linkProcedureShouldProxy() throws Exception {
        mockMvc.perform(post("/api/working-plans/WP-1/procedures")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"procedureId\":\"PROC-01\"}"))
            .andExpect(status().isOk());

        verify(xdmGatewayService).proxy(eq("/api/working-plans/WP-1/procedures"), eq(HttpMethod.POST),
            argThat(jsonFieldEquals("procedureId", "PROC-01")));
    }

    private static ArgumentMatcher<JsonNode> jsonFieldEquals(String key, String expected) {
        return node -> node != null && expected.equals(node.path(key).asText());
    }
}
