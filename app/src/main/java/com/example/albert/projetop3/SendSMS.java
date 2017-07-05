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


    public static void sendSms(Context context, String location) {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<Pessoa> contacts = getSelectedContacts(context);
        if (contacts.size() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            for (Pessoa contact : contacts) {
                String currentDateandTime = sdf.format(new Date());
                String message = "Estou correndo perigo!\nLocalizacao: " + location + "\nData: " + currentDateandTime;
                smsManager.sendTextMessage(contact.getTelefone(), null, message, null, null);
            }
        } else {
            Toast.makeText(context, "Nenhum contado habilitado para receber alerta!", Toast.LENGTH_SHORT).show();
        }
    }


    //Get only contacts whose notify boolean is true
    public static ArrayList<Pessoa> getSelectedContacts(Context context) {
        ArrayList<Pessoa> a = new ArrayList<Pessoa>();
        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(mContext);
        Cursor c = dbHelper.getReadableDatabase().query(DatabaseOpenHelper.TABLE_NAME,
                DatabaseOpenHelper.columns, null, new String[]{}, null, null,
                null);
        Log.d("qtdContacts", c.getCount() + "");
        if(c.moveToFirst()){
            do{
                String name = c.getString(c.getColumnIndex(DatabaseOpenHelper.CONTACT_NAME));
                String phone = c.getString(c.getColumnIndex(DatabaseOpenHelper.CONTACT_NUMBER));
                String avisar = c.getString(c.getColumnIndex(DatabaseOpenHelper.CONTACT_ALERT));
                if(avisar.equals("true")){
                    Pessoa p = new Pessoa();
                    p.setNome(name);
                    p.setTelefone(phone);
                    p.setAvisar((avisar.equals("true")) ? true : false);
                    a.add(p);
                }
            }while(c.moveToNext());
            c.close();
        }
        return a;
    }


}