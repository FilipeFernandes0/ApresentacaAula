package pt.ubi.di.pdm.projetogrupopdm.api;

import java.util.List;
import java.util.Map;

import pt.ubi.di.pdm.projetogrupopdm.api.dto.RedeemRequest;
import pt.ubi.di.pdm.projetogrupopdm.api.dto.RespostaInqueritoRequest;
import pt.ubi.di.pdm.projetogrupopdm.common.model.User;
import pt.ubi.di.pdm.projetogrupopdm.frontoffice.model.Inqueritos;
import pt.ubi.di.pdm.projetogrupopdm.frontoffice.model.Recompensas;
import pt.ubi.di.pdm.projetogrupopdm.frontoffice.model.Resgates;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WalletApi {

    @GET("rest/v1/profiles")
    Call<List<User>> getProfile(
            @Query("id") String idFilter,
            @Query("select") String select
    );

    @PATCH("rest/v1/profiles")
    Call<Void> atualizarPontos(
            @Query("id") String idFilter,
            @Body Map<String, Object> campos
    );

    @GET("rest/v1/recompensas")
    Call<List<Recompensas>> getRecompensas(
            @Query("select") String select,
            @Query("order") String order
    );

    @GET("rest/v1/resgates")
    Call<List<Resgates>> getResgates(
            @Query("utilizador_id") String utilizadorFilter,
            @Query("select") String select,
            @Query("order") String order
    );

    @POST("rest/v1/resgates")
    Call<Void> criarResgate(@Body RedeemRequest request);

    @GET("rest/v1/inqueritos")
    Call<List<Inqueritos>> getInqueritos(
            @Query("select") String select,
            @Query("order") String order
    );

    @GET("rest/v1/respostas_inquerito")
    Call<List<Map<String, Object>>> getRespostas(
            @Query("utilizador_id") String utilizadorFilter,
            @Query("select") String select
    );

    @POST("rest/v1/respostas_inquerito")
    Call<Void> criarResposta(@Body RespostaInqueritoRequest request);
}