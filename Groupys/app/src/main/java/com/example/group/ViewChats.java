package com.example.group;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.Ack;
import io.socket.emitter.Emitter;


public class ViewChats extends View_ {



    public Conexao Conexao = com.example.group.Conexao.getInstance();
    private final Convert Convert = new Convert();
    private final Toasty Toasty;
    private final RequestQueue Queue;

    private final String apiUrl = "http://192.168.0.117:3000/";
    private boolean _EditTextIgnore = false;

    Button btnCriarSala, btnReloadSala;

    public ViewChats(ActivityMain _activity) {
        super(_activity);
        Queue = Volley.newRequestQueue(activity);
        Toasty = new Toasty(activity);

        loadBtns();
        enableBtnsListeners();
        enableListernersDivNickname();

        Queue.add(listChats);
        setNickInServer();

        Conexao.socket.on("listData", onListChatReceived);
    }
    //Btns, EditText...
    private void loadBtns(){
        btnCriarSala = activity.findViewById(R.id.btnCriarSala);
        btnReloadSala = activity.findViewById(R.id.btnReload);
    }
    private void enableBtnsListeners(){
        btnCriarSala.setOnClickListener(this::createSala);
        btnReloadSala.setOnClickListener(this::reloadListChats);
    }
    public void  enableListernersDivNickname(){
        EditText divNick = activity.findViewById(R.id.editTextNickname);
        TextWatcher textWatcher = new TextWatcher() {
            boolean isTyping = false;
            private Timer timer = new Timer();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (_EditTextIgnore)
                    return;
                if(!isTyping) {
                    isTyping = true;
                }
                timer.cancel();
                timer = new Timer();
                long DELAY = 2000;
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                isTyping = false;
                                saveNick(divNick.getText().toString());
                                setNickInServer();
                            }
                        },
                        DELAY
                );
            }
        };

        divNick.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEND ||
                    actionId == EditorInfo.IME_ACTION_PREVIOUS){
                saveNick(divNick.getText().toString());
                setNickInServer();
            }
            return false;
        });

        divNick.addTextChangedListener(textWatcher);

        divNick.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                saveNick(divNick.getText().toString());
                setNickInServer();
                return true;
            }
            return false;
        });
    }

    //Chats
    public void createSala(View v){
        FragmentManager ft = ((FragmentActivity)activity).getSupportFragmentManager();
        ActivityCreateSala view = new ActivityCreateSala(activity);
        view.show(ft, "CreateSalaView");
    }

    public void loadListChats(JSONArray listChats){
        ListView listView = activity.findViewById(R.id.listChats);
        List<Chat> chatsList = Convert.toListChats(listChats);

        ListChatsAdapter adapter = new ListChatsAdapter(activity, R.layout.list_chats_layout, chatsList);
        listView.setAdapter(adapter);

        //open chat
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Chat chat =  chatsList.get(position);
            String nomeChat =chat.getNome();

            Conexao.socket.emit("join",nomeChat, (Ack) args -> {
                JSONArray response = (JSONArray) args[0];
                try {
                    if(response.get(0).toString().equals("error")){
                        Toasty.show(response.get(1).toString(),R.color.danger);
                    }
                    else{
                        Intent intent = new Intent(activity, ActivityChat.class);
                        intent.putExtra("nomeChat",nomeChat);
                        intent.putExtra("users",  response.get(1).toString());
                        activity.startActivity(intent);
                    }
                }catch (JSONException ignored){
                }
            });
        });
    }
    public void reloadListChats(View v){
        Queue.add(listChats);
        Toasty.show(activity.getString(R.string.toast_reload_success),R.color.success);
    }


    //Nick

    public String getMyNick() {
        SharedPreferences changesChat = activity.getSharedPreferences("changesChat", Context.MODE_PRIVATE);
        return changesChat.getString("nickInChat", null);
    }
    public void saveNick(String nick) {
        SharedPreferences changesChat = activity.getSharedPreferences("changesChat", Context.MODE_PRIVATE);
        changesChat.edit().putString("nickInChat",nick).apply();
    }
    public void setNickInUi(String nick){
        activity.runOnUiThread(() -> {
            _EditTextIgnore = true;
            EditText divNick = activity.findViewById(R.id.editTextNickname);
            divNick.setText(nick);
            _EditTextIgnore = false;
        });
    }
    public void setNickInServer() {
        View ProgressBar = activity.findViewById(R.id.loadingNick);
        activity.runOnUiThread(()-> ProgressBar.setVisibility(View.VISIBLE));
        Conexao.socket.emit("set_name",getMyNick(), (Ack) args -> {
            JSONArray response = (JSONArray) args[0];
            try {
                activity.runOnUiThread(()-> ProgressBar.setVisibility(View.GONE));
                if(response.get(0).toString().equals("success_guest")){
                    setNickInUi(response.get(1).toString());
                    saveNick(response.get(1).toString());
                }else if(response.get(0).equals("success")){
                    setNickInUi(response.get(1).toString());
                    Toasty.show(activity.getString(R.string.toast_nick_success),R.color.success);
                }else{
                    Toasty.show(response.get(1).toString(),R.color.danger);
                }
            } catch (JSONException e) {
                activity.runOnUiThread(()-> ProgressBar.setVisibility(View.GONE));
                Toasty.show(activity.getString(R.string.error_1),R.color.danger);
            }
        });
    }

    //Volley Requests
    JsonArrayRequest listChats = new JsonArrayRequest(Request.Method.GET, apiUrl+"getSalas",null,
            this::loadListChats,
            error -> { }
    );

    //Receivers
    private final Emitter.Listener onListChatReceived = args -> {
        JSONObject response = (JSONObject) args[0];
        activity.runOnUiThread(() -> {
            try {
                loadListChats(response.getJSONArray("rooms"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });
    };

}
