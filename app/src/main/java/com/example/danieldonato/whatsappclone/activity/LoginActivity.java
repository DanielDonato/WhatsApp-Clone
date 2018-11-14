package com.example.danieldonato.whatsappclone.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.danieldonato.whatsappclone.R;
import com.example.danieldonato.whatsappclone.config.ConfiguracaoFirebase;
import com.example.danieldonato.whatsappclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText campoEmail, campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);
    }

    public void logarUsuario(Usuario usuario){
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        abrirTelaPrincipal();
                    } else {
                        String excecao = "";
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            excecao = "Usuario não esta cadastrado";
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            excecao = "E-mail e senha não correspondem";
                        } catch (Exception e) {
                            excecao = "Erro ao logar usuario" + e.getMessage();
                            e.printStackTrace();
                        }
                        Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    public void validarAutenticacaoUsuario(View view){
        String email = campoEmail.getText().toString();
        String senha = campoSenha.getText().toString();
        if(!email.isEmpty()){
            if(!senha.isEmpty()){
                Usuario user = new Usuario();
                user.setEmail(email);
                user.setSenha(senha);
                logarUsuario(user);
            }else {
                Toast.makeText(LoginActivity.this, "Preencha a senha", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(LoginActivity.this, "Preencha o email", Toast.LENGTH_SHORT).show();
        }

    }

    public void abrirTelaCastro(View view){
        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);
    }

    public void abrirTelaPrincipal(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

}
