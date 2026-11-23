package pt.ubi.di.pdm.projetogrupopdm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pt.ubi.di.pdm.projetogrupopdm.common.auth.AuthRepository;
import pt.ubi.di.pdm.projetogrupopdm.common.SessionManager;
import pt.ubi.di.pdm.projetogrupopdm.common.auth.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SessionManager sessionManager = new SessionManager(this);
        AuthRepository authRepository = new AuthRepository(this);

        TextView tvRoleInfo = findViewById(R.id.tvRoleInfo);
        Button btnLogout = findViewById(R.id.btnLogout);

        String role = getIntent().getStringExtra("USER_ROLE");
        if (role == null) {
            role = sessionManager.getRole();
        }

        if ("camara".equalsIgnoreCase(role)) {
            tvRoleInfo.setText("Backoffice\n(Role: camara)");
        } else {
            tvRoleInfo.setText("Frontoffice\n(Role: cidadao)");
        }

        btnLogout.setOnClickListener(v -> {
            authRepository.logout();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}