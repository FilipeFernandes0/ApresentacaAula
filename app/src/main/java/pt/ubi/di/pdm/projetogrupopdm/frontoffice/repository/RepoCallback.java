package pt.ubi.di.pdm.projetogrupopdm.frontoffice.repository;

/** Callback usado pelos repositories do frontoffice (corre na thread principal). */
public interface RepoCallback<T> {
    void onSuccess(T result);

    void onError(String message);
}