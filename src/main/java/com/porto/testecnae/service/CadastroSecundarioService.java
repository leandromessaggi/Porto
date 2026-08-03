package com.porto.testecnae.service;

import com.porto.testecnae.dto.CadastroSecundarioRequest;
import com.porto.testecnae.dto.CadastroSecundarioResponse;
import com.porto.testecnae.dto.AtividadeEconomicaCnaeResponse;

import java.util.List;

public interface CadastroSecundarioService {

    CadastroSecundarioResponse cadastrar(CadastroSecundarioRequest request);

    AtividadeEconomicaCnaeResponse validarCnae(String codigoCnae);

    List<CadastroSecundarioResponse> listarTodos();
}
