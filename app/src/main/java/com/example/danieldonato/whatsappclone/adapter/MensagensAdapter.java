package com.example.danieldonato.whatsappclone.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.danieldonato.whatsappclone.R;
import com.example.danieldonato.whatsappclone.helper.UsuarioFirebase;
import com.example.danieldonato.whatsappclone.model.Mensagem;

import java.net.URI;
import java.util.List;

public class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.MyViewHolder> {

    private List<Mensagem> mensagens;
    private Context context;
    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DESTINATARIO = 1;

    public MensagensAdapter(List<Mensagem> lista, Context c) {
        this.mensagens = lista;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View item = null;
        if(viewType == TIPO_REMETENTE){
            item = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_mensagem_remetente, viewGroup, false);
        }else if(viewType == TIPO_DESTINATARIO){
            item = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_mensagem_destinatario, viewGroup, false);
        }
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Mensagem mensagem = mensagens.get(i);
        String msg = mensagem.getMensagem();
        String img = mensagem.getImagem();
        if(img != null){
            Uri url = Uri.parse(img);
            Glide.with(context).load(url).into(myViewHolder.imagem);
            String nome = mensagem.getNome();
            if(!nome.isEmpty()){
                myViewHolder.nome.setText(nome);
            }else{
                myViewHolder.nome.setVisibility(View.GONE);
            }
            //esconder o texto
            myViewHolder.mensagem.setVisibility(View.GONE);
        }else{
            myViewHolder.mensagem.setText(msg);
            String nome = mensagem.getNome();
            if(!nome.isEmpty()){
                myViewHolder.nome.setText(nome);
            }else{
                myViewHolder.nome.setVisibility(View.GONE);
            }
            //esconder imagem
            myViewHolder.imagem.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }

    @Override
    public int getItemViewType(int position) {
        Mensagem mensagem = mensagens.get(position);
        String idUsuario = UsuarioFirebase.getIdentificadorUsuario();
        if(idUsuario.equals(mensagem.getIdUsuario())){
            return TIPO_REMETENTE;
        }
        return TIPO_DESTINATARIO;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView mensagem, nome;
        ImageView imagem;
        public MyViewHolder(View view) {
            super(view);
            mensagem = view.findViewById(R.id.textMensagemText);
            imagem = view.findViewById(R.id.imageMensagemFoto);
            nome = view.findViewById(R.id.textNomeExibicao);
        }

    }
}
