package com.example.albert.projetop3;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by albert on 12/06/17.
 */

public class PessoaAdapter extends ArrayAdapter<Pessoa> {
    public PessoaAdapter(Context context, ArrayList<Pessoa> pessoas) {
        super(context, 0, pessoas);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Pessoa p = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.pessoa_adapter, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.pessoa_name);
        TextView phone = (TextView) convertView.findViewById(R.id.pessoa_phone);
        // Populate the data into the template view using the data object
        name.setText(p.getNome());
        phone.setText(p.getTelefone());
        return convertView;
    }

}