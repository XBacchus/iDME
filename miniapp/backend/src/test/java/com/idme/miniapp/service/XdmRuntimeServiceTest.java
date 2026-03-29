package com.idme.miniapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idme.miniapp.config.XdmProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;

class XdmRuntimeServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void executeFirstShouldReturnFirstDataItem() {
        XdmGatewayService gateway = Mockito.mock(XdmGatewayService.class);
        XdmProperties props = new XdmProperties();
        props.setApplicationId("app-demo");

        JsonNode response = objectMapper.valueToTree(Map.of(
            "result", "SUCCESS",
            "data", List.of(Map.of("id", "1001", "partCode", "P-001")),
            "errors", List.of()
        ));
        Mockito.when(gateway.proxy(Mockito.eq("/dynamic/api/Part/get"), Mockito.eq(HttpMethod.POST), Mockito.any()))
            .thenReturn(response);

        XdmRuntimeService service = new XdmRuntimeService(objectMapper, gateway, props);
        JsonNode first = service.executeFirst("/dynamic/api/Part/get", Map.of("id", "1001"));

        Assertions.assertEquals("1001", first.path("id").asText());
    }

    @Test
    void executeListShouldThrowWhenResultFailed() {
        XdmGatewayService gateway = Mockito.mock(XdmGatewayService.class);
        XdmProperties props = new XdmProperties();
        props.setApplicationId("");

        JsonNode response = objectMapper.valueToTree(Map.of(
            "result", "FAILED",
            "data", List.of(),
            "errors", List.of("boom")
        ));
        Mockito.when(gateway.proxy(Mockito.eq("/dynamic/api/Part/list"), Mockito.eq(HttpMethod.POST), Mockito.any()))
            .thenReturn(response);

        XdmRuntimeService service = new XdmRuntimeService(objectMapper, gateway, props);

        Assertions.assertThrows(
            IllegalStateException.class,
            () -> service.executeList("/dynamic/api/Part/list", Map.of("entityType", "Part"))
        );
    }
}
