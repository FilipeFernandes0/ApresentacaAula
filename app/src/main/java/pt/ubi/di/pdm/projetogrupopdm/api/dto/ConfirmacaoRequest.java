package pt.ubi.di.pdm.projetogrupopdm.api.dto;

import com.google.gson.annotations.SerializedName;

/** Corpo do INSERT em confirmacoes. */
public class ConfirmacaoRequest {
    @SerializedName("reporte_id")
    private final long reporteId;

    @SerializedName("utilizador_id")
    private final String utilizadorId;

    public ConfirmacaoRequest(long reporteId, String utilizadorId) {
        this.reporteId = reporteId;
        this.utilizadorId = utilizadorId;
    }
}