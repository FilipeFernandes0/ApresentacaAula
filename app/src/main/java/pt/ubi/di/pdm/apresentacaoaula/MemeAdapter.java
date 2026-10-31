package pt.ubi.di.pdm.apresentacaoaula;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MemeAdapter extends RecyclerView.Adapter<MemeAdapter.MemeViewHolder> {

    private final List<MemeTemplate> memes = new ArrayList<>();

    public void setMemes(List<MemeTemplate> novos) {
        memes.clear();
        if (novos != null) memes.addAll(novos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meme, parent, false);
        return new MemeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MemeViewHolder holder, int position) {
        MemeTemplate meme = memes.get(position);
        holder.nome.setText(meme.getName());
        Glide.with(holder.imagem)
                .load(meme.getUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.imagem);
    }

    @Override
    public int getItemCount() {
        return memes.size();
    }

    static class MemeViewHolder extends RecyclerView.ViewHolder {
        final ImageView imagem;
        final TextView nome;

        MemeViewHolder(@NonNull View itemView) {
            super(itemView);
            imagem = itemView.findViewById(R.id.imgMeme);
            nome = itemView.findViewById(R.id.txtNomeMeme);
        }
    }
}