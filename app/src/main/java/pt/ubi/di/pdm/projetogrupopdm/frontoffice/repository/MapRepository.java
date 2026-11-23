package pt.ubi.di.pdm.projetogrupopdm.frontoffice.repository;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ubi.di.pdm.projetogrupopdm.api.ReportsApi;
import pt.ubi.di.pdm.projetogrupopdm.api.SupabaseClient;
import pt.ubi.di.pdm.projetogrupopdm.api.dto.ConfirmacaoRequest;
import pt.ubi.di.pdm.projetogrupopdm.api.dto.ReportRequest;
import pt.ubi.di.pdm.projetogrupopdm.common.Constants;
import pt.ubi.di.pdm.projetogrupopdm.common.SessionManager;
import pt.ubi.di.pdm.projetogrupopdm.common.model.Report;
import pt.ubi.di.pdm.projetogrupopdm.common.model.TipoReporte;
import pt.ubi.di.pdm.projetogrupopdm.frontoffice.model.Paragens;

/** Mapa: paragens, tipos de reporte e reportes da comunidade. */
public class MapRepository {

    private static final String REPORT_SELECT = "*,tipos_reporte(id,nome),paragens(id,nome)";

    private final ReportsApi reportsApi;
    private final WalletRepository walletRepository;
    private final SessionManager sessionManager;

    public MapRepository(Context context) {
        this.reportsApi = SupabaseClient.getClient(context).create(ReportsApi.class);
        this.walletRepository = new WalletRepository(context);
        this.sessionManager = new SessionManager(context);
    }

    private String userId() {
        return sessionManager.getUserId();
    }

    public void getStops(RepoCallback<List<Paragens>> callback) {
        reportsApi.getParagens("*", "id.asc").enqueue(
                Net.handle(callback, "Não foi possível carregar as paragens", list ->
                        callback.onSuccess(list == null ? new ArrayList<>() : list)));
    }

    public void getReportTypes(RepoCallback<List<TipoReporte>> callback) {
        reportsApi.getTiposReporte("*", "id.asc").enqueue(
                Net.handle(callback, "Não foi possível carregar os tipos de reporte", list ->
                        callback.onSuccess(list == null ? new ArrayList<>() : list)));
    }

    /** Reportes ativos, do mais recente para o mais antigo. */
    public void getActiveReports(RepoCallback<List<Report>> callback) {
        reportsApi.getReportes("eq.true", null, REPORT_SELECT, "created_at.desc", 50).enqueue(
                Net.handle(callback, "Não foi possível carregar os reportes", list ->
                        callback.onSuccess(list == null ? new ArrayList<>() : list)));
    }

    /** Cria o reporte e atribui os U-Points. Devolve o reporte criado. */
    public void createReport(long paragemId, long tipoId, String nota, RepoCallback<Report> callback) {
        ReportRequest request = new ReportRequest(userId(), paragemId, tipoId, nota);
        reportsApi.criarReporte(REPORT_SELECT, request).enqueue(
                Net.handle(callback, "Não foi possível enviar o reporte", list -> {
                    Report created = (list == null || list.isEmpty()) ? null : list.get(0);
                    // O reporte já existe; se os pontos falharem não o escondemos do utilizador.
                    walletRepository.addPoints(Constants.POINTS_REPORT, new RepoCallback<Integer>() {
                        @Override
                        public void onSuccess(Integer balance) {
                            callback.onSuccess(created);
                        }

                        @Override
                        public void onError(String message) {
                            callback.onSuccess(created);
                        }
                    });
                }));
    }

    /** Confirma um reporte de outro utilizador. Devolve o novo número de confirmações. */
    public void confirmReport(Report report, RepoCallback<Integer> callback) {
        String uid = userId();
        if (uid != null && uid.equals(report.getUtilizadorId())) {
            callback.onError("Não podes confirmar o teu próprio reporte.");
            return;
        }

        reportsApi.getConfirmacoes("eq." + report.getId(), "eq." + uid, "id").enqueue(
                Net.handle(callback, "Não foi possível validar a confirmação", existing -> {
                    if (existing != null && !existing.isEmpty()) {
                        callback.onError("Já confirmaste este reporte.");
                        return;
                    }
                    reportsApi.criarConfirmacao(new ConfirmacaoRequest(report.getId(), uid)).enqueue(
                            Net.handle(callback, "Não foi possível confirmar o reporte", v -> {
                                int novoTotal = report.getConfirmacoes() + 1;
                                Map<String, Object> body = new HashMap<>();
                                body.put("confirmacoes", novoTotal);
                                reportsApi.atualizarReporte("eq." + report.getId(), body).enqueue(
                                        Net.handle(callback, "Não foi possível atualizar o reporte", v2 ->
                                                walletRepository.addPoints(Constants.POINTS_CONFIRM,
                                                        new RepoCallback<Integer>() {
                                                            @Override
                                                            public void onSuccess(Integer balance) {
                                                                callback.onSuccess(novoTotal);
                                                            }

                                                            @Override
                                                            public void onError(String message) {
                                                                callback.onSuccess(novoTotal);
                                                            }
                                                        })));
                            }));
                }));
    }

    /** "Já não existe": desativa o reporte. */
    public void dismissReport(Report report, RepoCallback<Void> callback) {
        Map<String, Object> body = new HashMap<>();
        body.put("ativo", false);
        reportsApi.atualizarReporte("eq." + report.getId(), body).enqueue(
                Net.handle(callback, "Não foi possível atualizar o reporte", v ->
                        callback.onSuccess(null)));
    }
}