package com.example.albert.projetop3;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.SyncStateContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CardViewPessoaClickActivity extends AppCompatActivity {
    private static DatabaseOpenHelper dbHelper;
    private ArrayList<Pessoa> arrayList;
    static final int PEGAR_CONTATO_REQ = 1;
    static final String SENT_BROADCAST = "SMS_ENVIADO";
    static final String DELIVERED_BROADCAST = "SMS_ENTREGUE";
    RecyclerView recyclerView;
    static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        dbHelper = new DatabaseOpenHelper(mContext);
        arrayList = getPessoas(mContext);
        //criando view
        recyclerView = new RecyclerView(this);

        //o tamanho do recycler view não é alterado pelo conteúdo (ocupa a tela inteira sempre)
        recyclerView.setHasFixedSize(true);

        //diferente de ListView e GridView, RecyclerView não sabe nada sobre como organizar elementos
        //esta tarefa eh delegada para o LayoutManager, possibilitando diferentes abordagens
        //neste caso, temos um layout linear para estruturar elementos verticalmente...
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //outras opcoes incluem GridLayoutManager, por ex.
        //tambem pode ser implementado um LayoutManager customizado

        //definindo o adapter (semelhante a listadapter...)
        recyclerView.setAdapter(new PessoaAdapter(arrayList));

        //definindo layout da activity - sem usar XML (nao tem um ListActivity que possamos estender)
        setContentView(recyclerView);
        registerForContextMenu(recyclerView);

    }

    public void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(deleteContact);
    }

    protected void onResume() {
        super.onResume();
        arrayList = getPessoas(mContext);
        recyclerView.setAdapter(new PessoaAdapter(arrayList));
        IntentFilter intent = new IntentFilter("DELETE CONTATO");
        LocalBroadcastManager.getInstance(this).registerReceiver(deleteContact, intent);
    }

    private BroadcastReceiver deleteContact = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            dbHelper.getWritableDatabase().delete(DatabaseOpenHelper.TABLE_NAME, "number=?", new String[]{b.getString("phone", "-1")});
            Toast.makeText(getApplicationContext(), "Contato deletado!", Toast.LENGTH_SHORT).show();
            arrayList = getPessoas(mContext);
            recyclerView.setAdapter(new PessoaAdapter(arrayList));
        }
    };

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

                if (cursor.moveToFirst()) {
                    // pega o numero de telefone e nome
                    String contactPhone = cursor.getString(column_number);
                    String contactName = cursor.getString(column_name);
                    saveContact(contactName, contactPhone);
                    //readSafeContacts();
                }
            }
        }
    }

    public void saveContact(String contactName, String contactPhone) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseOpenHelper.CONTACT_NAME, contactName);
        contentValues.put(DatabaseOpenHelper.CONTACT_NUMBER, contactPhone);
        contentValues.put(DatabaseOpenHelper.CONTACT_ALERT, "true");
        //salvar no DB
        Cursor c = dbHelper.getReadableDatabase().query(DatabaseOpenHelper.TABLE_NAME,
                DatabaseOpenHelper.columns, "number=?", new String[]{contactPhone}, null, null,
                null);
        c.moveToFirst();

        if (c.getCount() > 0) {
            System.out.println(c.getCount() + " CONTATOS COM ESSE NUMERO");
            Toast.makeText(getApplicationContext(), "Algum contato já possui este número", Toast.LENGTH_SHORT).show();
        } else {
            //adiciona o contato
            dbHelper.getWritableDatabase().insert(DatabaseOpenHelper.TABLE_NAME, null, contentValues);
        }

    }

    public ArrayList<Pessoa> getPessoas(Context context) {
        ArrayList<Pessoa> a = new ArrayList<Pessoa>();
        Cursor c = dbHelper.getReadableDatabase().query(DatabaseOpenHelper.TABLE_NAME,
                DatabaseOpenHelper.columns, null, new String[]{}, null, null,
                null);
        c.moveToFirst();
        System.out.println(c.getCount());
        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex(DatabaseOpenHelper.CONTACT_NAME));
            String phone = c.getString(c.getColumnIndex(DatabaseOpenHelper.CONTACT_NUMBER));
            String avisar = c.getString(c.getColumnIndex(DatabaseOpenHelper.CONTACT_ALERT));
            Pessoa p = new Pessoa();
            p.setNome(name);
            p.setTelefone(phone);
            p.setAvisar((avisar.equals("true"))? true:false);
            a.add(p);
        }
        return a;
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_button, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (true) {
            // do something here
            //adicionar contatos de segurança
            Intent i = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
            i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);//apenas contatos com telefone
            startActivityForResult(i, PEGAR_CONTATO_REQ);
        }
        return super.onOptionsItemSelected(item);
    }

    private class PessoaAdapter extends RecyclerView.Adapter<CardClickHolder> {
        //fonte de dados
//       // Pessoa[] pessoas;
        ArrayList<Pessoa> pessoas;

        //instanciando fonte de dados
        PessoaAdapter(ArrayList<Pessoa> pessoas) {
            this.pessoas = pessoas;
        }

        @Override
        public CardClickHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //cria, configura e retorna um ViewHolder para uma linha da lista
            //parent é o ViewGroup que contem as views, usado pelo layout inflater
            //viewType é para o caso de ter múltiplos tipos de Views, em um RecyclerView
            //View v = getLayoutInflater().inflate(R.layout.itemlistacardview,parent,false);
            //View v = getLayoutInflater().inflate(R.layout.itemlistacardviewclick,parent,false);
            View v = getLayoutInflater().inflate(R.layout.itemlistacardviewclick, parent, false);
            return new CardClickHolder(v);
        }

        @Override
        public void onBindViewHolder(CardClickHolder holder, int position) {
            //responsavel por atualizar ViewHolder com dados de um elemento na posição 'position'
            Log.d("infoPessoa", pessoas.get(position).getAvisar()+"");
            holder.bindModel(pessoas.get(position));
        }

        @Override
        public int getItemCount() {
            //total de elementos
            Log.d("item", pessoas.size() + "");
            return pessoas.size();
        }
    }

    //responsavel por fazer o binding dos dados com widgets para cada linha da lista
    static class CardClickHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView nome = null;
        TextView telefone = null;
        ImageView icone = null;
        Switch aSwitch = null;
        Uri site = null;

        //poderia tambem passar algum objeto aqui construido no adapter, para nao adicionar atributos
        CardClickHolder(View row) {
            super(row);
            icone = (ImageView) row.findViewById(R.id.icone);
            aSwitch = (Switch) row.findViewById(R.id.switch1);
            nome = (TextView) row.findViewById(R.id.nome);
            telefone = (TextView) row.findViewById(R.id.telefone);
            icone = (ImageView) row.findViewById(R.id.icone);

            //definindo listener para linha/card inteiro
            //poderia definir click listener para cada view (nome, login...)
            row.setOnClickListener(this);
            row.setOnLongClickListener(this);

            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //Toast.makeText(buttonView.getContext(), isChecked+"", Toast.LENGTH_SHORT).show();
                    DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(mContext);
                    ContentValues values = new ContentValues();
                    values.put(DatabaseOpenHelper.CONTACT_ALERT, isChecked? "true": "false");
                    dbHelper.getWritableDatabase().update(DatabaseOpenHelper.TABLE_NAME, values,
                            DatabaseOpenHelper.CONTACT_NUMBER + "=?",
                            new String[]{telefone.getText().toString()});
                    if(aSwitch.isChecked())icone.setImageResource(R.drawable.ok);
                    else icone.setImageResource(R.drawable.delete);

                }
            });

        }

        void bindModel(Pessoa p) {
            nome.setText(p.getNome());
            telefone.setText(p.getTelefone());
            aSwitch.setChecked(p.getAvisar());
            if(aSwitch.isChecked())icone.setImageResource(R.drawable.ok);
            else icone.setImageResource(R.drawable.delete);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Toast.makeText(v.getContext(), "Clique e segure para excluir um contato",Toast.LENGTH_SHORT).show();
            //TextView phone = (TextView) v.findViewById(R.id.telefone);
            //Toast.makeText(v.getContext(), phone.getText().toString(), Toast.LENGTH_SHORT).show();

            //Intent i = new Intent(Intent.ACTION_VIEW,site);
            //v.getContext().startActivity(i);
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            //TODO delete
            TextView phone = (TextView)v.findViewById(R.id.telefone);
            Intent delContact = new Intent("DELETE CONTATO");
            delContact.putExtra("phone", phone.getText().toString());
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(delContact);
            return true;
        }

    }
}

