package com.example.pakapon.chulatalkapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<SettingItem> settingItemList;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(int position) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
        initializeSettingItemList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        ListView listView = (ListView)rootView.findViewById(R.id.settingListView);

        Log.i("Setting View Selected"," " + settingItemList.size());

        if (listView!=null){
            listView.setAdapter(new SettingItemListAdapter(getActivity(),settingItemList));
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 4)
                {
                    Log.i("Log out","User log out");
                    logout();
                }

            }
        });

        return rootView;
    }

    public void initializeSettingItemList(){
        settingItemList = new ArrayList<SettingItem>();
        settingItemList.add(new SettingItem("ChulaTalk",null,true,-1));
        settingItemList.add(new SettingItem("Version","1.0",false,R.drawable.info_25));
        settingItemList.add(new SettingItem("Contact Us","http://chulatalk.com",false,R.drawable.message_25));
        settingItemList.add(new SettingItem("User",null,true,-1));
        settingItemList.add(new SettingItem("Log out",null,false,R.drawable.exit_25_red));
    }

    public void logout(){
        SharedPreferences prefs = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.clear();
        prefsEditor.apply();

        Intent intent = new Intent(this.getActivity(),MainActivity.class);

        startActivity(intent);


    }

}
