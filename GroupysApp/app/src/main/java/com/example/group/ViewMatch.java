package com.example.group;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONArray;
import org.json.JSONException;

import io.socket.client.Ack;
import io.socket.emitter.Emitter;

public class ViewMatch extends View_ {

    public Conexao Conexao = com.example.group.Conexao.getInstance();

    TextView introMatch;

    TextView titleFindMatch;
    LottieAnimationView btnFindMatch;
    boolean eBtnMatch;

    Handler handlerFindMatch = new Handler();

    public ViewMatch(Activity _activity)  {
        super(_activity);

        introMatch = activity.findViewById(R.id.intro_match);

        btnFindMatch = activity.findViewById(R.id.btn_find_match);
        titleFindMatch = activity.findViewById(R.id.title_find_match);
        eBtnMatch = false;

        btnFindMatch();

        Conexao.socket.on("matchFound", onMatchFound);
    }
    //OnClick Buttons
    private void disable(boolean emit){
        introMatch.setText(R.string.intro_match);
        titleFindMatch.setVisibility(View.VISIBLE);
        btnFindMatch.pauseAnimation();
        btnFindMatch.setFrame(3);
        eBtnMatch = false;
        if(emit) Conexao.socket.emit("leave_match");
    }
    private void enable(){
        introMatch.setText(R.string.toast_find);
        titleFindMatch.setVisibility(View.INVISIBLE);
        btnFindMatch.playAnimation();
        eBtnMatch = true;
        Conexao.socket.emit("join_match");
        handlerFindMatch();
    }
    public void onClickBtnFindMatch(){
        if(eBtnMatch)disable(true);
        else enable();
    }
    //Buttons
    private void btnFindMatch(){
        btnFindMatch.loop(true);
        btnFindMatch.setFrame(3);
        btnFindMatch.setOnClickListener(v -> onClickBtnFindMatch());
    }
    //PvChat
    public void openChat(){
        activity.runOnUiThread(()->disable(false));
        Intent intent = new Intent(activity, ActivityChatPv.class);
        intent.putExtra("nomeChat","Match");
        activity.startActivity(intent);
    }
    //Handlers
    private void handlerFindMatch(){
        int delay = 3000;
        handlerFindMatch.postDelayed(new Runnable() {
            public void run() {
                if(!eBtnMatch) return;
                Conexao.socket.emit("find_match", (Ack) args -> {
                    JSONArray response = (JSONArray) args[0];
                    try {
                        if(response.get(0).toString().equals("success")) openChat();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
                handlerFindMatch.postDelayed(this, delay);
            }
        }, delay);
    }
    //Receivers
    private final Emitter.Listener onMatchFound = args -> openChat();
}
