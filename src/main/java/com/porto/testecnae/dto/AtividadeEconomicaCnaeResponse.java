package com.porto.testecnae.dto;

import com.porto.testecnae.domain.AtividadeEconomicaCnae;

public record AtividadeEconomicaCnaeResponse(
        Long id,
        String codigo,
        String descricao,
        String secao
) {

    public static AtividadeEconomicaCnaeResponse fromEntity(AtividadeEconomicaCnae atividade) {
        return new AtividadeEconomicaCnaeResponse(
                atividade.getId(),
                atividade.getCodigo(),
                atividade.getDescricao(),
                atividade.getSecao()
        );
    }
}
