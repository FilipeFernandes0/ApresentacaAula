package pt.ubi.di.pdm.apresentacaoaula;

import com.google.gson.annotations.SerializedName;

public class MemeTemplate {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("url")
    private String url;

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getUrl(){
        return url;
    }




}
