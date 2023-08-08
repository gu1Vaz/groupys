package com.example.group;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.socket.client.Ack;
import io.socket.emitter.Emitter;


public class ActivityChatPv extends AppCompatActivity {

    String nome;
    List<Message> messagesList = new ArrayList<>();

    ListMessagesAdapter adapterMessages;

    Conexao Conexao = com.example.group.Conexao.getInstance();
    Convert Convert  = new Convert();
    Toasty Toasty = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Main);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_pv);

        Toasty = new Toasty(this);

        Bundle props = getIntent().getExtras();
        setAllProps(props);

        enableListernersDivMessage();

        setAdapters();

        Conexao.socket.on("message", onMessageReceived);


    }
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Sair de "+nome+" ?")
                .setCancelable(true)
                .setPositiveButton("Sim", (dialog1, id) -> leave())
                .setNegativeButton("Não", null)
                .create();
        dialog.setOnShowListener(arg0 -> {
            Button btnNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            btnNegative.setBackgroundResource(android.R.color.transparent);
            btnPositive.setBackgroundResource(android.R.color.transparent);

            btnNegative.setTextColor(Color.parseColor("#737373"));
            btnPositive.setTextColor(Color.parseColor("#737373"));
        });
        dialog.show();
    }
    //listeners
    public void  enableListernersDivMessage(){
        EditText divMessage = findViewById(R.id.editTextMessage);

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
        TextView textViewNomeChat = findViewById(R.id.nomeChat);
        nome = props.getString("nomeChat");
        textViewNomeChat.setText(nome);
    }
    public void leave(View v){
        Conexao.socket.emit("leave_match","", (Ack) args -> { });
        finish();
    }
    public void leave(){
        Conexao.socket.emit("leave_match","", (Ack) args -> { });
        finish();
    }
    //adapters
    private void setAdapters(){
        ListView listMessages = findViewById(R.id.listMessages);
        adapterMessages = new ListMessagesAdapter(this,R.layout.list_messages_layout,messagesList);
        listMessages.setAdapter(adapterMessages);
    }
    public void updateAdapters(){
        runOnUiThread(() -> {
            ListView listMessages = findViewById(R.id.listMessages);
            adapterMessages.notifyDataSetChanged();
            int end = listMessages.getCount() -1;
            int current = listMessages.getLastVisiblePosition();
            if(end - current <= 5){
                listMessages.smoothScrollByOffset(listMessages.getCount() -1);
            }
        });

    }
    //message
    private final Emitter.Listener onMessageReceived = args -> {
        Message msg = Convert.toMessage((JSONObject) args[0]);
        messagesList.add(msg);
        updateAdapters();
    };
    public void sendMessage(View v){
        EditText input = findViewById(R.id.editTextMessage);
        String message = input.getText().toString();
        if (!message.equals("")) {
            Message msg = new Message("Você", message);
            messagesList.add(msg);
            updateAdapters();
            Conexao.socket.emit("sendMessageMatch",message);
            input.setText("");
        }
    }
    public void sendMessage(String message){
        EditText input = findViewById(R.id.editTextMessage);
        if (!message.equals("")) {
            Message msg = new Message("Vocẽ", message);
            messagesList.add(msg);
            updateAdapters();
            Conexao.socket.emit("sendMessageMatch",message);
            input.setText("");
        }
    }


}