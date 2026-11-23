package pt.ubi.di.pdm.projetogrupopdm.frontoffice.repository;

import android.content.Context;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.ubi.di.pdm.projetogrupopdm.api.ReportsApi;
import pt.ubi.di.pdm.projetogrupopdm.api.SupabaseClient;
import pt.ubi.di.pdm.projetogrupopdm.api.WalletApi;
import pt.ubi.di.pdm.projetogrupopdm.api.dto.RedeemRequest;
import pt.ubi.di.pdm.projetogrupopdm.api.dto.RespostaInqueritoRequest;
import pt.ubi.di.pdm.projetogrupopdm.common.Constants;
import pt.ubi.di.pdm.projetogrupopdm.common.SessionManager;
import pt.ubi.di.pdm.projetogrupopdm.common.model.Report;
import pt.ubi.di.pdm.projetogrupopdm.common.model.User;
import pt.ubi.di.pdm.projetogrupopdm.frontoffice.model.Inqueritos;
import pt.ubi.di.pdm.projetogrupopdm.frontoffice.model.PointTransaction;
import pt.ubi.di.pdm.projetogrupopdm.frontoffice.model.Recompensas;
import pt.ubi.di.pdm.projetogrupopdm.frontoffice.model.Resgates;
import retrofit2.Retrofit;

/** Carteira: saldo, recompensas, resgates, movimentos e inquéritos flash. */
public class WalletRepository {

    private static final String CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static class RedeemResult {
        private final String codigo;
        private final int saldoDepois;

        RedeemResult(String codigo, int saldoDepois) {
            this.codigo = codigo;
            this.saldoDepois = saldoDepois;
        }

        public String getCodigo() {
            return codigo;
        }

        public int getSaldoDepois() {
            return saldoDepois;
        }
    }

    private final WalletApi walletApi;
    private final ReportsApi reportsApi;
    private final SessionManager sessionManager;

    public WalletRepository(Context context) {
        Retrofit retrofit = SupabaseClient.getClient(context);
        this.walletApi = retrofit.create(WalletApi.class);
        this.reportsApi = retrofit.create(ReportsApi.class);
        this.sessionManager = new SessionManager(context);
    }

    private String userId() {
        return sessionManager.getUserId();
    }

    // ---------------------------------------------------------------- perfil / saldo

    public void getProfile(RepoCallback<User> callback) {
        walletApi.getProfile("eq." + userId(), "*").enqueue(
                Net.handle(callback, "Não foi possível carregar o perfil", list -> {
                    if (list == null || list.isEmpty()) {
                        callback.onError("Perfil não encontrado.");
                    } else {
                        callback.onSuccess(list.get(0));
                    }
                }));
    }

    /** Soma (ou subtrai, se negativo) pontos ao saldo e devolve o novo saldo. */
    public void addPoints(int delta, RepoCallback<Integer> callback) {
        getProfile(new RepoCallback<User>() {
            @Override
            public void onSuccess(User user) {
                setPoints(Math.max(0, user.getUPoints() + delta), callback);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    private void setPoints(int newBalance, RepoCallback<Integer> callback) {
        Map<String, Object> body = new HashMap<>();
        body.put("u_points", newBalance);
        walletApi.atualizarPontos("eq." + userId(), body).enqueue(
                Net.handle(callback, "Não foi possível atualizar os pontos",
                        v -> callback.onSuccess(newBalance)));
    }

    // ---------------------------------------------------------------- recompensas

    public void getRewards(RepoCallback<List<Recompensas>> callback) {
        walletApi.getRecompensas("*", "custo_pontos.asc").enqueue(
                Net.handle(callback, "Não foi possível carregar as recompensas", list ->
                        callback.onSuccess(list == null ? new ArrayList<>() : list)));
    }

    public void redeem(Recompensas reward, RepoCallback<RedeemResult> callback) {
        getProfile(new RepoCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (user.getUPoints() < reward.getCustoPontos()) {
                    callback.onError("Não tens U-Points suficientes.");
                    return;
                }
                String codigo = generateCode();
                int saldoDepois = user.getUPoints() - reward.getCustoPontos();
                RedeemRequest request = new RedeemRequest(userId(), reward.getId(), codigo);

                walletApi.criarResgate(request).enqueue(
                        Net.handle(callback, "Não foi possível trocar a recompensa", v ->
                                setPoints(saldoDepois, new RepoCallback<Integer>() {
                                    @Override
                                    public void onSuccess(Integer saldo) {
                                        callback.onSuccess(new RedeemResult(codigo, saldo));
                                    }

                                    @Override
                                    public void onError(String message) {
                                        callback.onError(message);
                                    }
                                })));
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    private static String generateCode() {
        StringBuilder sb = new StringBuilder("UT-");
        for (int i = 0; i < 5; i++) {
            sb.append(CODE_ALPHABET.charAt(RANDOM.nextInt(CODE_ALPHABET.length())));
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------- movimentos

    /**
     * Não há tabela de movimentos: junta resgates (-), respostas a inquéritos (+),
     * reportes criados (+) e confirmações (+), ordenados do mais recente para o mais antigo.
     */
    public void getTransactions(RepoCallback<List<PointTransaction>> callback) {
        String uid = "eq." + userId();
        List<PointTransaction> all = new ArrayList<>();
        int[] pending = {4};
        boolean[] failed = {false};

        RepoCallback<Object> fail = new RepoCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
            }

            @Override
            public void onError(String message) {
                if (!failed[0]) {
                    failed[0] = true;
                    callback.onError(message);
                }
            }
        };

        Runnable finishOne = () -> {
            if (failed[0]) return;
            pending[0]--;
            if (pending[0] == 0) {
                Collections.sort(all, (a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
                callback.onSuccess(all);
            }
        };

        walletApi.getResgates(uid, "id,codigo,created_at,recompensas(nome,custo_pontos)", "created_at.desc")
                .enqueue(Net.handle(fail, "Não foi possível carregar os resgates", list -> {
                    if (list != null) {
                        for (Resgates r : list) {
                            Recompensas rw = r.getRecompensa();
                            String nome = rw == null ? "Recompensa" : rw.getNome();
                            int custo = rw == null ? 0 : rw.getCustoPontos();
                            all.add(new PointTransaction(nome, -custo, r.getCreatedAt()));
                        }
                    }
                    finishOne.run();
                }));

        walletApi.getRespostas(uid, "created_at")
                .enqueue(Net.handle(fail, "Não foi possível carregar os inquéritos", list -> {
                    if (list != null) {
                        for (Map<String, Object> row : list) {
                            all.add(new PointTransaction("Inquérito Flash respondido",
                                    Constants.POINTS_SURVEY, asString(row.get("created_at"))));
                        }
                    }
                    finishOne.run();
                }));

        reportsApi.getReportes(null, uid, "id,created_at,tipos_reporte(id,nome)", "created_at.desc", null)
                .enqueue(Net.handle(fail, "Não foi possível carregar os reportes", list -> {
                    if (list != null) {
                        for (Report r : list) {
                            String tipo = r.getTipoNome();
                            all.add(new PointTransaction(
                                    tipo == null ? "Reporte enviado" : "Reporte enviado: " + tipo,
                                    Constants.POINTS_REPORT, r.getCreatedAt()));
                        }
                    }
                    finishOne.run();
                }));

        reportsApi.getConfirmacoes(null, uid, "created_at")
                .enqueue(Net.handle(fail, "Não foi possível carregar as confirmações", list -> {
                    if (list != null) {
                        for (Map<String, Object> row : list) {
                            all.add(new PointTransaction("Reporte confirmado",
                                    Constants.POINTS_CONFIRM, asString(row.get("created_at"))));
                        }
                    }
                    finishOne.run();
                }));
    }

    // ---------------------------------------------------------------- inquérito flash

    /** Primeiro inquérito que o utilizador ainda não respondeu; onSuccess(null) se não houver. */
    public void getPendingSurvey(RepoCallback<Inqueritos> callback) {
        walletApi.getRespostas("eq." + userId(), "inquerito_id").enqueue(
                Net.handle(callback, "Não foi possível carregar as respostas", respostas -> {
                    Set<Long> answered = new HashSet<>();
                    if (respostas != null) {
                        for (Map<String, Object> row : respostas) {
                            Object id = row.get("inquerito_id");
                            if (id instanceof Number) answered.add(((Number) id).longValue());
                        }
                    }
                    walletApi.getInqueritos("*,paragens(id,nome)", "id.asc").enqueue(
                            Net.handle(callback, "Não foi possível carregar o inquérito", surveys -> {
                                if (surveys != null) {
                                    for (Inqueritos s : surveys) {
                                        if (!answered.contains(s.getId())) {
                                            callback.onSuccess(s);
                                            return;
                                        }
                                    }
                                }
                                callback.onSuccess(null);
                            }));
                }));
    }

    /** Guarda a resposta e atribui os U-Points; devolve o novo saldo. */
    public void answerSurvey(Inqueritos survey, String resposta, RepoCallback<Integer> callback) {
        RespostaInqueritoRequest request =
                new RespostaInqueritoRequest(survey.getId(), userId(), resposta);
        walletApi.criarResposta(request).enqueue(
                Net.handle(callback, "Não foi possível enviar a resposta", v ->
                        addPoints(Constants.POINTS_SURVEY, callback)));
    }

    private static String asString(Object o) {
        return o == null ? null : o.toString();
    }
}