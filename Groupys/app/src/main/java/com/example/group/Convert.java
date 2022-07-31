package com.example.group;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Convert {
    //listChats
    public List<Chat> toListChats(JSONArray obj){
        List<Chat> arr = new ArrayList<>();
        int tam = obj.length();
        try {
            for (int i = 0;i<tam;i++){
                JSONObject chat= (JSONObject) obj.get(i);
                Chat value = new Chat(
                        chat.get("nome").toString(),
                        Integer.parseInt(chat.get("count").toString()),
                        Integer.parseInt(chat.get("max").toString())
                );
                arr.add(value);
            }
        }catch (Exception e){

        }
        return arr;
    };

    //listUsers
    public List<User> toListUsers(JSONArray obj){
        List<User> arr = new ArrayList<>();
        int tam = obj.length();
        try {
            for (int i = 0;i<tam;i++){
                JSONObject user= (JSONObject) obj.get(i);
                User value = new User(
                        user.get("name").toString()
                );
                arr.add(value);
            }
        }catch (Exception e){

        }
        return arr;
    };

    //messages

    public Message toMessage(JSONObject obj){
      try {
          Message msg = new Message(obj.get("user").toString(),obj.get("text").toString());
          return msg;
      }catch (Exception e){

      }
      return new Message("admin","Ocorreu um erro ao carregar essa mensagem");
    };




}
