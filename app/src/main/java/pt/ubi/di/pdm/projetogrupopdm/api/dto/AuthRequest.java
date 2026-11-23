package pt.ubi.di.pdm.projetogrupopdm.api.dto;

import java.util.HashMap;
import java.util.Map;

public class AuthRequest {
    private final String email;
    private final String password;
    private Map<String, Object> data;

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public AuthRequest(String email, String password, String nome, boolean partilharLocalizacao) {
        this.email = email;
        this.password = password;
        this.data = new HashMap<>();
        if (nome != null && !nome.trim().isEmpty()) {
            this.data.put("nome", nome.trim());
        }
        this.data.put("partilhar_localizacao", partilharLocalizacao);
    }
}