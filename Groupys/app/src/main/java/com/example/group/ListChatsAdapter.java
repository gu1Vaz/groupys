package com.example.group;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class Chat {
    private final String nome;
    private final int qtd;
    private final int max;



    public Chat(String nome, int qtd,int max) {
        this.nome = nome;
        this.qtd = qtd;
        this.max = max;
    }

    public String  getNome() {
        return nome;
    }
    public int  getMax() {
        return max;
    }
    public int getQtd() {
        return qtd;
    }
}
public class ListChatsAdapter extends ArrayAdapter<Chat> {

    private final int layoutResource;

    public ListChatsAdapter(Context context, int layoutResource, List<Chat> dadosChat){
        super(context, layoutResource, dadosChat);
        this.layoutResource = layoutResource;

    }
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(layoutResource, null);
        }

        Chat dados = getItem(position);

        if (dados != null) {
            TextView nomeChatTextView = (TextView) view.findViewById(R.id.nomeChat);
            TextView qtdChatTextView = (TextView) view.findViewById(R.id.qtdChat);

            if (nomeChatTextView != null) {
                nomeChatTextView.setText(dados.getNome());
            }
            if (qtdChatTextView != null) {
                qtdChatTextView.setText(dados.getQtd()+"/"+dados.getMax());
            }
        }
        return view;
    }
    
}

