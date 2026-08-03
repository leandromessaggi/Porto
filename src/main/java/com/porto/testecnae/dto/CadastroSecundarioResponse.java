package com.porto.testecnae.dto;

import com.porto.testecnae.domain.CadastroSecundario;

public record CadastroSecundarioResponse(
        Long id,
        String nomeFantasia,
        String documento,
        AtividadeEconomicaCnaeResponse cnae
) {

    public static CadastroSecundarioResponse fromEntity(CadastroSecundario cadastro) {
        return new CadastroSecundarioResponse(
                cadastro.getId(),
                cadastro.getNomeFantasia(),
                cadastro.getDocumento(),
                AtividadeEconomicaCnaeResponse.fromEntity(cadastro.getCnae())
        );
    }
}
