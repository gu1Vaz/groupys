package com.example.group;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

public class Updater  {

    private static double version = 2.0;

    private RequestQueue Queue;
    public Conexao Conexao = com.example.group.Conexao.getInstance();
    private FragmentManager fragmentManager;


    public Updater(ActivityMain _activity, FragmentManager _fragmentManager){
        fragmentManager = _fragmentManager;
        Queue = Volley.newRequestQueue(_activity);
        Queue.add(update);
    }
    private void load(JSONArray data){
        try {
            if(  Double.parseDouble(data.getString(0)) != version){
                UpdateDialogFragment updateDialogFragment = new UpdateDialogFragment();
                updateDialogFragment.show(fragmentManager, "update_dialog");
            }
        }catch (Error | JSONException ignored){};
    }
    JsonArrayRequest update = new JsonArrayRequest(Request.Method.GET, Conexao.host+"version",null,
            this::load,
            error -> {}
    );
}