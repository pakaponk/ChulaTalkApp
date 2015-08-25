package com.example.pakapon.chulatalkapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Pakapon on 5/15/2015 AD.
 */
public class ChatMessageAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<JSONObject> chatList;
    private String username;

    public ChatMessageAdapter(Activity activity, ArrayList<JSONObject> chatList, String username){
        this.activity = activity;
        this.chatList = chatList;
        this.username = username;

        Log.i("Total Message", String.valueOf(chatList.size()));
    }

    @Override
    public int getCount() {
        return chatList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // getting movie data for the row
        Log.i("Data Position", String.valueOf(position));

        JSONObject m = (JSONObject)getItem(position);

        String from = "";
        String to = "";
        String message = "";
        TextView chatMessage = null;

        try {
            from = m.getString("from");
            to = m.getString("to");
            message = m.getString("message");
        } catch (JSONException e){
            e.printStackTrace();
        }

        Log.i("Message Chat",message);

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if (position == 0 || message.contains("added you."))
                {
                    convertView = inflater.inflate(R.layout.system_message, null);

                    chatMessage = (TextView) convertView.findViewById(R.id.systemChatMessage);
                }
                else {
                    if (from.equals(username)) {
                        convertView = inflater.inflate(R.layout.user_message, null);

                        chatMessage = (TextView) convertView.findViewById(R.id.userChatMessage);
                    } else if (to.equals(username)) {
                        convertView = inflater.inflate(R.layout.opponent_message, null);
                        chatMessage = (TextView) convertView.findViewById(R.id.opponentChatMessage);
                    }
                }


        // Chat Message

        if (chatMessage != null) {
            chatMessage.setText(message);
        }

        return convertView;

    }
}
