package pt.ubi.di.pdm.projetogrupopdm.frontoffice.model;

public class PointTransaction {
    private final String titulo;
    private final int pontos;
    private final String createdAt;

    public PointTransaction(String titulo, int pontos, String createdAt) {
        this.titulo = titulo;
        this.pontos = pontos;
        this.createdAt = createdAt == null ? "" : createdAt;
    }

    public String getTitulo() { return titulo; }
    public int getPontos() { return pontos; }
    public String getCreatedAt() { return createdAt; }
}