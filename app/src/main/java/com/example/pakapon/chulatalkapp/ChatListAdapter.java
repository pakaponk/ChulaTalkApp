package com.example.pakapon.chulatalkapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Pakapon on 5/14/2015 AD.
 */
public class ChatListAdapter extends BaseAdapter{

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<JSONObject> chatList;
    private String username;

    public ChatListAdapter(Activity activity,ArrayList<JSONObject> chatList,String username){
        this.activity = activity;
        this.chatList = chatList;
        this.username = username;
    }

    @Override
    public int getCount() {
        return this.chatList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.chatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.chat_list_item, null);

        TextView chatTitle = (TextView) convertView.findViewById(R.id.chattedUsername);
        TextView recentMessage = (TextView) convertView.findViewById(R.id.lastChatMessage);

        // getting movie data for the row
        JSONObject m = (JSONObject)getItem(position);

        // Chat Title

        try {
            if (m.getString("from").equals(username))
                chatTitle.setText(m.getString("to"));
            else
                chatTitle.setText(m.getString("from"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Recent Message
        try {
            recentMessage.setText(m.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;

    }
}
