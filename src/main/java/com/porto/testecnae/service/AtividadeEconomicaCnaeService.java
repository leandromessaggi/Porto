package com.porto.testecnae.service;

import com.porto.testecnae.dto.AtividadeEconomicaCnaeResponse;

import java.util.List;

public interface AtividadeEconomicaCnaeService {

    List<AtividadeEconomicaCnaeResponse> listarTodas();

    List<AtividadeEconomicaCnaeResponse> buscarPorDescricao(String termo);

    AtividadeEconomicaCnaeResponse buscarPorCodigo(String codigo);
}
