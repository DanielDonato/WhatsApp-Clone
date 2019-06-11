package com.example.danieldonato.whatsappclone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.danieldonato.whatsappclone.R;
import com.example.danieldonato.whatsappclone.activity.ChatActivity;
import com.example.danieldonato.whatsappclone.activity.GrupoActivity;
import com.example.danieldonato.whatsappclone.adapter.ContatosAdapter;
import com.example.danieldonato.whatsappclone.config.ConfiguracaoFirebase;
import com.example.danieldonato.whatsappclone.helper.RecyclerItemClickListener;
import com.example.danieldonato.whatsappclone.helper.UsuarioFirebase;
import com.example.danieldonato.whatsappclone.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private RecyclerView recyclerViewListaContatos;
    private ContatosAdapter adapter;
    private List<Usuario> listaContatos = new ArrayList<>();
    private DatabaseReference usuariosRef;
    private ValueEventListener valueEventListenerContatos;
    FirebaseUser user;


    public ContatosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contatos, container, false);
        recyclerViewListaContatos = view.findViewById(R.id.recyclerViewListaContatos);
        usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");
        user = UsuarioFirebase.getUsuarioAtual();

        //configurando adapter
        adapter = new ContatosAdapter(listaContatos, getActivity());

        //configurando reciclyer view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewListaContatos.setLayoutManager(layoutManager);
        recyclerViewListaContatos.setHasFixedSize(true);
        recyclerViewListaContatos.setAdapter(adapter);

        recyclerViewListaContatos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerViewListaContatos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Usuario usuarioSelecionado = listaContatos.get(position);
                                boolean cabecalho = usuarioSelecionado.getEmail().isEmpty();
                                if(cabecalho){
                                    Intent i = new Intent(getActivity(), GrupoActivity.class);
                                    startActivity(i);
                                }else {
                                    Intent i = new Intent(getActivity(), ChatActivity.class);
                                    i.putExtra("chatContato", usuarioSelecionado);
                                    startActivity(i);
                                }
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );

        Usuario itemGrupo = new Usuario();
        itemGrupo.setNome("Novo Grupo");
        itemGrupo.setEmail("");
        listaContatos.add(itemGrupo);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListenerContatos);
    }

    public void recuperarContatos(){
        valueEventListenerContatos = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dados : dataSnapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);
                    if(!user.getEmail().equals(usuario.getEmail())){
                        listaContatos.add(usuario);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
