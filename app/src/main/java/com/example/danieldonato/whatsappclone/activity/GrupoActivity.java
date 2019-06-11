package com.example.danieldonato.whatsappclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;

import com.example.danieldonato.whatsappclone.R;
import com.example.danieldonato.whatsappclone.adapter.ContatosAdapter;
import com.example.danieldonato.whatsappclone.adapter.GrupoSelecionadoAdapter;
import com.example.danieldonato.whatsappclone.config.ConfiguracaoFirebase;
import com.example.danieldonato.whatsappclone.helper.RecyclerItemClickListener;
import com.example.danieldonato.whatsappclone.helper.UsuarioFirebase;
import com.example.danieldonato.whatsappclone.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GrupoActivity extends AppCompatActivity {


    private RecyclerView recyclerMembros, recyclerMembrosSelecionados;
    private ContatosAdapter contatosAdapter;
    private GrupoSelecionadoAdapter grupoSelecionadoAdapter;
    private List<Usuario> listaMembros = new ArrayList<>();
    private List<Usuario> listaMembrosSelecionados = new ArrayList<>();
    private ValueEventListener valueEventListenerMembros;
    private DatabaseReference usuariosRef;
    private FirebaseUser user;
    private Toolbar toolbar;
    private FloatingActionButton fabAvancarCadastro;

    public void atualizarMembrosToolbar(){
        int totalSelecionados = listaMembrosSelecionados.size();
        int total = listaMembros.size() + totalSelecionados;
        toolbar.setTitle("Novo grupo");
        toolbar.setSubtitle(totalSelecionados + " de " + total + " selecionados");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Novo grupo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerMembros = findViewById(R.id.recyclerMembrosGrupo);
        recyclerMembrosSelecionados = findViewById(R.id.recyclerMembrosSelecionados);
        usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");
        user = UsuarioFirebase.getUsuarioAtual();
        fabAvancarCadastro = findViewById(R.id.fabAvancarCadastro);

        //configurar adapter
        contatosAdapter = new ContatosAdapter(listaMembros, getApplicationContext());

        //configura reclycerview contatos
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMembros.setLayoutManager(layoutManager);
        recyclerMembros.setHasFixedSize(true);
        recyclerMembros.setAdapter(contatosAdapter);
        recyclerMembros.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerMembros,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Usuario usuarioSelecionado = listaMembros.get(position);
                                listaMembros.remove(usuarioSelecionado);
                                contatosAdapter.notifyDataSetChanged();
                                listaMembrosSelecionados.add(usuarioSelecionado);
                                grupoSelecionadoAdapter.notifyDataSetChanged();
                                atualizarMembrosToolbar();
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

        //configuracao recyclerview selcionados
        grupoSelecionadoAdapter = new GrupoSelecionadoAdapter(listaMembrosSelecionados, getApplicationContext());

        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerMembrosSelecionados.setLayoutManager(layoutManagerHorizontal);
        recyclerMembrosSelecionados.setHasFixedSize(true);
        recyclerMembrosSelecionados.setAdapter(grupoSelecionadoAdapter);

        recyclerMembrosSelecionados.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                recyclerMembrosSelecionados,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Usuario usuarioSelcionado = listaMembrosSelecionados.get(position);
                        listaMembrosSelecionados.remove(usuarioSelcionado);
                        listaMembros.add(usuarioSelcionado);
                        contatosAdapter.notifyDataSetChanged();
                        grupoSelecionadoAdapter.notifyDataSetChanged();
                        atualizarMembrosToolbar();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        fabAvancarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GrupoActivity.this, CadastroGrupoActivity.class);
                i.putExtra("membros", (Serializable) listaMembrosSelecionados);
                startActivity(i);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListenerMembros);
    }

    public void recuperarContatos(){
        valueEventListenerMembros = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dados : dataSnapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);
                    if(!user.getEmail().equals(usuario.getEmail())){
                        listaMembros.add(usuario);
                    }
                }
                contatosAdapter.notifyDataSetChanged();
                atualizarMembrosToolbar();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
