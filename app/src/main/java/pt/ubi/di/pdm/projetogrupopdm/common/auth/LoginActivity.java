package pt.ubi.di.pdm.projetogrupopdm.common.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import pt.ubi.di.pdm.projetogrupopdm.MainActivity;
import pt.ubi.di.pdm.projetogrupopdm.R;
import pt.ubi.di.pdm.projetogrupopdm.backoffice.BackofficeActivity;
import pt.ubi.di.pdm.projetogrupopdm.common.auth.AuthRepository;
import pt.ubi.di.pdm.projetogrupopdm.common.SessionManager;
import pt.ubi.di.pdm.projetogrupopdm.common.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnEntrar;
    private ProgressBar progressBar;
    private AuthRepository authRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            redirecionarPorRole(sessionManager.getRole());
            return;
        }

        setContentView(R.layout.activity_login);
        getWindow().setStatusBarColor(android.graphics.Color.parseColor("#0B2545"));

        authRepository = new AuthRepository(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnEntrar = findViewById(R.id.btnEntrar);
        progressBar = findViewById(R.id.progressBar);
        TextView tvIrParaRegisto = findViewById(R.id.btnIrParaRegisto);

        btnEntrar.setOnClickListener(v -> tentarLogin());
        tvIrParaRegisto.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void tentarLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Indica um email válido.");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Indica a tua palavra-passe.");
            etPassword.requestFocus();
            return;
        }

        setLoading(true);

        authRepository.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                setLoading(false);
                Toast.makeText(LoginActivity.this,
                        "Bem-vindo, " + user.getNome() + " (" + user.getRole() + ")",
                        Toast.LENGTH_SHORT).show();
                redirecionarPorRole(user.getRole());
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void redirecionarPorRole(String role) {
        Intent intent;

        if ("camara".equalsIgnoreCase(role)) {
            intent = new Intent(this, BackofficeActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        intent.putExtra("USER_ROLE", role);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnEntrar.setEnabled(!isLoading);
    }
}