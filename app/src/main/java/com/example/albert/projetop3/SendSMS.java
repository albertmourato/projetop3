package com.example.albert.projetop3;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.telephony.SmsManager.getDefault;
import static com.example.albert.projetop3.CardViewPessoaClickActivity.DELIVERED_BROADCAST;
import static com.example.albert.projetop3.CardViewPessoaClickActivity.SENT_BROADCAST;
import static com.example.albert.projetop3.CardViewPessoaClickActivity.mContext;
import static java.lang.Thread.sleep;

/**
 * Created by albert on 04/07/17.
 */

public class SendSMS {

    public SendSMS() {}


    public static void sendSms(Context context, Activity activity, String location) {
        boolean sendSMS = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, 1);
        sendSMS = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
        if (sendSMS) {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<Pessoa> safeContacts = getPessoas(mContext);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            if (safeContacts.size() > 0) {
                for (Pessoa p : safeContacts) {
                    Log.d("pessoa", p.getNome());
                    String currentDateandTime = sdf.format(new Date());

                    String message = "Estou correndo perigo!\nLocalização: " + location + "\nHora: " + currentDateandTime;

                    smsManager.sendTextMessage(p.getTelefone(), null, message, null, null);

                    Log.d("pessoa", "mandei para " + p.getNome());
                    Log.d("pessoa", message);

                }
            } else {
                Toast.makeText(context, "Nenhum contato selecionado para receber alerta!", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(context, "Conceda permissões em settings", Toast.LENGTH_SHORT).show();
        }
    }


    //Get only contacts whose notify boolean is true
    public static ArrayList<Pessoa> getPessoas(Context context) {
        ArrayList<Pessoa> a = new ArrayList<Pessoa>();
        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(mContext);
        Cursor c = dbHelper.getReadableDatabase().query(DatabaseOpenHelper.TABLE_NAME,
                DatabaseOpenHelper.columns, null, new String[]{}, null, null,
                null);
        c.moveToFirst();
        Log.d("qtdContacts", c.getCount() + "");
        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex(DatabaseOpenHelper.CONTACT_NAME));
            String phone = c.getString(c.getColumnIndex(DatabaseOpenHelper.CONTACT_NUMBER));
            String avisar = c.getString(c.getColumnIndex(DatabaseOpenHelper.CONTACT_ALERT));
            if (avisar.equals("true")) {
                Pessoa p = new Pessoa();
                p.setNome(name);
                p.setTelefone(phone);
                p.setAvisar((avisar.equals("true")) ? true : false);
                a.add(p);
            }
        }
        return a;
    }

}