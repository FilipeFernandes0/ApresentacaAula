package pt.ubi.di.pdm.projetogrupopdm.api;
import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import pt.ubi.di.pdm.projetogrupopdm.common.Constants;
import pt.ubi.di.pdm.projetogrupopdm.common.SessionManager;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class SupabaseClient {
    private static Retrofit retrofit;

    public static Retrofit getClient(Context context){
        if(retrofit == null){
            SessionManager sessionManager = new SessionManager(context);
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(sessionManager))
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.SUPABASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
