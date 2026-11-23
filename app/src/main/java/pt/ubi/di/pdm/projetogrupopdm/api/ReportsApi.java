package pt.ubi.di.pdm.projetogrupopdm.api;

import java.util.List;
import java.util.Map;

import pt.ubi.di.pdm.projetogrupopdm.api.dto.ConfirmacaoRequest;
import pt.ubi.di.pdm.projetogrupopdm.api.dto.ReportRequest;
import pt.ubi.di.pdm.projetogrupopdm.common.model.Report;
import pt.ubi.di.pdm.projetogrupopdm.common.model.TipoReporte;
import pt.ubi.di.pdm.projetogrupopdm.frontoffice.model.Paragens;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ReportsApi {

    @GET("rest/v1/paragens")
    Call<List<Paragens>> getParagens(
            @Query("select") String select,
            @Query("order") String order
    );

    @GET("rest/v1/tipos_reporte")
    Call<List<TipoReporte>> getTiposReporte(
            @Query("select") String select,
            @Query("order") String order
    );

    @GET("rest/v1/reportes")
    Call<List<Report>> getReportes(
            @Query("ativo") String ativoFilter,
            @Query("utilizador_id") String utilizadorFilter,
            @Query("select") String select,
            @Query("order") String order,
            @Query("limit") Integer limit
    );

    @Headers("Prefer: return=representation")
    @POST("rest/v1/reportes")
    Call<List<Report>> criarReporte(
            @Query("select") String select,
            @Body ReportRequest request
    );

    @PATCH("rest/v1/reportes")
    Call<Void> atualizarReporte(
            @Query("id") String idFilter,
            @Body Map<String, Object> campos
    );

    @GET("rest/v1/confirmacoes")
    Call<List<Map<String, Object>>> getConfirmacoes(
            @Query("reporte_id") String reporteFilter,
            @Query("utilizador_id") String utilizadorFilter,
            @Query("select") String select
    );

    @POST("rest/v1/confirmacoes")
    Call<Void> criarConfirmacao(@Body ConfirmacaoRequest request);
}