package com.example.group;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Objects;

public class ActivityConta extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Main);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta);
    }
    public void onBackPressed(){
        finish();
    }
}