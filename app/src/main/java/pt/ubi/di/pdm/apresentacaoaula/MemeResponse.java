package pt.ubi.di.pdm.apresentacaoaula;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MemeResponse {
    @SerializedName("templates")
    private List<MemeTemplate> templates;

    public List<MemeTemplate> getTemplates(){
        return templates;
    }


}
