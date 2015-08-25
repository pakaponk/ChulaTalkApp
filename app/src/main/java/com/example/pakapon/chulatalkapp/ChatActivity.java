package com.example.pakapon.chulatalkapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class ChatActivity extends ActionBarActivity {

    private String opponentUsername;
    private SharedPreferences userInfo;
    private String sessionid;
    private String username;
    private String seqno = "0";
    private EditText editText;

    private Boolean threadIsRunning = true;

    private ArrayList<JSONObject> chatMessageList;

    public Thread getChatMessageThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        HomeActivity.tabbedSelection = 2;

        threadIsRunning = true;

        Intent intent = getIntent();
        opponentUsername = intent.getStringExtra("opponentUsername");

        Log.i("OpponentID",opponentUsername);

        chatMessageList = new ArrayList<JSONObject>();

        userInfo = getSharedPreferences("UserInfo",MODE_PRIVATE);
        sessionid = userInfo.getString("sessionid","");
        username = userInfo.getString("username","");

        setTitle("Chat With " + opponentUsername);

        editText = (EditText)findViewById(R.id.editText);

        getChatMessageThread = new Thread(){
            @Override
            public void run() {
                while(threadIsRunning) {
                    try {
                        requestChatMessage();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

        };

        getChatMessageThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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

    public void requestChatMessage() throws JSONException {
        HTTPHelper httpHelper = new HTTPHelper();

        HashMap<String,String> requestParams = new HashMap<>();

        requestParams.put("sessionid",sessionid);
        requestParams.put("seqno",seqno);
        requestParams.put("limit","100");

        String jsonString = httpHelper.POST("https://mis.cp.eng.chula.ac.th/mobile/api/?q=getMessage", requestParams);

        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject = new JSONObject(jsonString);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        Log.i("JSONString", jsonObject.getString("content"));

        if ((jsonObject.getString("type")).equals("getMessage")) {
            JSONArray array = jsonObject.getJSONArray("content");
            JSONObject item;

            String tempSeqNo = seqno;

            for (int i = 0;i < array.length(); i++)
            {
                item = new JSONObject(array.getString(i));

                if (item.getString("from").equals(opponentUsername))
                {
                    chatMessageList.add(chatMessageList.size(),item);
                    tempSeqNo = item.getString("seqno");
                    Log.i("Message From", item.toString());
                }
                else if (item.getString("to").equals(opponentUsername))
                {
                    chatMessageList.add(chatMessageList.size(),item);
                    tempSeqNo = item.getString("seqno");
                    Log.i("Message To", item.toString());
                }
            }

            Log.i("Total Chat Messages", String.valueOf(chatMessageList.size()));

            if (!tempSeqNo.equals(seqno)) {

                seqno = tempSeqNo;

                synchronized (chatMessageList) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadChatMessagesToListView();
                        }
                    });
                }
            }

        }
        else{
            chatMessageList = null;
        }
    }

    public void loadChatMessagesToListView(){
        ListView listView = (ListView)findViewById(R.id.listView4);

        Log.i("Total Message Again", String.valueOf(chatMessageList.size()));

        listView.setAdapter(new ChatMessageAdapter(this,chatMessageList,username));

        listView.setSelection(chatMessageList.size() - 1);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        threadIsRunning = false;
    }

    public void sendButtonClicked(View view){
        Thread sendMessageThread = new Thread(){
            @Override
            public void run() {
                requestPostMessage();
            }
        };

        sendMessageThread.start();
    }

    public void requestPostMessage(){
        HTTPHelper httpHelper = new HTTPHelper();

        HashMap<String,String> requestParams = new HashMap<>();

        requestParams.put("sessionid",sessionid);
        requestParams.put("targetname",opponentUsername);
        requestParams.put("message", editText.getText().toString());

        String jsonString = httpHelper.POST("https://mis.cp.eng.chula.ac.th/mobile/api/?q=postMessage", requestParams);

        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject = new JSONObject(jsonString);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                editText.setText("");
            }
        });
    }
}
