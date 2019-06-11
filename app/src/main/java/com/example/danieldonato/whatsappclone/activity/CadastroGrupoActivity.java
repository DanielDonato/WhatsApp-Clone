package com.example.danieldonato.whatsappclone.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.danieldonato.whatsappclone.R;
import com.example.danieldonato.whatsappclone.adapter.GrupoSelecionadoAdapter;
import com.example.danieldonato.whatsappclone.config.ConfiguracaoFirebase;
import com.example.danieldonato.whatsappclone.helper.UsuarioFirebase;
import com.example.danieldonato.whatsappclone.model.Grupo;
import com.example.danieldonato.whatsappclone.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CadastroGrupoActivity extends AppCompatActivity {

    private List<Usuario> listaMembrosSelecionados = new ArrayList<>();
    private TextView textTotalParticipantes;
    private GrupoSelecionadoAdapter grupoSelecionadoAdapter;
    private RecyclerView recyclerMembrosSelecionados;
    private CircleImageView imageGrupo;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private FloatingActionButton fabSalvarGrupo;
    private Grupo grupo;
    private EditText editNomeGrupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_grupo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo grupo");
        toolbar.setSubtitle("Defina o nome");
        setSupportActionBar(toolbar);

        fabSalvarGrupo = findViewById(R.id.fabSalvarGrupo);
        textTotalParticipantes = findViewById(R.id.textTotalParticipantes);
        recyclerMembrosSelecionados = findViewById(R.id.recyclerMembrosNovoGrupo);
        imageGrupo = findViewById(R.id.imageGrupo);
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        editNomeGrupo = findViewById(R.id.editNomeGrupo);

        grupo = new Grupo();

        imageGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getExtras() != null){
            List<Usuario> membros = (List<Usuario>) getIntent().getExtras().getSerializable("membros");
            listaMembrosSelecionados.addAll(membros);
            textTotalParticipantes.setText("Participantes: " + listaMembrosSelecionados.size());
        }

        //configurar o recycler view
        grupoSelecionadoAdapter = new GrupoSelecionadoAdapter(listaMembrosSelecionados, getApplicationContext());

        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerMembrosSelecionados.setLayoutManager(layoutManagerHorizontal);
        recyclerMembrosSelecionados.setHasFixedSize(true);
        recyclerMembrosSelecionados.setAdapter(grupoSelecionadoAdapter);

        fabSalvarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomeGrupo = editNomeGrupo.getText().toString();
                listaMembrosSelecionados.add(UsuarioFirebase.getDadosUsuarioLogado());
                grupo.setMembros(listaMembrosSelecionados);
                grupo.setNome(nomeGrupo);
                grupo.salvar();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Bitmap imagem = null;
            try{
                Uri localImagemSelecionada = data.getData();
                imagem = MediaStore.Images.Media.getBitmap( getContentResolver(), localImagemSelecionada);
            }catch (Exception e){
                e.printStackTrace();
            }

            if(imagem != null){
                imageGrupo.setImageBitmap(imagem);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] dadosImagem = baos.toByteArray();
                StorageReference imagemRef = storageReference.child("imagens").child("grupos").child(grupo.getId() + ".jpeg");
                UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CadastroGrupoActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(CadastroGrupoActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        String uri = taskSnapshot.getDownloadUrl().toString();
                        grupo.setFoto(uri);
                    }
                });
            }
        }
    }
}
