package com.example.pakapon.chulatalkapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class SearchUserActivity extends ActionBarActivity {

    private SharedPreferences userInfo;
    private String sessionid;
    private String username;
    private String opponentUsername;
    private ArrayList<String> userList;
    private ArrayList<String> contactList;
    private AlertDialog noUserDialog;
    private AlertDialog alreadyFriendDialog;
    private AlertDialog addContactCompleteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        setTitle("Search User");

        Intent intent = getIntent();
        contactList = intent.getStringArrayListExtra("contactList");

        userInfo = getSharedPreferences("UserInfo",MODE_PRIVATE);
        sessionid = userInfo.getString("sessionid","");
        username = userInfo.getString("username","");

        userList = new ArrayList<String>();

        noUserDialog = new AlertDialog.Builder(this).create();
        noUserDialog.setTitle("StudentID Not Found!");
        noUserDialog.setMessage("Please enter new StudentID");
        noUserDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alreadyFriendDialog = new AlertDialog.Builder(this).create();
        alreadyFriendDialog.setTitle("Already be friends with the given ID");
        alreadyFriendDialog.setMessage("Please enter new StudentID");
        alreadyFriendDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        SearchView searchView = (SearchView) findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(final String s) {

                Thread getUserThread = new Thread(){
                    @Override
                    public void run() {
                        try{
                            requestUserList(s);
                        } catch (JSONException e){

                        }
                    }
                };

                getUserThread.start();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void requestUserList(String keyword) throws JSONException {

        HTTPHelper httpHelper = new HTTPHelper();

        HashMap<String,String> requestParams = new HashMap<>();

        requestParams.put("sessionid",sessionid);
        requestParams.put("keyword",keyword);

        String jsonString = httpHelper.POST("https://mis.cp.eng.chula.ac.th/mobile/api/?q=searchUser", requestParams);

        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject = new JSONObject(jsonString);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        String type = jsonObject.getString("type");

        if (type.equals("userList")){
            JSONArray array = jsonObject.getJSONArray("content");
            userList.clear();
            Log.i("Number of User", String.valueOf(array.length()));
            for (int i = 0;i < array.length();i++)
            {
                userList.add(i, (String) array.get(i));
            }

            if (userList.size() == 0)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        noUserDialog.show();
                    }
                });

            }
            else
            {
                userList.removeAll(contactList);

                if (userList.size() == 0)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alreadyFriendDialog.show();
                        }
                    });
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadUserListToListView();
                        }
                    });
                }
            }
        }
    }

    public void addContactButtonClicked(View view){

        opponentUsername = ((TextView)((View)view.getParent()).findViewById(R.id.usernameView)).getText().toString();

        Thread addContactThread = new Thread(){
            @Override
            public void run() {
                try {
                    requestAddContact();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        addContactThread.start();

    }

    public void loadUserListToListView(){
        ListView listView = (ListView) findViewById(R.id.listView3);

        if (listView != null) {
            listView.setAdapter(new UserListAdapter(this, userList));
        }
    }

    public void requestAddContact() throws JSONException {
        HTTPHelper httpHelper = new HTTPHelper();

        HashMap<String,String> requestParams = new HashMap<>();

        requestParams.put("sessionid",sessionid);
        requestParams.put("username",opponentUsername);

        String jsonString = httpHelper.POST("https://mis.cp.eng.chula.ac.th/mobile/api/?q=addContact", requestParams);

        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject = new JSONObject(jsonString);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        String type = jsonObject.getString("type");

        if (type.equals("addContact")){

            this.contactList.add(opponentUsername);
            this.userList.remove(opponentUsername);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showAddContactCompleteDialog(opponentUsername);
                    loadUserListToListView();
                }
            });
        }
    }

    public void showAddContactCompleteDialog(String username){
        addContactCompleteDialog = new AlertDialog.Builder(this).create();
        addContactCompleteDialog.setTitle("Add Contact Complete");
        addContactCompleteDialog.setMessage("You are now friend with " + username);
        addContactCompleteDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        addContactCompleteDialog.show();
    }
}
