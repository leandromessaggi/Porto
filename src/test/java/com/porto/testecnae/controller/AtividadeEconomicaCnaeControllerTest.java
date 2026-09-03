package com.porto.testecnae.controller;

import com.porto.testecnae.exception.CnaeNotFoundException;
import com.porto.testecnae.service.AtividadeEconomicaCnaeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AtividadeEconomicaCnaeController.class)
class AtividadeEconomicaCnaeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AtividadeEconomicaCnaeService service;

    @Test
    void deveRetornar404QuandoCnaeNaoExistir() throws Exception {

        String codigo = "9999-9/99";

        given(service.buscarPorCodigo(codigo))
                .willThrow(new CnaeNotFoundException(codigo));

        mockMvc.perform(
                        get("/api/cnaes/codigo")
                                .param("codigo", codigo)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("CNAE não encontrado"))
                .andExpect(jsonPath("$.detail")
                        .value("CNAE não encontrado para o código: " + codigo));
    }
}