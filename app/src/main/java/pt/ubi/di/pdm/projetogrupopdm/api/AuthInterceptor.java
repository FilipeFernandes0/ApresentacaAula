package pt.ubi.di.pdm.projetogrupopdm.api;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import pt.ubi.di.pdm.projetogrupopdm.common.Constants;
import pt.ubi.di.pdm.projetogrupopdm.common.SessionManager;

public class AuthInterceptor implements Interceptor {
    private final SessionManager sessionManager;

    public AuthInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder()
                .addHeader("apikey", Constants.SUPABASE_KEY)
                .addHeader("Content-Type", "application/json");

        String token = sessionManager.getToken();
        if (token != null && !token.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + token);
        }

        return chain.proceed(builder.build());
    }
}