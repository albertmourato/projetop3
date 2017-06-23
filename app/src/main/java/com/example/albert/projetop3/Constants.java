package com.example.albert.projetop3;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static DatabaseOpenHelper dbHelper;
    public static ArrayList<Pessoa> pessoas;

    public static ArrayList<Pessoa> getPessoas(Context context){
        dbHelper = new DatabaseOpenHelper(context);
        pessoas = new ArrayList<Pessoa>();
        Cursor c = dbHelper.getReadableDatabase().query(DatabaseOpenHelper.TABLE_NAME,
                DatabaseOpenHelper.columns, null, new String[]{}, null, null,
                null);
        c.moveToFirst();
        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex(DatabaseOpenHelper.CONTACT_NAME));
            String phone = c.getString(c.getColumnIndex(DatabaseOpenHelper.CONTACT_NUMBER));
            Pessoa p = new Pessoa();
            p.setNome(name);
            p.setTelefone(phone);
            pessoas.add(p);
        }
        return pessoas;
    }

}
