package pt.ubi.di.pdm.apresentacaoaula;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface MemeApiService {
    @GET("api/v1/templates")
    Call<MemeResponse> obterMemes();

    @GET("api/v1/templates/search")
    Call<MemeResponse> pesquisar(@Query("q") String termo);

}
