package pt.ubi.di.pdm.projetogrupopdm.frontoffice.model;
import com.google.gson.annotations.SerializedName;

public class Resgates {
    private long id;
    private String codigo;

    @SerializedName("created_at")
    private String createdAt;

    private Recompensas recompensas;

    public long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getCreatedAt() { return createdAt; }
    public Recompensas getRecompensa() { return recompensas; }
}
