package com.porto.testecnae.dto;

import com.porto.testecnae.domain.AtividadeEconomicaCnae;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AtividadeEconomicaCnaeResponseTest {

    @Test
    void deveConverterEntidadeParaResponse() {

        var entidade = AtividadeEconomicaCnae.builder()
                .id(1L)
                .codigo("6201-5/01")
                .descricao(
                        "Desenvolvimento de programas de computador sob encomenda"
                )
                .secao("Tecnologia")
                .build();

        var response =
                AtividadeEconomicaCnaeResponse.fromEntity(entidade);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.codigo()).isEqualTo("6201-5/01");
        assertThat(response.descricao())
                .isEqualTo(
                        "Desenvolvimento de programas de computador sob encomenda"
                );
        assertThat(response.secao()).isEqualTo("Tecnologia");
    }
}