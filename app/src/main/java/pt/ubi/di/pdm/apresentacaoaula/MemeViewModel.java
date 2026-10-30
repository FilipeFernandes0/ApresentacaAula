package pt.ubi.di.pdm.apresentacaoaula;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonParseException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

public class MemeViewModel extends ViewModel {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<UiState<List<MemeTemplate>>> estado = new MutableLiveData<>();
    private Call<MemeResponse> chamadaAtual;
    public LiveData<UiState<List<MemeTemplate>>> getEstado() {
        return estado;
    }

    //carrega a lista inicial de memes
    public void carregarMemes() {
        iniciar(ClienteRede.getApi().obterMemes());
    }

    //pesquisa de memes pelo os nomes
    public void pesquisar(String termo) {
        iniciar(ClienteRede.getApi().pesquisar(termo));
    }

    // Parte comum: prepara o estado e entrega o pedido ao theread
    private void iniciar(Call<MemeResponse> chamada) {
        if (chamadaAtual != null) chamadaAtual.cancel();   // cancela o pedido anterior (se ainda estiver a correr)
        chamadaAtual = chamada;

        estado.setValue(UiState.loading());                // main thread -> setValue

        executor.execute(() -> executarChamada(chamada));  // a partir daqui: thread de fundo
    }

    // Corre na worker thread: faz o pedido e trata de todos os erros possíveis
    private void executarChamada(Call<MemeResponse> chamada) {
        try {
            Response<MemeResponse> r = chamada.execute();  // chamada síncrona (bloqueia só o ajudante)

            if (r.isSuccessful() && r.body() != null && r.body().getTemplates() != null) {
                //Rodrigo guardar a cahe local aqui (só para a lista inicial, não para as pesquisas)
                estado.postValue(UiState.success(r.body().getTemplates()));   // thread de fundo -> postValue
            } else {
                estado.postValue(UiState.error(mensagemParaCodigo(r.code())));
            }
        } catch (SocketTimeoutException e) {
            estado.postValue(UiState.error("O servidor demorou demasiado a responder"));
        } catch (IOException e) {
            if (chamada.isCanceled()) return;              // cancelada de propósito por nós, não é erro
            //Rodrigo tenta ler a cache
            estado.postValue(UiState.error("Sem ligação à internet"));
        } catch (JsonParseException e) {
            estado.postValue(UiState.error("Resposta do servidor inválida"));
        }
    }

    // Traduz o código HTTP de erro numa mensagem para o utilizador
    private String mensagemParaCodigo(int codigo) {
        switch (codigo) {
            case 400: return "Pedido inválido";
            case 404: return "Nada encontrado";
            case 429: return "Demasiados pedidos. Tenta daqui a um minuto";   // limite de 60 pedidos/min da API
            case 503: return "Serviço temporariamente indisponível";
            default:  return "Erro do servidor (código " + codigo + ")";
        }
    }

    @Override
    protected void onCleared() {
        executor.shutdown();        // deixa de chamar as thereads
        super.onCleared();
    }
}