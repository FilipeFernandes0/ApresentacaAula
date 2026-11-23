package pt.ubi.di.pdm.projetogrupopdm.backoffice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BackofficeViewModel extends ViewModel {

    private final MutableLiveData<Integer> selectedDays = new MutableLiveData<>(7);

    public LiveData<Integer> getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(int days) {
        Integer current = selectedDays.getValue();
        if (current == null || current != days) {
            selectedDays.setValue(days);
        }
    }
}