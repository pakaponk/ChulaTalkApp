package com.example.pakapon.chulatalkapp;

/**
 * Created by Pakapon on 5/21/2015 AD.
 */
public class SettingItem {
    public String itemTitle;
    public String itemSubTitle;
    public Boolean isHeader;
    public int icon;

    public SettingItem(String itemTitle,String itemSubTitle,Boolean isHeader,int icon){
        this.itemTitle = itemTitle;
        this.itemSubTitle = itemSubTitle;
        this.isHeader = isHeader;
        this.icon = icon;
    }
}
