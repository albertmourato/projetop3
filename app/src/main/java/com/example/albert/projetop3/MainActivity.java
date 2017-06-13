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
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    static final int PEGAR_CONTATO_REQ = 1;
    static final String SENT_BROADCAST = "SMS_ENVIADO";
    static final String DELIVERED_BROADCAST = "SMS_ENTREGUE";
    static ArrayList<Pessoa> secureContacts;
    Button emergButton;
    Button arrivedButton;
    Button addContactsButton;
    ListView contacts;
    LocationManager mLocationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
        setContentView(R.layout.activity_main);
        init();
        addListeners();

    }

    public void init(){
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        contacts = (ListView) findViewById(R.id.list_view);
        secureContacts = new ArrayList<Pessoa>();
        emergButton = (Button) findViewById(R.id.emerg_button);
        arrivedButton = (Button) findViewById(R.id.arrivedButton);
        addContactsButton = (Button) findViewById(R.id.addContactsButton);

    }

    public void populateContactList(){

    }

    public void addListeners(){

        emergButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean sendSMS = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                if (sendSMS) {


                    String message = "Estou correndo perigo!" +"LOCALIZACAO";
                    registerReceiver(enviadoReceiver, new IntentFilter(SENT_BROADCAST));
                    registerReceiver(entregueReceiver, new IntentFilter(DELIVERED_BROADCAST));

                    PendingIntent piEnvio = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SENT_BROADCAST), 0);
                    PendingIntent piEntrega = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(DELIVERED_BROADCAST), 0);


                    SmsManager smsManager = SmsManager.getDefault();
                    if (secureContacts.size() > 0) {
                        for (Pessoa contact : secureContacts) {
                            System.out.println(contact.getTelefone());
                            smsManager.sendTextMessage(contact.getTelefone(),null, message, piEnvio, piEntrega);

                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Conceda permissões em settings", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });


        arrivedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cheguei bem
            }
        });

        addContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //adicionar contatos de segurança
                Intent i = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);//apenas contatos com telefone
                startActivityForResult(i, PEGAR_CONTATO_REQ);
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PEGAR_CONTATO_REQ) {
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();

                //pegar apenas o numero de telefone
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

                //fazendo query direto na thread principal...
                Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
                int column_number = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int column_name = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                if(cursor.moveToFirst()){
                    // pega o numero de telefone

                    String contactPhone = cursor.getString(column_number);
                    String contactName = cursor.getString(column_name);
                    Pessoa p = new Pessoa();
                    p.setTelefone(contactPhone);
                    p.setNome(contactName);
                    secureContacts.add(p);
                    PessoaAdapter pessoaAdapter = new PessoaAdapter(getApplicationContext(), secureContacts);
                    contacts.setAdapter(pessoaAdapter);
                }

            }
        }
    }

    BroadcastReceiver enviadoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(getBaseContext(), "SMS enviado", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(getBaseContext(), "Falha geral", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(getBaseContext(), "Sem serviço", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
                    break;
            }

            unregisterReceiver(this);
        }
    };

    BroadcastReceiver entregueReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(getBaseContext(), "SMS entregue", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(getBaseContext(), "SMS não foi entregue", Toast.LENGTH_SHORT).show();
                    break;
            }
            unregisterReceiver(this);
        }
    };


}
