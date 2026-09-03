package com.porto.testecnae.repository;

import com.porto.testecnae.domain.AtividadeEconomicaCnae;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AtividadeEconomicaCnaeRepositoryTest {

    @Autowired
    private AtividadeEconomicaCnaeRepository repository;

    @Test
    void deveBuscarCnaeQuandoTermoEstiverNoMeioDaDescricao() {

        var resultado =
                repository.buscarPorDescricao("programas");

        assertThat(resultado)
                .extracting(AtividadeEconomicaCnae::getCodigo)
                .containsExactly(
                        "6201-5/01",
                        "6202-3/00"
                );
    }

    @Test
    void deveBuscarCnaeIgnorandoMaiusculasEMinusculas() {

        var resultado =
                repository.buscarPorDescricao("PROgrAMAS");

        assertThat(resultado)
                .extracting(AtividadeEconomicaCnae::getCodigo)
                .containsExactly(
                        "6201-5/01",
                        "6202-3/00"
                );
    }

    @Test
    void deveRetornarListaVaziaQuandoTermoNaoExistir() {

        var resultado =
                repository.buscarPorDescricao("xyz-inexistente");

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveEncontrarCnaePorCodigo() {

        var resultado =
                repository.findByCodigo("6201-5/01");

        assertThat(resultado).isPresent();

        assertThat(resultado.get().getDescricao())
                .isEqualTo(
                        "Desenvolvimento de programas de computador sob encomenda"
                );
    }

    @Test
    void deveRetornarOptionalVazioParaCodigoInexistente() {

        var resultado =
                repository.findByCodigo("9999-9/99");

        assertThat(resultado).isEmpty();
    }
}