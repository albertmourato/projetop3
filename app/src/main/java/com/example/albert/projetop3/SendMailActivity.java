package com.example.albert.projetop3;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;

public class SendMailActivity {

    public SendMailActivity() {}

    //Método que envia email
    public static void sendMail(final Context context, final Activity activity) {

        Context mContext = context;
        String fromEmail = "if1001projeto2017.1teste@gmail.com";
        String fromPassword = "projetop3";
        String emailSubject = "Quero Férias";
        //pegar localização
        String emailBody = "Leopoldo, me passe!";
        //TODO pegar localizacao
        List<String> toEmailList = new ArrayList<String>();
        //pode-se adicionar vários emails para receber o alerta com a posição
        toEmailList.add("ams11@cin.ufpe.br");
        toEmailList.add("tpa@cin.ufpe.br");
        Log.d("mandar", "vou mandar o email");
        new SendMailTask(activity).execute(fromEmail,
                fromPassword, toEmailList, emailSubject, emailBody);
        try {
            sleep(1000);
        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
