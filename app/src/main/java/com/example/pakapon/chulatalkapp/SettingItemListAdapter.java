package com.example.pakapon.chulatalkapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Pakapon on 5/21/2015 AD.
 */
public class SettingItemListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<SettingItem> settingItemList;

    public SettingItemListAdapter(Activity activity,ArrayList<SettingItem> settingItemList){
        this.activity = activity;
        this.settingItemList = settingItemList;
    }

    @Override
    public int getCount() {
        return settingItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return settingItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SettingItem item = settingItemList.get(position);

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.setting_item, null);

        TextView title = (TextView)convertView.findViewById(R.id.settingItemTitle);
        TextView subtitle = (TextView)convertView.findViewById(R.id.settingItemSubtitle);
        ImageView iconImage = (ImageView)convertView.findViewById(R.id.settingItemImage);

        title.setText(item.itemTitle);
        subtitle.setText(item.itemSubTitle);

        if (item.icon != -1) {
            iconImage.setImageResource(item.icon);
        }
        else {
            iconImage.setVisibility(View.GONE);
        }

        if (item.isHeader)
        {
            title.setTypeface(null, Typeface.BOLD);
        }

        if (item.itemTitle.equals("Log out"))
        {
            Log.i("Setting Item Title : ", item.itemTitle);
            title.setTextColor(Color.RED);
        }
        else
        {
            title.setTextColor(Color.BLACK);
        }

        return convertView;
    }
}
