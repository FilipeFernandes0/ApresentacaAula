package pt.ubi.di.pdm.apresentacaoaula;

public class UiState<T> {
    public enum Status {LOADING, SUCCESS, ERROR}

    public final Status status;
    public final T data;
    public final String message;

    private UiState(Status status, T data, String message){
        this.status = status;   // Atribui o estado da operação (LOADING, SUCCESS ou ERROR)
        this.data = data;       // Atribui os dados recebidos (se existirem)
        this.message = message; // Atribui a mensagem associada (por exemplo, mensagem de erro)
    }

    public static <T> UiState<T> loading(){
        return new UiState<>(Status.LOADING, null, null); // Retorna uma nova instância com o estado LOADING
    }

    public static <T> UiState<T> success(T data){
        return new UiState<>(Status.SUCCESS, data, null); // Retorna uma nova instância com o estado SUCCESS e os dados
    }

    public static <T> UiState<T> error(String message){
        return new UiState<>(Status.ERROR, null, message); // Retorna uma nova instância com o estado ERROR e a mensagem
    }
}
