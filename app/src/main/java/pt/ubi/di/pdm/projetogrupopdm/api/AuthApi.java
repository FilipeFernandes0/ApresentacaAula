package pt.ubi.di.pdm.projetogrupopdm.api;

import java.util.List;

import pt.ubi.di.pdm.projetogrupopdm.api.dto.AuthRequest;
import pt.ubi.di.pdm.projetogrupopdm.api.dto.AuthResponse;
import pt.ubi.di.pdm.projetogrupopdm.common.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApi {

    @POST("auth/v1/signup")
    Call<AuthResponse> register(@Body AuthRequest request);

    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> login(@Body AuthRequest request);

    @GET("rest/v1/profiles")
    Call<List<User>> getProfile(
            @Query("id") String idFilter,
            @Query("select") String select
    );
}