package com.example.group;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class Toasty {
    public Activity activity;

    View layout;
    Toast toast;
    View toastBox;
    TextView toastText;

    public Toasty(Activity _activity)  {
        this.activity = _activity;

        LayoutInflater inflater = activity.getLayoutInflater();

        layout = inflater.inflate(R.layout.toast_layout,(ViewGroup)activity.findViewById(R.id.toast_root));
        toast = new Toast(activity.getApplicationContext());
    }

    public void show(String text,int color){

        toastText = layout.findViewById(R.id.toast_text);
        toastBox=  layout.findViewById(R.id.toast_root);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toastBox.setBackgroundResource(color);
                toastText.setText(text);
            }
        });

        //toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        toast.show();
    };
}
