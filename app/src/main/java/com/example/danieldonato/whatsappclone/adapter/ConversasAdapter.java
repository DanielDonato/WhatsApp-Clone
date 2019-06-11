package com.example.danieldonato.whatsappclone.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.danieldonato.whatsappclone.R;
import com.example.danieldonato.whatsappclone.model.Conversa;
import com.example.danieldonato.whatsappclone.model.Grupo;
import com.example.danieldonato.whatsappclone.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.MyViewHolder> {

    private List<Conversa> conversas;
    private Context context;

    public ConversasAdapter(List<Conversa> conversas, Context context) {
        this.conversas = conversas;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemLista = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_contatos, viewGroup, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Conversa conversa = conversas.get(i);
        myViewHolder.ultimaMensagem.setText(conversa.getUltimaMensagem());
        if(conversa.getIsGroup().equals("true")){
            Grupo grupo = conversa.getGrupo();
            myViewHolder.nome.setText(grupo.getNome());
            if (grupo.getFoto() != null) {
                Uri uri = Uri.parse(grupo.getFoto());
                Glide.with(context).load(uri).into(myViewHolder.foto);
            } else {
                myViewHolder.foto.setImageResource(R.drawable.padrao);
            }
        }else {
            Usuario usuario = conversa.getUsuarioExibicao();
            myViewHolder.nome.setText(usuario.getNome());

            if (usuario.getFoto() != null) {
                Uri uri = Uri.parse(usuario.getFoto());
                Glide.with(context).load(uri).into(myViewHolder.foto);
            } else {
                myViewHolder.foto.setImageResource(R.drawable.padrao);
            }
        }
    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView foto;
        TextView nome, ultimaMensagem;

        public MyViewHolder(View itemView){
            super(itemView);
            foto = itemView.findViewById(R.id.imageViewFotoContato);
            nome = itemView.findViewById(R.id.textNomeContato);
            ultimaMensagem = itemView.findViewById(R.id.textEmailContato);
        }

    }
}
