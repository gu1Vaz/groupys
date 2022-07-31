package com.example.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class Message {
    private final String nick;
    private final String msg;

    public Message(String nick,String msg) {
        this.msg = msg;
        this.nick = nick;
    }

    public String  getMsg() {
        return msg;
    }
    public String  getNick() {
        return nick;
    }
}

public class ListMessagesAdapter extends ArrayAdapter<Message> {
    private final int layoutResource;

    public ListMessagesAdapter(Context context, int layoutResource, List<Message> dadosMessage){
        super(context, layoutResource, dadosMessage);
        this.layoutResource = layoutResource;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(layoutResource, null);
        }

        Message dados = getItem(position);

        if (dados != null) {
            TextView messageTextView = (TextView) view.findViewById(R.id.messageUser);
            TextView nickTextView = (TextView) view.findViewById(R.id.nickUser);

            if (messageTextView != null) {
                messageTextView.setText(dados.getMsg());
            }
            if (nickTextView != null) {
                nickTextView.setText(dados.getNick());
            }
        }
        return view;
    }
}
