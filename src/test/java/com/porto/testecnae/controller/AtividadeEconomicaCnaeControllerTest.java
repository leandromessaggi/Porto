package com.porto.testecnae.controller;

import com.porto.testecnae.dto.AtividadeEconomicaCnaeResponse;
import com.porto.testecnae.exception.CnaeNotFoundException;
import com.porto.testecnae.service.AtividadeEconomicaCnaeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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
    void deveListarTodosOsCnaes() throws Exception {

        given(service.listarTodas())
                .willReturn(List.of(
                        response(
                                1L,
                                "6201-5/01",
                                "Desenvolvimento de programas de computador sob encomenda"
                        ),
                        response(
                                2L,
                                "6202-3/00",
                                "Desenvolvimento e licenciamento de programas customizaveis"
                        )
                ));

        mockMvc.perform(get("/api/cnaes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].codigo").value("6201-5/01"))
                .andExpect(jsonPath("$[1].codigo").value("6202-3/00"));
    }

    @Test
    void deveBuscarCnaesPorDescricao() throws Exception {

        String termo = "programas";

        given(service.buscarPorDescricao(termo))
                .willReturn(List.of(
                        response(
                                1L,
                                "6201-5/01",
                                "Desenvolvimento de programas de computador sob encomenda"
                        )
                ));

        mockMvc.perform(
                        get("/api/cnaes/buscar")
                                .param("termo", termo)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].codigo").value("6201-5/01"))
                .andExpect(jsonPath("$[0].secao").value("Tecnologia"));
    }

    @Test
    void deveBuscarCnaePorCodigo() throws Exception {

        String codigo = "6201-5/01";

        given(service.buscarPorCodigo(codigo))
                .willReturn(
                        response(
                                1L,
                                codigo,
                                "Desenvolvimento de programas de computador sob encomenda"
                        )
                );

        mockMvc.perform(
                        get("/api/cnaes/codigo")
                                .param("codigo", codigo)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigo").value(codigo))
                .andExpect(jsonPath("$.secao").value("Tecnologia"));
    }

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
                .andExpect(jsonPath("$.title")
                        .value("CNAE não encontrado"))
                .andExpect(jsonPath("$.detail")
                        .value(
                                "CNAE não encontrado para o código: "
                                        + codigo
                        ));
    }

    private AtividadeEconomicaCnaeResponse response(
            Long id,
            String codigo,
            String descricao) {

        return new AtividadeEconomicaCnaeResponse(
                id,
                codigo,
                descricao,
                "Tecnologia"
        );
    }
}