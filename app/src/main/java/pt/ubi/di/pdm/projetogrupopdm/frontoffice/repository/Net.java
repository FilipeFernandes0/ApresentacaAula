package pt.ubi.di.pdm.projetogrupopdm.frontoffice.repository;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Evita repetir o tratamento de erros HTTP e de ligação em cada pedido. */
final class Net {

    interface OnSuccess<T> {
        void run(T body);
    }

    private Net() {
    }

    static <T> Callback<T> handle(RepoCallback<?> fail, String errorMessage, OnSuccess<T> ok) {
        return new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (response.isSuccessful()) {
                    ok.run(response.body());
                } else if (response.code() == 401) {
                    fail.onError("Sessão expirada. Inicia sessão novamente.");
                } else {
                    fail.onError(errorMessage + " (código " + response.code() + ").");
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                fail.onError("Erro de ligação: " + t.getMessage());
            }
        };
    }
}