package pt.ubi.di.pdm.projetogrupopdm.common;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "utransit_session";
    private static final String KEY_TOKEN = "access_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ROLE = "user_role";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token, String userId) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_USER_ID, userId)
                .apply();
    }

    public void saveRole(String role) {
        prefs.edit()
                .putString(KEY_ROLE, role)
                .apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, "cidadao");
    }

    public boolean isLoggedIn() {
        return getToken() != null && !getToken().isEmpty();
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}