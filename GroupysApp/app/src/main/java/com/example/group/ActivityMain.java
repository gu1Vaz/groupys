package com.example.group;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ViewFlipper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Objects;


public class ActivityMain extends AppCompatActivity  {

    private ViewFlipper viewFlipper;
    private GestureDetector gestureDetector;
    View selectedBtn;

    private HashMap<String, Class<? extends View_> > dicViews;
    private HashMap<String, View_> dicObjsViews;
    private Conexao Conexao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Main);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedBtn = (View) findViewById(R.id.btn_chats);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        viewFlipper.setDisplayedChild(2);

        scrollInitialBtns();

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        gestureDetector = new GestureDetector(this, customGestureDetector);

        setConexao();
        dicViews();

        ViewChats vChats = new ViewChats(this);
        dicObjsViews = new HashMap<>();
        dicObjsViews.put("chats",vChats);

    }
    public void switchTheme(View view) {
        int nightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        AppCompatDelegate.setDefaultNightMode(nightMode == Configuration.UI_MODE_NIGHT_YES
                ? AppCompatDelegate.MODE_NIGHT_NO
                : AppCompatDelegate.MODE_NIGHT_YES);
        recreate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    //listeners ui
    public  void  scrollInitialBtns(){
        HorizontalScrollView sw = (HorizontalScrollView) findViewById(R.id.scroll_btns);
        sw.post(() -> sw.smoothScrollTo(60, 0));
    }
    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // Swipe left (next)
            if (e1.getX() > e2.getX()) {
                viewFlipper.showNext();
                trocaView();
            }

            // Swipe right (previous)
            if (e1.getX() < e2.getX()) {
                viewFlipper.showPrevious();
                trocaView();
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d("eae", "eeee");
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
    public void dicViews(){
        dicViews = new HashMap<>();

        dicViews.put("chats",ViewChats.class);
        dicViews.put("match",ViewMatch.class);
        dicViews.put("groupys",ViewGroupys.class);
        dicViews.put("amigos",ViewAmigos.class);
        dicViews.put("ale",ViewAle.class);
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    public void trocaView(View v) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        String tagView = v.getResources().getResourceEntryName(v.getId()).substring(4);
        int idView = getResources().getIdentifier(tagView, "id", getPackageName());
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_in));
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(idView)));

        this.selectedBtn.setBackground(null);
        this.selectedBtn = v;
        this.selectedBtn.setBackground(getResources().getDrawable(R.drawable.borderbottom));

        if(!dicObjsViews.containsKey(tagView)){
            Class[] cArg = new Class[1];
            cArg[0] = Activity.class;
            dicObjsViews.put(tagView, Objects.requireNonNull(dicViews.get(tagView))
                    .getDeclaredConstructor(cArg)
                    .newInstance(this));
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    public void trocaView(){
        this.selectedBtn.setBackground(null);
        View v = viewFlipper.getCurrentView();

        String tagView = v.getResources().getResourceEntryName(v.getId());
        int idView = getResources().getIdentifier("btn_"+tagView, "id", getPackageName());

        v = findViewById(idView);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_in));

        this.selectedBtn = v;
        this.selectedBtn.setBackground(getResources().getDrawable(R.drawable.borderbottom));
        try {
            if(!dicObjsViews.containsKey(tagView)){
                Class[] cArg = new Class[1];
                cArg[0] = Activity.class;
                dicObjsViews.put(tagView, Objects.requireNonNull(dicViews.get(tagView))
                        .getDeclaredConstructor(cArg)
                        .newInstance(this));
            }}catch (Exception ignored){ }
    }
    //Btns Activitys

    public void openConfig(View v){
        Intent intent = new Intent(this, ActivityConfig.class);
        startActivity(intent);
    }
    public void openConta(View v){
        Intent intent = new Intent(this, ActivityConta.class);
        startActivity(intent);
    }

    //conexoes

    public void setConexao(){
        Conexao= com.example.group.Conexao.getInstance();
        Conexao.open();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Conexao != null) {
            Conexao.close();
        }
    }

}
