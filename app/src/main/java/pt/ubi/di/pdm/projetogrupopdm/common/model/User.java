package pt.ubi.di.pdm.projetogrupopdm.common.model;

import com.google.gson.annotations.SerializedName;

public class User {
    private String id;
    private String nome;
    private String email;
    private String role;

    @SerializedName("u_points")
    private int uPoints;
    @SerializedName("partilhar_localizacao")
    private boolean partilharLocalizacao;


    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public int getUPoints() {
        return uPoints;
    }
    public boolean isPartilharLocalizacao() {
        return partilharLocalizacao;
    }

}