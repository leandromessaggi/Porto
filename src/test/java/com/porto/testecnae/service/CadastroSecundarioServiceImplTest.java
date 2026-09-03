package com.porto.testecnae.service;

import com.porto.testecnae.domain.AtividadeEconomicaCnae;
import com.porto.testecnae.domain.CadastroSecundario;
import com.porto.testecnae.dto.CadastroSecundarioRequest;
import com.porto.testecnae.dto.CadastroSecundarioResponse;
import com.porto.testecnae.exception.CnaeNotFoundException;
import com.porto.testecnae.repository.AtividadeEconomicaCnaeRepository;
import com.porto.testecnae.repository.CadastroSecundarioRepository;
import com.porto.testecnae.service.impl.CadastroSecundarioServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
    void deveCadastrarQuandoCnaeExistir() {

        String codigo = "6201-5/01";

        var request = new CadastroSecundarioRequest(
                "Tech Porto",
                "12345678000199",
                codigo
        );

        var cnae = criarCnae(codigo);

        when(cnaeRepository.findByCodigo(codigo))
                .thenReturn(Optional.of(cnae));

        when(repository.save(any(CadastroSecundario.class)))
                .thenAnswer(invocation -> {

                    CadastroSecundario cadastro =
                            invocation.getArgument(0);

                    cadastro.setId(10L);

                    return cadastro;
                });

        var resultado = service.cadastrar(request);

        assertThat(resultado.id()).isEqualTo(10L);
        assertThat(resultado.nomeFantasia()).isEqualTo("Tech Porto");
        assertThat(resultado.documento()).isEqualTo("12345678000199");
        assertThat(resultado.cnae().codigo()).isEqualTo(codigo);

        ArgumentCaptor<CadastroSecundario> captor =
                ArgumentCaptor.forClass(CadastroSecundario.class);

        verify(repository).save(captor.capture());

        CadastroSecundario salvo = captor.getValue();

        assertThat(salvo.getNomeFantasia()).isEqualTo("Tech Porto");
        assertThat(salvo.getDocumento()).isEqualTo("12345678000199");
        assertThat(salvo.getCnae()).isSameAs(cnae);

        verify(cnaeRepository).findByCodigo(codigo);
    }

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

        verify(cnaeRepository).findByCodigo(codigo);

        verify(repository, never())
                .save(any(CadastroSecundario.class));
    }

    @Test
    void deveValidarCnaeQuandoExistir() {

        String codigo = "6201-5/01";

        var cnae = criarCnae(codigo);

        when(cnaeRepository.findByCodigo(codigo))
                .thenReturn(Optional.of(cnae));

        var resultado = service.validarCnae(codigo);

        assertThat(resultado.codigo()).isEqualTo(codigo);
        assertThat(resultado.descricao())
                .isEqualTo(
                        "Desenvolvimento de programas de computador sob encomenda"
                );
        assertThat(resultado.secao()).isEqualTo("Tecnologia");

        verify(cnaeRepository).findByCodigo(codigo);
    }

    @Test
    void deveLancarExcecaoAoValidarCnaeInexistente() {

        String codigo = "9999-9/99";

        when(cnaeRepository.findByCodigo(codigo))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.validarCnae(codigo))
                .isInstanceOf(CnaeNotFoundException.class)
                .hasMessage("CNAE não encontrado para o código: " + codigo);

        verify(cnaeRepository).findByCodigo(codigo);
    }

    @Test
    void deveListarTodosOsCadastrosSecundarios() {

        var cnae = criarCnae("6201-5/01");

        var cadastro1 = CadastroSecundario.builder()
                .id(1L)
                .nomeFantasia("Tech Porto")
                .documento("12345678000199")
                .cnae(cnae)
                .build();

        var cadastro2 = CadastroSecundario.builder()
                .id(2L)
                .nomeFantasia("Porto Sistemas")
                .documento("98765432000199")
                .cnae(cnae)
                .build();

        when(repository.findAll())
                .thenReturn(List.of(cadastro1, cadastro2));

        var resultado = service.listarTodos();

        assertThat(resultado).hasSize(2);

        assertThat(resultado)
                .extracting(CadastroSecundarioResponse::nomeFantasia)
                .containsExactly(
                        "Tech Porto",
                        "Porto Sistemas"
                );

        verify(repository).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremCadastros() {

        when(repository.findAll())
                .thenReturn(List.of());

        var resultado = service.listarTodos();

        assertThat(resultado).isEmpty();

        verify(repository).findAll();
    }

    private AtividadeEconomicaCnae criarCnae(String codigo) {

        return AtividadeEconomicaCnae.builder()
                .id(1L)
                .codigo(codigo)
                .descricao(
                        "Desenvolvimento de programas de computador sob encomenda"
                )
                .secao("Tecnologia")
                .build();
    }
}