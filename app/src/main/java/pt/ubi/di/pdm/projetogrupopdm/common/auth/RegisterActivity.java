package pt.ubi.di.pdm.projetogrupopdm.common.auth;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import pt.ubi.di.pdm.projetogrupopdm.R;
import pt.ubi.di.pdm.projetogrupopdm.common.auth.AuthRepository;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNome, etEmail, etPassword, etConfirmPassword;
    private CheckBox cbPartilharLocalizacao;
    private Button btnCriarConta;
    private ProgressBar progressBar;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepository = new AuthRepository(this);

        ImageButton btnBack = findViewById(R.id.btnBack);
        etNome = findViewById(R.id.etNome);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbPartilharLocalizacao = findViewById(R.id.cbPartilharLocalizacao);
        btnCriarConta = findViewById(R.id.btnCriarConta);
        progressBar = findViewById(R.id.progressBar);
        TextView tvIrParaLogin = findViewById(R.id.tvIrParaLogin);

        btnBack.setOnClickListener(v -> finish());
        tvIrParaLogin.setOnClickListener(v -> finish()); // Volta ao LoginActivity
        btnCriarConta.setOnClickListener(v -> tentarRegisto());
    }

    private void tentarRegisto() {
        String nome = etNome.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        boolean partilharLocalizacao = cbPartilharLocalizacao.isChecked();

        if (nome.isEmpty()) {
            etNome.setError("Indica o teu nome.");
            etNome.requestFocus();
            return;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Indica um email válido.");
            etEmail.requestFocus();
            return;
        }

        if (!isPasswordValida(password)) {
            etPassword.setError("Mínimo 8 caracteres, com pelo menos uma letra e um número.");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("As palavras-passe não coincidem.");
            etConfirmPassword.requestFocus();
            return;
        }

        setLoading(true);

        authRepository.register(nome, email, password, partilharLocalizacao, new AuthRepository.RegisterCallback() {
            @Override
            public void onSuccess() {
                setLoading(false);
                Toast.makeText(RegisterActivity.this,
                        "Conta criada com sucesso! Já podes iniciar sessão.",
                        Toast.LENGTH_LONG).show();
                finish(); // Regressa ao LoginActivity
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isPasswordValida(String password) {
        return password.length() >= 8
                && password.matches(".*[a-zA-Z].*")
                && password.matches(".*\\d.*");
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnCriarConta.setEnabled(!isLoading);
    }
}