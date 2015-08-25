package com.example.pakapon.chulatalkapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class MainActivity extends ActionBarActivity {

    private EditText usernameTextField;
    private EditText passwordTextField;
    private String sessionID = null;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameTextField = (EditText) findViewById(R.id.usernameTextField);
        passwordTextField = (EditText) findViewById(R.id.passwordTextField);

        SharedPreferences prefs = getSharedPreferences("UserInfo",MODE_PRIVATE);

        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Invalid StudentID/Password");
        alertDialog.setMessage("Please enter StudentID and Password again");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        if (!prefs.getString("sessionid","").isEmpty()){
            Intent intent = new Intent(this,HomeActivity.class);
            startActivity(intent);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void loginButtonClicked(View view){

        final String username = usernameTextField.getText().toString();
        final String password = passwordTextField.getText().toString();

        final Thread jsonThread = new Thread(){
            @Override
            public void run() {
                try {
                    getSessionIDJSON(username,password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread intentThread = new Thread(){
            @Override
            public void run() {

                try {
                    jsonThread.join();
                } catch (Exception e){
                    e.printStackTrace();
                }

                if (sessionID != null){
                    goToFriends(username);
                }
                else{
                    loginFail();
                }
            }
        };

        jsonThread.start();
        intentThread.start();

    }

    private void getSessionIDJSON(String username,String password) throws JSONException {

        HTTPHelper httpHelper = new HTTPHelper();

        HashMap<String,String> requestParams = new HashMap<>();

        requestParams.put("username",username);
        requestParams.put("password",password);

        String jsonString = httpHelper.POST("https://mis.cp.eng.chula.ac.th/mobile/api/?q=signIn", requestParams);

        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject = new JSONObject(jsonString);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try {
            Log.i("id", jsonObject.getString("content"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if ((jsonObject.getString("type")).equals("sessionid")) {
            this.sessionID = jsonObject.getString("content");
        }
        else{
            this.sessionID = null;
        }
    }

    private void goToFriends(String username){

        Intent intent = new Intent(this,HomeActivity.class);
        SharedPreferences prefs = getSharedPreferences("UserInfo",MODE_PRIVATE);
        Editor prefsEditor =  prefs.edit();

        prefsEditor.putString("username",username);
        prefsEditor.putString("sessionid",this.sessionID);
        prefsEditor.apply();

        startActivity(intent);

    }

    private void loginFail(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.show();
            }
        });
    }
}
