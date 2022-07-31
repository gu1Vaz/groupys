package com.example.group;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;

import io.socket.client.Ack;


public class ActivityCreateSala extends BottomSheetDialogFragment {

    Activity activity;

    EditText nomeView;
    Spinner maxView;

    Conexao Conexao = null;
    Toasty Toasty = null;
    Button btnCriar;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.sub_activity_create_sala, container, false);

        nomeView =(EditText)  v.findViewById(R.id.editTextNameSala);
        maxView= (Spinner) v.findViewById(R.id.spinnerMaxSala);

        btnCriar = v.findViewById(R.id.btnCriarSala2);
        btnCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCriar();
            }
        });
        return v;
    }
    public ActivityCreateSala(Activity _activity){
        this.activity = _activity;
        Toasty = new Toasty(activity);
        Conexao= com.example.group.Conexao.getInstance();
    }
    public void onCriar(){
        String nomeChat =  nomeView.getText().toString();
        int maxChat = Integer.parseInt(maxView.getSelectedItem().toString());
        Conexao.socket.emit("create",nomeChat,maxChat, (Ack) args -> {
            JSONArray response = (JSONArray) args[0];
            try {
                if(response.get(0).toString().equals("error")){
                    Toasty.show("Algo de errado nao esta correto",R.color.danger);
                }
                else{
                    this.dismiss();
                    Intent intent = new Intent(activity, ActivityChat.class);
                    intent.putExtra("nomeChat",nomeChat);
                    intent.putExtra("users",  response.get(1).toString());
                    startActivity(intent);
                }
            }catch (JSONException e){
                Toasty.show("Algo de correto esta errado",R.color.danger);
            }
        });
    }


}
