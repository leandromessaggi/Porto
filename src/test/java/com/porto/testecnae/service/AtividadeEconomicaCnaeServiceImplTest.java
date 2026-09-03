package com.porto.testecnae.service;

import com.porto.testecnae.exception.CnaeNotFoundException;
import com.porto.testecnae.repository.AtividadeEconomicaCnaeRepository;
import com.porto.testecnae.service.impl.AtividadeEconomicaCnaeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtividadeEconomicaCnaeServiceImplTest {

    @Mock
    private AtividadeEconomicaCnaeRepository repository;

    @InjectMocks
    private AtividadeEconomicaCnaeServiceImpl service;

    @Test
    void deveLancarExcecaoQuandoCnaeNaoExistir() {

        String codigo = "9999-9/99";

        when(repository.findByCodigo(codigo))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorCodigo(codigo))
                .isInstanceOf(CnaeNotFoundException.class)
                .hasMessageContaining(codigo);
    }
}