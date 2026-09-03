package com.porto.testecnae.dto;

import com.porto.testecnae.domain.AtividadeEconomicaCnae;
import com.porto.testecnae.domain.CadastroSecundario;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CadastroSecundarioResponseTest {

    @Test
    void deveConverterCadastroParaResponse() {

        var cnae = AtividadeEconomicaCnae.builder()
                .id(1L)
                .codigo("6201-5/01")
                .descricao(
                        "Desenvolvimento de programas de computador sob encomenda"
                )
                .secao("Tecnologia")
                .build();

        var cadastro = CadastroSecundario.builder()
                .id(10L)
                .nomeFantasia("Tech Porto")
                .documento("12345678000199")
                .cnae(cnae)
                .build();

        var response =
                CadastroSecundarioResponse.fromEntity(cadastro);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.nomeFantasia()).isEqualTo("Tech Porto");
        assertThat(response.documento())
                .isEqualTo("12345678000199");

        assertThat(response.cnae()).isNotNull();
        assertThat(response.cnae().id()).isEqualTo(1L);
        assertThat(response.cnae().codigo()).isEqualTo("6201-5/01");
        assertThat(response.cnae().secao()).isEqualTo("Tecnologia");
    }
}