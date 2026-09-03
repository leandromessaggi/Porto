package com.porto.testecnae.service;

import com.porto.testecnae.domain.AtividadeEconomicaCnae;
import com.porto.testecnae.dto.AtividadeEconomicaCnaeResponse;
import com.porto.testecnae.exception.CnaeNotFoundException;
import com.porto.testecnae.repository.AtividadeEconomicaCnaeRepository;
import com.porto.testecnae.service.impl.AtividadeEconomicaCnaeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtividadeEconomicaCnaeServiceImplTest {

    @Mock
    private AtividadeEconomicaCnaeRepository repository;

    @InjectMocks
    private AtividadeEconomicaCnaeServiceImpl service;

    @Test
    void deveListarTodosOsCnaes() {

        var cnae1 = criarCnae(
                1L,
                "6201-5/01",
                "Desenvolvimento de programas de computador sob encomenda",
                "Tecnologia"
        );

        var cnae2 = criarCnae(
                2L,
                "6202-3/00",
                "Desenvolvimento e licenciamento de programas customizaveis",
                "Tecnologia"
        );

        when(repository.findAll())
                .thenReturn(List.of(cnae1, cnae2));

        var resultado = service.listarTodas();

        assertThat(resultado).hasSize(2);

        assertThat(resultado)
                .extracting(AtividadeEconomicaCnaeResponse::codigo)
                .containsExactly(
                        "6201-5/01",
                        "6202-3/00"
                );

        verify(repository).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremCnaes() {

        when(repository.findAll())
                .thenReturn(List.of());

        var resultado = service.listarTodas();

        assertThat(resultado).isEmpty();

        verify(repository).findAll();
    }

    @Test
    void deveBuscarCnaePorDescricao() {

        String termo = "programas";

        var cnae = criarCnae(
                1L,
                "6201-5/01",
                "Desenvolvimento de programas de computador sob encomenda",
                "Tecnologia"
        );

        when(repository.buscarPorDescricao(termo))
                .thenReturn(List.of(cnae));

        var resultado = service.buscarPorDescricao(termo);

        assertThat(resultado).hasSize(1);

        assertThat(resultado.getFirst().codigo())
                .isEqualTo("6201-5/01");

        assertThat(resultado.getFirst().descricao())
                .containsIgnoringCase(termo);

        verify(repository).buscarPorDescricao(termo);
    }

    @Test
    void deveRetornarListaVaziaQuandoDescricaoNaoForEncontrada() {

        String termo = "inexistente";

        when(repository.buscarPorDescricao(termo))
                .thenReturn(List.of());

        var resultado = service.buscarPorDescricao(termo);

        assertThat(resultado).isEmpty();

        verify(repository).buscarPorDescricao(termo);
    }

    @Test
    void deveBuscarCnaePorCodigoQuandoExistir() {

        String codigo = "6201-5/01";

        var cnae = criarCnae(
                1L,
                codigo,
                "Desenvolvimento de programas de computador sob encomenda",
                "Tecnologia"
        );

        when(repository.findByCodigo(codigo))
                .thenReturn(Optional.of(cnae));

        var resultado = service.buscarPorCodigo(codigo);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.codigo()).isEqualTo(codigo);
        assertThat(resultado.descricao())
                .isEqualTo("Desenvolvimento de programas de computador sob encomenda");
        assertThat(resultado.secao()).isEqualTo("Tecnologia");

        verify(repository).findByCodigo(codigo);
    }

    @Test
    void deveLancarExcecaoQuandoCnaeNaoExistir() {

        String codigo = "9999-9/99";

        when(repository.findByCodigo(codigo))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorCodigo(codigo))
                .isInstanceOf(CnaeNotFoundException.class)
                .hasMessage("CNAE não encontrado para o código: " + codigo);

        verify(repository).findByCodigo(codigo);
    }

    private AtividadeEconomicaCnae criarCnae(
            Long id,
            String codigo,
            String descricao,
            String secao) {

        return AtividadeEconomicaCnae.builder()
                .id(id)
                .codigo(codigo)
                .descricao(descricao)
                .secao(secao)
                .build();
    }
}