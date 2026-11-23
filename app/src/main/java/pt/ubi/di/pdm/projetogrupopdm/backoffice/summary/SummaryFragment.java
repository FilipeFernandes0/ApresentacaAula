package pt.ubi.di.pdm.projetogrupopdm.backoffice.summary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import pt.ubi.di.pdm.projetogrupopdm.R;
import pt.ubi.di.pdm.projetogrupopdm.backoffice.BackofficeViewModel;

public class SummaryFragment extends Fragment {

    private BackofficeViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(BackofficeViewModel.class);
        viewModel.getSelectedDays().observe(getViewLifecycleOwner(), days -> {
            // Próximo passo: atualizar os cartões do Resumo para "days" (7, 30 ou 180)
        });
    }
}