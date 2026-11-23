package pt.ubi.di.pdm.projetogrupopdm.common.auth;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.List;

import pt.ubi.di.pdm.projetogrupopdm.api.AuthApi;
import pt.ubi.di.pdm.projetogrupopdm.api.SupabaseClient;
import pt.ubi.di.pdm.projetogrupopdm.api.dto.AuthRequest;
import pt.ubi.di.pdm.projetogrupopdm.api.dto.AuthResponse;
import pt.ubi.di.pdm.projetogrupopdm.common.SessionManager;
import pt.ubi.di.pdm.projetogrupopdm.common.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final AuthApi authApi;
    private final SessionManager sessionManager;

    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public interface RegisterCallback {
        void onSuccess();
        void onError(String message);
    }

    public AuthRepository(Context context) {
        this.sessionManager = new SessionManager(context);
        this.authApi = SupabaseClient.getClient(context).create(AuthApi.class);
    }

    public void register(String nome, String email, String password, boolean partilharLocalizacao, RegisterCallback callback) {
        AuthRequest request = new AuthRequest(email, password, nome,partilharLocalizacao );

        authApi.register(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();

                } else if (response.code() == 422 || response.code() == 400) {
                    callback.onError("Dados inválidos ou este email já está registado.");
                } else {
                    callback.onError("Erro no registo (código " + response.code() + ").");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                callback.onError("Erro de ligação: " + t.getMessage());
            }
        });
    }

    public void login(String email, String password, AuthCallback callback) {
        AuthRequest request = new AuthRequest(email, password);

        authApi.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getAccessToken() != null
                        && response.body().getUser() != null) {

                    String token = response.body().getAccessToken();
                    String userId = response.body().getUser().getId();

                    sessionManager.saveToken(token, userId);

                    fetchUserProfile(userId, callback);
                } else if (response.code() == 400) {
                    callback.onError("Email ou password incorretos.");
                } else {
                    callback.onError("Erro ao iniciar sessão (código " + response.code() + ").");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                callback.onError("Erro de ligação: " + t.getMessage());
            }
        });
    }

    private void fetchUserProfile(String userId, AuthCallback callback) {
        authApi.getProfile("eq." + userId, "*").enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    User user = response.body().get(0);
                    sessionManager.saveRole(user.getRole());
                    callback.onSuccess(user);
                } else {
                    sessionManager.clearSession();
                    callback.onError("Não foi possível carregar o perfil do utilizador.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                sessionManager.clearSession();
                callback.onError("Erro ao carregar perfil: " + t.getMessage());
            }
        });
    }

    public void logout() {
        sessionManager.clearSession();
    }
}