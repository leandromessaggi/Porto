package com.porto.testecnae.service;

import com.porto.testecnae.dto.CadastroSecundarioRequest;
import com.porto.testecnae.exception.CnaeNotFoundException;
import com.porto.testecnae.repository.AtividadeEconomicaCnaeRepository;
import com.porto.testecnae.repository.CadastroSecundarioRepository;
import com.porto.testecnae.service.impl.CadastroSecundarioServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastroSecundarioServiceImplTest {

    @Mock
    private CadastroSecundarioRepository repository;

    @Mock
    private AtividadeEconomicaCnaeRepository cnaeRepository;

    @InjectMocks
    private CadastroSecundarioServiceImpl service;

    @Test
    void naoDeveCadastrarQuandoCnaeNaoExistir() {

        String codigo = "9999-9/99";

        var request = new CadastroSecundarioRequest(
                "Empresa Invalida",
                "99999999000199",
                codigo
        );

        when(cnaeRepository.findByCodigo(codigo))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cadastrar(request))
                .isInstanceOf(CnaeNotFoundException.class)
                .hasMessageContaining(codigo);

        verify(repository, never()).save(
                org.mockito.ArgumentMatchers.any()
        );
    }
}