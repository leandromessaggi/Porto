package com.porto.testecnae.repository;

import com.porto.testecnae.domain.AtividadeEconomicaCnae;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AtividadeEconomicaCnaeRepositoryTest {

    @Autowired
    private AtividadeEconomicaCnaeRepository repository;

    @Test
    void deveBuscarCnaeQuandoTermoEstiverNoMeioDaDescricao() {

        List<AtividadeEconomicaCnae> resultado =
                repository.buscarPorDescricao("programas");

        assertThat(resultado)
                .isNotEmpty()
                .allMatch(cnae ->
                        cnae.getDescricao()
                                .toLowerCase()
                                .contains("programas"));
    }

    @Test
    void deveBuscarCnaeIgnorandoMaiusculasEMinusculas() {

        var resultado = repository.buscarPorDescricao("PROgrAMAS");

        assertThat(resultado).isNotEmpty();
    }
}