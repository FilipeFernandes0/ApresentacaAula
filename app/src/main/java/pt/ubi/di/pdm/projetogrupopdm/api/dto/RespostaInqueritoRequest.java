package pt.ubi.di.pdm.projetogrupopdm.api.dto;

import com.google.gson.annotations.SerializedName;

/** Corpo do INSERT em respostas_inquerito. */
public class RespostaInqueritoRequest {
    @SerializedName("inquerito_id")
    private final long inqueritoId;

    @SerializedName("utilizador_id")
    private final String utilizadorId;

    private final String resposta;

    public RespostaInqueritoRequest(long inqueritoId, String utilizadorId, String resposta) {
        this.inqueritoId = inqueritoId;
        this.utilizadorId = utilizadorId;
        this.resposta = resposta;
    }
}