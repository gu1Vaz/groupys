package com.example.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class User {
    private final String nome;

    public User(String nome) {
        this.nome = nome;

    }

    public String  getNome() {
        return nome;
    }
}

public class ListUsersAdapter extends ArrayAdapter<User> {
    private final int layoutResource;

    public ListUsersAdapter(Context context, int layoutResource, List<User> dadosUser){
        super(context, layoutResource, dadosUser);
        this.layoutResource = layoutResource;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(layoutResource, null);
        }

        User dados = getItem(position);

        if (dados != null) {
            TextView nomeUserTextView = (TextView) view.findViewById(R.id.nomeUser);

            if (nomeUserTextView != null) {
                nomeUserTextView.setText(dados.getNome());
            }
        }
        return view;
    }
}
