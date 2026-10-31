package pt.ubi.di.pdm.apresentacaoaula;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String CHAVE_MODO = "modo_pesquisa";
    private static final String CHAVE_TERMO = "ultimo_termo";
    private LinearProgressIndicator barraProgresso;
    private CircularProgressIndicator progressoCentral;
    private TextView bannerAviso;
    private RecyclerView listaMemes;
    private View estadoErro;
    private TextView txtErro;
    private EditText campoPesquisa;

    private MemeViewModel vm;
    private MemeAdapter adapter;
    private CacheMemes cache;

    private boolean modoPesquisa = false;   // false = lista inicial, true = resultados de pesquisa
    private String ultimoTermo = "";
    private UiState<List<MemeTemplate>> ultimoGuardado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        if (savedInstanceState != null) {   // rotação do ecrã
            modoPesquisa = savedInstanceState.getBoolean(CHAVE_MODO, false);
            ultimoTermo = savedInstanceState.getString(CHAVE_TERMO, "");
        }

        barraProgresso = findViewById(R.id.barraProgresso);
        progressoCentral = findViewById(R.id.progressoCentral);
        bannerAviso = findViewById(R.id.bannerAviso);
        listaMemes = findViewById(R.id.listaMemes);
        estadoErro = findViewById(R.id.estadoErro);
        txtErro = findViewById(R.id.txtErro);
        campoPesquisa = findViewById(R.id.campoPesquisa);

        adapter = new MemeAdapter();
        listaMemes.setLayoutManager(new LinearLayoutManager(this));
        listaMemes.setAdapter(adapter);

        cache = new CacheMemes(this);

        // Pesquisa: botão ou Enter no teclado
        ((Button) findViewById(R.id.btnPesquisar)).setOnClickListener(v -> pesquisar());
        campoPesquisa.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                pesquisar();
                return true;
            }
            return false;
        });
        ((Button) findViewById(R.id.btnTentarNovamente)).setOnClickListener(v -> repetir());

        // Observer: o ecrã reage sempre ao estado do ViewModel
        vm = new ViewModelProvider(this).get(MemeViewModel.class);
        vm.getEstado().observe(this, this::aoMudarEstado);

        if (vm.getEstado().getValue() == null) vm.carregarMemes();   // rotação não repete o pedido
    }

    // ---------- Estados ----------

    private void aoMudarEstado(UiState<List<MemeTemplate>> estado) {
        switch (estado.status) {
            case LOADING:
                mostrarLoading();
                break;
            case SUCCESS:
                mostrarSucesso(estado);
                break;
            case ERROR:
                mostrarErro(estado.message);
                break;
        }
    }

    private void mostrarLoading() {
        bannerAviso.setVisibility(View.GONE);
        estadoErro.setVisibility(View.GONE);

        // Cache primeiro: se a lista está vazia, mostra já os dados guardados
        if (!modoPesquisa && adapter.getItemCount() == 0) {
            List<MemeTemplate> guardados = cache.ler();
            if (guardados != null) adapter.setMemes(guardados);
        }
        listaMemes.setVisibility(View.VISIBLE);

        barraProgresso.setVisibility(View.VISIBLE);
        progressoCentral.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private void mostrarSucesso(UiState<List<MemeTemplate>> estado) {
        esconderProgresso();
        bannerAviso.setVisibility(View.GONE);

        if (estado.data == null || estado.data.isEmpty()) {
            adapter.setMemes(null);
            listaMemes.setVisibility(View.GONE);
            txtErro.setText(R.string.sem_resultados);
            estadoErro.setVisibility(View.VISIBLE);
            return;
        }

        estadoErro.setVisibility(View.GONE);
        listaMemes.setVisibility(View.VISIBLE);
        adapter.setMemes(estado.data);

        // Só guardamos a lista inicial; uma pesquisa nunca sobrescreve a cache
        if (!modoPesquisa && estado != ultimoGuardado) {
            cache.guardar(estado.data);
            ultimoGuardado = estado;
        }
    }

    private void mostrarErro(String mensagem) {
        esconderProgresso();

        // Fallback: erro na lista inicial + existe cache -> mantém os dados e avisa
        if (!modoPesquisa) {
            List<MemeTemplate> guardados = cache.ler();
            if (guardados != null) {
                adapter.setMemes(guardados);
                estadoErro.setVisibility(View.GONE);
                listaMemes.setVisibility(View.VISIBLE);

                String aviso = mensagem;
                long quando = cache.guardadoEm();
                if (quando > 0) {
                    CharSequence relativo = DateUtils.getRelativeTimeSpanString(
                            quando, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
                    aviso = getString(R.string.aviso_cache, mensagem, relativo);
                }
                bannerAviso.setText(aviso);
                bannerAviso.setVisibility(View.VISIBLE);
                return;
            }
        }

        // Sem cache (ou erro numa pesquisa): ecrã de erro com "Tentar novamente"
        bannerAviso.setVisibility(View.GONE);
        listaMemes.setVisibility(View.GONE);
        txtErro.setText(mensagem);
        estadoErro.setVisibility(View.VISIBLE);
    }

    private void esconderProgresso() {
        barraProgresso.setVisibility(View.GONE);
        progressoCentral.setVisibility(View.GONE);
    }

    // ---------- Ações ----------

    private void pesquisar() {
        String texto = campoPesquisa.getText().toString().trim();
        esconderTeclado();

        if (texto.isEmpty()) {              // caixa vazia -> volta à lista inicial
            modoPesquisa = false;
            vm.carregarMemes();
        } else if (texto.length() < 2) {    // a API dá erro 400 com menos de 2 letras
            campoPesquisa.setError(getString(R.string.erro_pesquisa_curta));
        } else {
            modoPesquisa = true;
            ultimoTermo = texto;
            vm.pesquisar(texto);
        }
    }

    private void repetir() {                // "Tentar novamente" repete a última ação
        if (modoPesquisa) vm.pesquisar(ultimoTermo);
        else vm.carregarMemes();
    }

    private void esconderTeclado() {
        InputMethodManager imm = getSystemService(InputMethodManager.class);
        if (imm != null) imm.hideSoftInputFromWindow(campoPesquisa.getWindowToken(), 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CHAVE_MODO, modoPesquisa);
        outState.putString(CHAVE_TERMO, ultimoTermo);
    }
}