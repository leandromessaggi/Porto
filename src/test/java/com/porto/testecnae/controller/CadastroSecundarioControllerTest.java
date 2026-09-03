package com.porto.testecnae.controller;

import tools.jackson.databind.ObjectMapper;
import com.porto.testecnae.dto.AtividadeEconomicaCnaeResponse;
import com.porto.testecnae.dto.CadastroSecundarioRequest;
import com.porto.testecnae.dto.CadastroSecundarioResponse;
import com.porto.testecnae.exception.CnaeNotFoundException;
import com.porto.testecnae.service.CadastroSecundarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CadastroSecundarioController.class)
class CadastroSecundarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CadastroSecundarioService service;

    @Test
    void deveCadastrarComSucesso() throws Exception {

        var request = new CadastroSecundarioRequest(
                "Tech Porto",
                "12345678000199",
                "6201-5/01"
        );

        given(service.cadastrar(any(CadastroSecundarioRequest.class)))
                .willReturn(responseCadastro());

        mockMvc.perform(
                        post("/api/cadastros-secundarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomeFantasia")
                        .value("Tech Porto"))
                .andExpect(jsonPath("$.documento")
                        .value("12345678000199"))
                .andExpect(jsonPath("$.cnae.codigo")
                        .value("6201-5/01"));
    }

    @Test
    void deveRetornar404AoCadastrarComCnaeInexistente()
            throws Exception {

        String codigo = "9999-9/99";

        var request = new CadastroSecundarioRequest(
                "Empresa Invalida",
                "99999999000199",
                codigo
        );

        given(service.cadastrar(any(CadastroSecundarioRequest.class)))
                .willThrow(new CnaeNotFoundException(codigo));

        mockMvc.perform(
                        post("/api/cadastros-secundarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
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

    @Test
    void deveValidarCnaeExistente() throws Exception {

        String codigo = "6201-5/01";

        given(service.validarCnae(codigo))
                .willReturn(responseCnae());

        mockMvc.perform(
                        get(
                                "/api/cadastros-secundarios/"
                                        + "validar-cnae"
                        )
                                .param("codigoCnae", codigo)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(codigo))
                .andExpect(jsonPath("$.secao").value("Tecnologia"));
    }

    @Test
    void deveRetornar404AoValidarCnaeInexistente()
            throws Exception {

        String codigo = "9999-9/99";

        given(service.validarCnae(codigo))
                .willThrow(new CnaeNotFoundException(codigo));

        mockMvc.perform(
                        get(
                                "/api/cadastros-secundarios/"
                                        + "validar-cnae"
                        )
                                .param("codigoCnae", codigo)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title")
                        .value("CNAE não encontrado"));
    }

    @Test
    void deveListarCadastrosSecundarios() throws Exception {

        given(service.listarTodos())
                .willReturn(List.of(responseCadastro()));

        mockMvc.perform(
                        get("/api/cadastros-secundarios")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nomeFantasia")
                        .value("Tech Porto"))
                .andExpect(jsonPath("$[0].cnae.codigo")
                        .value("6201-5/01"));
    }

    private CadastroSecundarioResponse responseCadastro() {

        return new CadastroSecundarioResponse(
                1L,
                "Tech Porto",
                "12345678000199",
                responseCnae()
        );
    }

    private AtividadeEconomicaCnaeResponse responseCnae() {

        return new AtividadeEconomicaCnaeResponse(
                1L,
                "6201-5/01",
                "Desenvolvimento de programas de computador sob encomenda",
                "Tecnologia"
        );
    }
}