package com.example.group;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import java.io.Serializable;
import java.net.URISyntaxException;
import io.socket.client.IO;
import io.socket.client.Socket;


public class Conexao  {
    private static Conexao instance;
    //http://192.168.0.117:3000/
    //https://groupys.nonegui.cloud/
    public  String host = "https://groupys.nonegui.cloud/";
    Socket socket;

    public synchronized static Conexao getInstance() {
        if (instance == null) {
            try {
                instance = new Conexao();
            }catch (Exception e){

            }
        }
        return instance;
    }

    public Conexao() throws URISyntaxException {
        socket = IO.socket(host);
    }
    public void open(){
        socket.connect();
    }
    public void close(){
        socket.disconnect();
    }

}
