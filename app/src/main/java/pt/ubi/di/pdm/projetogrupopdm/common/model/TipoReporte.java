package pt.ubi.di.pdm.projetogrupopdm.common.model;

/** Tabela: tipos_reporte (id, nome). Ex.: Atraso, Lotação esgotada, Insegurança... */
public class TipoReporte {
    private long id;
    private String nome;

    public long getId() { return id; }
    public String getNome() { return nome; }
}