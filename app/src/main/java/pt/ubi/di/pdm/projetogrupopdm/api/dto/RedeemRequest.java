package pt.ubi.di.pdm.projetogrupopdm.api.dto;

import com.google.gson.annotations.SerializedName;

/** Corpo do INSERT em resgates. */
public class RedeemRequest {
    @SerializedName("utilizador_id")
    private final String utilizadorId;

    @SerializedName("recompensa_id")
    private final long recompensaId;

    private final String codigo;

    public RedeemRequest(String utilizadorId, long recompensaId, String codigo) {
        this.utilizadorId = utilizadorId;
        this.recompensaId = recompensaId;
        this.codigo = codigo;
    }
}