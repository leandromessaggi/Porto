package com.porto.testecnae.integration;

import com.porto.testecnae.repository.CadastroSecundarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CnaeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CadastroSecundarioRepository cadastroSecundarioRepository;

    @BeforeEach
    void limparCadastros() {
        cadastroSecundarioRepository.deleteAll();
    }

    @Test
    void deveListarTodosOsCnaes() throws Exception {

        mockMvc.perform(
                        get("/api/cnaes")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

    }

    @Test
    void deveBuscarCnaesContendoTermoIgnorandoMaiusculasEMinusculas()
            throws Exception {

        mockMvc.perform(
                        get("/api/cnaes/buscar")
                                .param("termo", "PROGRAMAS")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].codigo")
                        .value("6201-5/01"))
                .andExpect(jsonPath("$[1].codigo")
                        .value("6202-3/00"));
    }

    @Test
    void deveBuscarCnaeExistentePorCodigo() throws Exception {

        mockMvc.perform(
                        get("/api/cnaes/codigo")
                                .param("codigo", "6201-5/01")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo")
                        .value("6201-5/01"))
                .andExpect(jsonPath("$.descricao")
                        .value(
                                "Desenvolvimento de programas de computador sob encomenda"
                        ));
    }

    @Test
    void deveRetornar404ParaCnaeInexistente() throws Exception {

        mockMvc.perform(
                        get("/api/cnaes/codigo")
                                .param("codigo", "9999-9/99")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title")
                        .value("CNAE não encontrado"))
                .andExpect(jsonPath("$.detail")
                        .value(
                                "CNAE não encontrado para o código: 9999-9/99"
                        ));
    }

    @Test
    void deveValidarCnaeExistente() throws Exception {

        mockMvc.perform(
                        get(
                                "/api/cadastros-secundarios/validar-cnae"
                        )
                                .param(
                                        "codigoCnae",
                                        "6201-5/01"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo")
                        .value("6201-5/01"));
    }

    @Test
    void deveRetornar404AoValidarCnaeInexistente()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/cadastros-secundarios/validar-cnae"
                        )
                                .param(
                                        "codigoCnae",
                                        "9999-9/99"
                                )
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveCadastrarQuandoCnaeExistir() throws Exception {

        assertThat(cadastroSecundarioRepository.count())
                .isZero();

        mockMvc.perform(
                        post("/api/cadastros-secundarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "nomeFantasia": "Tech Porto",
                                          "documento": "12345678000199",
                                          "codigoCnae": "6201-5/01"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeFantasia")
                        .value("Tech Porto"))
                .andExpect(jsonPath("$.documento")
                        .value("12345678000199"))
                .andExpect(jsonPath("$.cnae.codigo")
                        .value("6201-5/01"));

        assertThat(cadastroSecundarioRepository.count())
                .isEqualTo(1);
    }

    @Test
    void naoDevePersistirCadastroQuandoCnaeNaoExistir()
            throws Exception {

        long quantidadeAntes =
                cadastroSecundarioRepository.count();

        mockMvc.perform(
                        post("/api/cadastros-secundarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "nomeFantasia": "Empresa Invalida",
                                          "documento": "99999999000199",
                                          "codigoCnae": "9999-9/99"
                                        }
                                        """)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title")
                        .value("CNAE não encontrado"));

        long quantidadeDepois =
                cadastroSecundarioRepository.count();

        assertThat(quantidadeDepois)
                .isEqualTo(quantidadeAntes);
    }

    @Test
    void deveListarCadastroCriado() throws Exception {

        mockMvc.perform(
                        post("/api/cadastros-secundarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "nomeFantasia": "Tech Porto",
                                          "documento": "12345678000199",
                                          "codigoCnae": "6201-5/01"
                                        }
                                        """)
                )
                .andExpect(status().isCreated());

        mockMvc.perform(
                        get("/api/cadastros-secundarios")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nomeFantasia")
                        .value("Tech Porto"))
                .andExpect(jsonPath("$[0].documento")
                        .value("12345678000199"))
                .andExpect(jsonPath("$[0].cnae.codigo")
                        .value("6201-5/01"));
    }
}