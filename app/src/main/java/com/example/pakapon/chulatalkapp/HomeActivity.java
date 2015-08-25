package com.example.pakapon.chulatalkapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class HomeActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private ArrayAdapter<String> contentArray;
    private ChatListAdapter contentArray2;
    private ArrayList<String> contactList;
    private ArrayList<JSONObject> chatList;
    private SharedPreferences userInfo;
    private String sessionid;
    private String username;
    public static int tabbedSelection;
    public  Thread getMessageThread;
    private Thread getContactThread;
    public static String seqno = "0";
    private HashMap<String,JSONObject> list;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setTitle("ChulaTalk");

        userInfo = getSharedPreferences("UserInfo",MODE_PRIVATE);
        sessionid = userInfo.getString("sessionid","");
        username = userInfo.getString("username","");

        contactList = new ArrayList<String>();
        chatList = new ArrayList<JSONObject>();
        list = new HashMap<String,JSONObject>();

        // Set up the action bar.
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.i("Position", String.valueOf(position));
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.

        tabbedSelection = tab.getPosition();

        if (tabbedSelection == 0)
        {
            initializeGetContactThread();
            getContactThread.start();
        }
        else if (tabbedSelection == 1)
        {
            initializeGetMessageThread();
            getMessageThread.start();
        }
        else if (tabbedSelection == 2){
        }

        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Log.i("Instantiate Fragment "," " + position);

            if (position == 0) {
                return PlaceholderFragment.newInstance(position);
            } else if (position == 1) {
                return ChatListFragment.newInstance(position);
            } else{
                return SettingFragment.newInstance(position);
            }
        }

        @Override
        public int getCount(){
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            Log.i ("Section Number",String.valueOf(sectionNumber));

            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);

            return rootView;
        }


    }

    private void getContactList() throws JSONException {
        HTTPHelper httpHelper = new HTTPHelper();

        HashMap<String,String> requestParams = new HashMap<>();

        requestParams.put("sessionid",sessionid);

        String jsonString = httpHelper.POST("https://mis.cp.eng.chula.ac.th/mobile/api/?q=getContact", requestParams);

        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject = new JSONObject(jsonString);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        if ((jsonObject.getString("type")).equals("contact")) {
            JSONArray array = jsonObject.getJSONArray("content");
            contactList.clear();
            for (int i = 0;i < array.length(); i++)
            {
                contactList.add(i, array.getString(i));
                Log.i("Contact List",contactList.get(i));
            }
            contentArray = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,contactList);
        }
        else{
            contactList.add(0,"Error");
        }
    }

    private void getChatList() throws JSONException{
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

        if ((jsonObject.getString("type")).equals("getMessage")) {
            JSONArray array = jsonObject.getJSONArray("content");
            JSONObject item;

            String tempSeqNo = seqno;

            for (int i = 0;i < array.length(); i++)
            {
                item = new JSONObject(array.getString(i));

                if (item.getString("from").equals(username))
                {
                    list.put(item.getString("to"),item);
                    tempSeqNo = item.getString("seqno");
                }
                else if (item.getString("to").equals(username))
                {
                    list.put(item.getString("from"),item);
                    tempSeqNo = item.getString("seqno");
                }
            }

            if (!tempSeqNo.equals(seqno)){
                seqno = tempSeqNo;

                chatList.clear();
                int i = 0;
                for (String key : list.keySet())
                {
                    chatList.add(i,list.get(key));
                    i++;
                }

                int count = 1;

                while (count != 0){
                    count = 0;
                    for (i = 0; i < chatList.size() - 1; i++)
                    {
                        if (Integer.parseInt((chatList.get(i).getString("seqno"))) < Integer.parseInt((chatList.get(i+1)).getString("seqno"))){
                            JSONObject temp = chatList.get(i);
                            chatList.set(i,chatList.get(i+1));
                            chatList.set(i+1,temp);
                            count++;
                        }
                    }
                }

                contentArray2 = new ChatListAdapter(this,chatList,username);
            }
        }
        else{
            chatList = null;
        }
    }

    public void addNewContactButtonClicked(View view){

        Intent intent = new Intent(this,SearchUserActivity.class);
        intent.putStringArrayListExtra("contactList",this.contactList);

        tabbedSelection = 2;

        startActivity(intent);
    }

    public void initializeGetMessageThread() {
        getMessageThread = new Thread() {
            public void run() {

                while (tabbedSelection == 1) {
                    try {
                        getChatList();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (chatList == null) {

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ListView listView = (ListView) findViewById(R.id.listView2);

                                if (listView != null) {
                                    listView.setAdapter(contentArray2);
                                }

                                Log.i("Content Changed", "changing");
                            }
                        });
                    }

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public void initializeGetContactThread(){
        getContactThread = new Thread() {
            @Override
            public void run() {
                try {
                    getContactList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (contactList.get(0).equals("Error")){

                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ListView listView = (ListView) findViewById(R.id.listView);
                            if (listView != null) {
                                listView.setAdapter(contentArray);
                            }
                        }
                    });
                }

            }
        };
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (mViewPager.getCurrentItem() == 0){

            tabbedSelection = 0;

            if (!getContactThread.isAlive()){
                Log.i("Restart Thread", "Restart Contact Thread");
                initializeGetContactThread();
                getContactThread.start();
            }
        }
        else if (mViewPager.getCurrentItem() == 1){

            tabbedSelection = 1;

            if (!getMessageThread.isAlive()){
                Log.i("Restart Thread", "Restart Message Thread");
                initializeGetMessageThread();
                getMessageThread.start();
            }
        }
    }
}
