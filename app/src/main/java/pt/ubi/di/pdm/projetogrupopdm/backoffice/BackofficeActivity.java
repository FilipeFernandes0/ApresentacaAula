package pt.ubi.di.pdm.projetogrupopdm.backoffice;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import pt.ubi.di.pdm.projetogrupopdm.R;
import pt.ubi.di.pdm.projetogrupopdm.common.auth.AuthRepository;
import pt.ubi.di.pdm.projetogrupopdm.backoffice.heatmap.HeatmapFragment;
import pt.ubi.di.pdm.projetogrupopdm.backoffice.summary.SummaryFragment;
import pt.ubi.di.pdm.projetogrupopdm.backoffice.tokens.TokenFragment;
import pt.ubi.di.pdm.projetogrupopdm.common.auth.LoginActivity;

public class BackofficeActivity extends AppCompatActivity {

    private TextView tab7Dias, tab30Dias, tabSemestre;
    private BackofficeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backoffice);
        getWindow().setStatusBarColor(Color.parseColor("#0B2545"));

        // Cria ou recupera o ViewModel partilhado com os 3 Fragments
        viewModel = new ViewModelProvider(this).get(BackofficeViewModel.class);
        AuthRepository authRepository = new AuthRepository(this);

        ImageButton btnLogout = findViewById(R.id.btnLogout);
        tab7Dias = findViewById(R.id.tab7Dias);
        tab30Dias = findViewById(R.id.tab30Dias);
        tabSemestre = findViewById(R.id.tabSemestre);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavBackoffice);

        btnLogout.setOnClickListener(v -> {
            authRepository.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        tab7Dias.setOnClickListener(v -> viewModel.setSelectedDays(7));
        tab30Dias.setOnClickListener(v -> viewModel.setSelectedDays(30));
        tabSemestre.setOnClickListener(v -> viewModel.setSelectedDays(180));

        // Observa o período: mantém o visual certo mesmo após rodar o ecrã
        viewModel.getSelectedDays().observe(this, days -> {
            atualizarEstiloTab(tab7Dias, days == 7);
            atualizarEstiloTab(tab30Dias, days == 30);
            atualizarEstiloTab(tabSemestre, days == 180);
        });

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_summary) {
                abrirFragment(new SummaryFragment());
                return true;
            } else if (id == R.id.nav_heatmap) {
                abrirFragment(new HeatmapFragment());
                return true;
            } else if (id == R.id.nav_tokens) {
                abrirFragment(new TokenFragment());
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_summary);
        }
    }

    private void abrirFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void atualizarEstiloTab(TextView tab, boolean selected) {
        if (selected) {
            tab.setBackgroundResource(R.drawable.bg_period_selected);
            tab.setTextColor(Color.parseColor("#7A869A"));
            tab.setTypeface(null, Typeface.BOLD);
        } else {
            tab.setBackground(null);
            tab.setTextColor(Color.WHITE);
            tab.setTypeface(null, Typeface.NORMAL);
        }
    }
}