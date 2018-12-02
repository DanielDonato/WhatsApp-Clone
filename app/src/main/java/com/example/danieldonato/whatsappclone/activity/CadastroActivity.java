package com.example.danieldonato.whatsappclone.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.danieldonato.whatsappclone.R;
import com.example.danieldonato.whatsappclone.config.ConfiguracaoFirebase;
import com.example.danieldonato.whatsappclone.helper.Base64Custom;
import com.example.danieldonato.whatsappclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText campoNome, campoEmail, campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
    }

    public void cadastrarUsuario(final Usuario usuario){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(
            usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar usuario", Toast.LENGTH_SHORT).show();
                    finish();
                    try{
                        String identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                        usuario.setId(identificadorUsuario);
                        usuario.salvar();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else {
                    String excecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        excecao = "Digite uma senha mais forte";
                    }catch (FirebaseAuthInvalidCredentialsException e) {
                        excecao = "Digite um email valido";
                    }catch (FirebaseAuthUserCollisionException e) {
                        excecao = "Essa conta ja foi cadastrada";
                    }catch (Exception e) {
                        excecao = "Erro ao cadastrar usuario" + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void validarCadastroUsuario(View view) {
        String nome = campoNome.getText().toString();
        String email = campoEmail.getText().toString();
        String senha = campoSenha.getText().toString();

        if(!nome.isEmpty()){
            if(!email.isEmpty()){
                if(!senha.isEmpty()){
                    Usuario usuario = new Usuario(nome, email, senha);
                    cadastrarUsuario(usuario);
                }else {
                    Toast.makeText(CadastroActivity.this, "Preencha a senha", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(CadastroActivity.this, "Preencha o email", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(CadastroActivity.this, "Preencha o nome", Toast.LENGTH_SHORT).show();
        }

    }

}
