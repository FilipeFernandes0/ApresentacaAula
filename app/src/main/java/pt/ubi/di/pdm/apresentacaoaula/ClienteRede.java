package pt.ubi.di.pdm.apresentacaoaula;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClienteRede {
    private static MemeApiService api;

    public static MemeApiService getApi(){
        if(api == null){
            OkHttpClient okHttp = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://justmeme.wtf")
                    .client(okHttp)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            api = retrofit.create(MemeApiService.class);

        }

        return api;
    }
}
