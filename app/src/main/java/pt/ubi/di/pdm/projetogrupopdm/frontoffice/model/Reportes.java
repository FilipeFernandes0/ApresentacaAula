package pt.ubi.di.pdm.projetogrupopdm.frontoffice.model;
import com.google.gson.annotations.SerializedName;

public class Reportes {
    private long id;

    @SerializedName("utilizador_id")
    private String utilizadorId;

    @SerializedName("paragem_id")
    private Long paragemId;

    @SerializedName("tipo_id")
    private Long tipoId;

    private String nota;
    private int confirmacoes;
    private Boolean ativo;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("tipos_reporte")
    private TipoReporte tipo;

    @SerializedName("paragens")
    private ParagemRef paragem;

    public long getId() { return id; }
    public String getUtilizadorId() { return utilizadorId; }
    public Long getParagemId() { return paragemId; }
    public Long getTipoId() { return tipoId; }
    public String getNota() { return nota; }
    public int getConfirmacoes() { return confirmacoes; }
    public boolean isAtivo() { return ativo == null || ativo; }
    public String getCreatedAt() { return createdAt; }

    /** Pode ser null se o pedido não incluiu tipos_reporte(...). */
    public TipoReporte getTipo() { return tipo; }

    public String getTipoNome() {
        return tipo == null ? null : tipo.getNome();
    }

    public String getParagemNome() {
        return paragem == null ? null : paragem.nome;
    }

    private static class ParagemRef {
        private String nome;
    }
}
