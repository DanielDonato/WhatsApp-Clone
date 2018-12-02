package com.example.danieldonato.whatsappclone.helper;

import com.example.danieldonato.whatsappclone.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class UsuarioFirebase {

    public static String getIdentificadorUsuario() {
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAuth();
        String email = usuario.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(email);
        return idUsuario;
    }
}
