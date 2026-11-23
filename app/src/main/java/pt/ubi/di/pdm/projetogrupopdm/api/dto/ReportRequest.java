package pt.ubi.di.pdm.projetogrupopdm.api.dto;

import com.google.gson.annotations.SerializedName;

/** Corpo do INSERT em reportes. */
public class ReportRequest {
    @SerializedName("utilizador_id")
    private final String utilizadorId;

    @SerializedName("paragem_id")
    private final long paragemId;

    @SerializedName("tipo_id")
    private final long tipoId;

    private final String nota;
    private final int confirmacoes = 0;
    private final boolean ativo = true;

    public ReportRequest(String utilizadorId, long paragemId, long tipoId, String nota) {
        this.utilizadorId = utilizadorId;
        this.paragemId = paragemId;
        this.tipoId = tipoId;
        this.nota = (nota == null || nota.trim().isEmpty()) ? null : nota.trim();
    }
}