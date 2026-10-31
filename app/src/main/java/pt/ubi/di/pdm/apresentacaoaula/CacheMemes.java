package pt.ubi.di.pdm.apresentacaoaula;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/** Cache local: guarda a lista de memes (em JSON) e a data da gravação. */
public class CacheMemes {
    private static final String FICHEIRO = "cache_memes";
    private static final String CHAVE_LISTA = "lista_json";
    private static final String CHAVE_DATA = "guardado_em";
    private static final Type TIPO_LISTA = new TypeToken<List<MemeTemplate>>() {}.getType();

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public CacheMemes(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(FICHEIRO, Context.MODE_PRIVATE);
    }

    /** Serialização: lista de objetos -> texto JSON. */
    public void guardar(List<MemeTemplate> memes) {
        prefs.edit()
                .putString(CHAVE_LISTA, gson.toJson(memes))
                .putLong(CHAVE_DATA, System.currentTimeMillis())
                .apply();
    }

    /** Desserialização: texto JSON -> lista de objetos. Devolve null se não houver cache. */
    public List<MemeTemplate> ler() {
        String json = prefs.getString(CHAVE_LISTA, null);
        if (json == null) return null;
        try {
            List<MemeTemplate> lista = gson.fromJson(json, TIPO_LISTA);
            return (lista == null || lista.isEmpty()) ? null : lista;
        } catch (JsonParseException e) {
            return null;
        }
    }

    public long guardadoEm() {
        return prefs.getLong(CHAVE_DATA, 0L);
    }
}