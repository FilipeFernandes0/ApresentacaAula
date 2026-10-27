package pt.ubi.di.pdm.apresentacaoaula;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
public interface MemeApiService {
    @GET("https://justmeme.wtf/api/v1/templates")
    Call<MemeResponse> obterMemes();


}
