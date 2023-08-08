package com.example.group;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import io.socket.client.Ack;
import io.socket.emitter.Emitter;


public class ActivityChat extends AppCompatActivity {

    String nome;
    List<User> usersList;
    List<Message> messagesList = new ArrayList<>();


    ListUsersAdapter adapterUsers;
    ListMessagesAdapter adapterMessages;

    Conexao Conexao = com.example.group.Conexao.getInstance();
    Convert Convert  = new Convert();
    Toasty Toasty = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Main);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toasty = new Toasty(this);

        Bundle props = getIntent().getExtras();
        setAllProps(props);

        enableListernersDivMessage();

        setAdapters();

        Conexao.socket.on("message", onMessageReceived);
        Conexao.socket.on("roomData", onRoomDataReceived);

    }
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Sair de "+nome+" ?")
                .setCancelable(true)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        leave();
                    }
                })
                .setNegativeButton("Não", null)
                .create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                Button btnNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

                btnNegative.setBackgroundResource(android.R.color.transparent);
                btnPositive.setBackgroundResource(android.R.color.transparent);

                btnNegative.setTextColor(Color.parseColor("#737373"));
                btnPositive.setTextColor(Color.parseColor("#737373"));
            }
        });
        dialog.show();
    }
    //listeners
    public void  enableListernersDivMessage(){
        EditText divMessage = (EditText) findViewById(R.id.editTextMessage);

        divMessage.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEND ){
                sendMessage(divMessage.getText().toString());
            }
            return false;
        });
        divMessage.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                sendMessage(divMessage.getText().toString());
                return true;
            }
            return false;
        });
    }

    //chat
    private void setAllProps(Bundle props){
        TextView textViewNomeChat = (TextView) findViewById(R.id.nomeChat);
        try {
            JSONArray arr = new JSONArray(props.getString("users"));
            Message boasVindas = new Message("admin","Bem Vindo(a)!");

            nome = props.getString("nomeChat");
            usersList = Convert.toListUsers(arr);
            messagesList.add(boasVindas);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        textViewNomeChat.setText(nome);

    }
    public void leave(View v){
        Conexao.socket.emit("leave","", (Ack) args -> {

        });
        finish();
    }
    public void openConfig(View v){
        Intent intent = new Intent(this, ActivityConfig.class);
        startActivity(intent);
    }
    public void leave(){
        Conexao.socket.emit("leave","", (Ack) args -> {

        });
        finish();
    }
    //adapters
    private void setAdapters(){
        ListView listUsers = (ListView) findViewById(R.id.listUsers);
        ListView listMessages = (ListView) findViewById(R.id.listMessages);

        adapterUsers = new ListUsersAdapter(this, R.layout.list_users_layout, usersList);
        adapterMessages = new ListMessagesAdapter(this,R.layout.list_messages_layout,messagesList);

        listUsers.setAdapter(adapterUsers);
        listMessages.setAdapter(adapterMessages);

    }
    public void updateAdapters(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listMessages = (ListView) findViewById(R.id.listMessages);
                adapterMessages.notifyDataSetChanged();
                adapterUsers.notifyDataSetChanged();
                int end = listMessages.getCount() -1;
                int current = listMessages.getLastVisiblePosition();
                if(end - current <= 5){
                    listMessages.smoothScrollByOffset(listMessages.getCount() -1);
                }
            }
        });

    }
    //message
    private final Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
           Message msg = Convert.toMessage((JSONObject) args[0]);
           messagesList.add(msg);
           updateAdapters();
        }
    };
    public void sendMessage(View v){
        EditText input = (EditText) findViewById(R.id.editTextMessage);
        String message = input.getText().toString();
        if (!message.equals("")) {
            Conexao.socket.emit("sendMessage",message);
            input.setText("");
        }
    }
    public void sendMessage(String message){
        EditText input = (EditText) findViewById(R.id.editTextMessage);
        if (!message.equals("")) {
            Conexao.socket.emit("sendMessage",message);
            input.setText("");
        }
    }
    //users
    private final Emitter.Listener onRoomDataReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject obj = (JSONObject) args[0];
            try{
                JSONArray arr = obj.getJSONArray("users");
                usersList.removeAll(usersList);
                usersList.addAll(Convert.toListUsers(arr));
            }catch (Exception ignored){

            }
            updateAdapters();
        }
    };


}