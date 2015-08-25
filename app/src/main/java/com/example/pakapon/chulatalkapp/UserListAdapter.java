package com.example.pakapon.chulatalkapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Pakapon on 5/15/2015 AD.
 */
public class UserListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<String> userList;

    public UserListAdapter(Activity activity, ArrayList<String> userList){
        this.activity = activity;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
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
            convertView = inflater.inflate(R.layout.user_list_item, null);

        TextView usernameView = (TextView) convertView.findViewById(R.id.usernameView);

        String username = userList.get(position);

        // Chat Title
        usernameView.setText(username);

        // Recent Message

        return convertView;
    }
}
