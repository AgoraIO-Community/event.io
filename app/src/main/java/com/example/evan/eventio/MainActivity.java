package com.example.evan.eventio;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.results.Tokens;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";
    private DrawerLayout drawer;
    private NavigationView mNavigationView;
    private String mUserId;
    private RequestQueue mQueue;

    private ArrayList<String> mCameraPreview = new ArrayList<>();
    private ArrayList<String> mCameraName = new ArrayList<>();
    private ArrayList<String> mNumberViewers = new ArrayList<>();
    private ArrayList<Boolean> mCameraStatus = new ArrayList<>();
    private ArrayList<Object> mCameraStream = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AWSMobileClient.getInstance().getTokens(new Callback<Tokens>() {
            @Override
            public void onResult(Tokens result) {

                mUserId = result.getIdToken().getClaim("sub");

                Log.d(TAG, "onResult: MAINACTIVITY USERID: " + mUserId);


            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG, "onError: " + e);
            }
        });

        //init new fragment home
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                    new FragmentHome(), "HOME_FRAGMENT").commit();
        }

        initToolbar();
        initTicketButton();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = mNavigationView.getHeaderView(0);
        ImageView drawerProfilePic = (ImageView) hView.findViewById(R.id.drawer_profile_pic);
        Glide.with(this).load(R.drawable.photo_jon_snow).apply(RequestOptions.centerCropTransform()).into(drawerProfilePic);


    }

    private void initTicketButton(){
        ImageView ticketButton = (ImageView) findViewById(R.id.icon_ticket);
        ticketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("event.io QR Code Scanner");
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(true);
                integrator.setOrientationLocked(true);
                integrator.initiateScan();

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null){

            if(result.getContents() == null){
                Toast.makeText(this, "No result found", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                String event_id = result.getContents();
                jsonParse(event_id, mUserId);
                clearArrayLists();
            }
        }
    }


    private void jsonParse(String event_id, String user_id){
        mQueue = Volley.newRequestQueue(this);
        String url = "https://vhhuc3efeb.execute-api.us-east-1.amazonaws.com/eventio/ticket?key1=" + event_id + "&key2=" + user_id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONArray streamArray = response.getJSONArray("Stream Data");
                    String eventType = response.getString("Event Type");

                    if(streamArray.length() > 0){
                        for (int i = 0; i <streamArray.length(); i++){
                            JSONObject streamInfo = streamArray.getJSONObject(i);

                            String camera_name = streamInfo.getString("camera_name");
                            Boolean camera_status = streamInfo.getBoolean("camera_status");
                            Object camera_stream = streamInfo.get("camera_stream");
                            String camera_preview = streamInfo.getString("camera_preview");
                            int number_viewers = streamInfo.getInt("number_viewers");

                            mCameraName.add(camera_name);
                            mCameraPreview.add(camera_preview);
                            mNumberViewers.add(String.valueOf(number_viewers));
                            mCameraStatus.add(camera_status);
                            mCameraStream.add(camera_stream);


                        }
                    }

                    if(eventType.equals("sports")){
                        FragmentSportEvent fragmentSportEvent = (FragmentSportEvent) getSupportFragmentManager().findFragmentByTag("SPORT_FRAGMENT");
                        if(fragmentSportEvent == null){
                            //Must call setChecked before transaction otherwise navigation menu doesn't switch back to home
                            mNavigationView.getMenu().getItem(0).setChecked(true);
                            Log.d(TAG, "onResponse: CALLED BRO!");
                            FragmentSportEvent fragmentSportEvent1 = (FragmentSportEvent) new FragmentSportEvent();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentSportEvent1, "SPORT_FRAGMENT").commitAllowingStateLoss();
                            fragmentSportEvent1.recieveTicketId(mCameraName, mCameraPreview, mNumberViewers, mCameraStatus, mCameraStream);
                        }else{
                            fragmentSportEvent.recieveTicketId(mCameraName, mCameraPreview, mNumberViewers, mCameraStatus, mCameraStream);

                        }
                    }

                    if(eventType.equals("music")){
                        FragmentMusicEvent fragmentMusicEvent = (FragmentMusicEvent) getSupportFragmentManager().findFragmentByTag("MUSIC_FRAGMENT");
                        if(fragmentMusicEvent == null){
                            //Must call setChecked before transaction otherwise navigation menu doesn't switch back to home
                            mNavigationView.getMenu().getItem(0).setChecked(true);
                            FragmentMusicEvent fragmentMusicEvent1 = (FragmentMusicEvent) new FragmentMusicEvent();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentMusicEvent1, "MUSIC_FRAGMENT").commitAllowingStateLoss();
                            fragmentMusicEvent1.recieveTicketId(mCameraName, mCameraPreview, mNumberViewers, mCameraStatus, mCameraStream);
                        }else{
                            fragmentMusicEvent.recieveTicketId(mCameraName, mCameraPreview, mNumberViewers, mCameraStatus, mCameraStream);
                        }

                    }


                }catch (Exception e){
                    Log.d(TAG, "Error retrieving stream info " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "GetMessageThread: Function is cold timeout " + error);
            }
        });

        mQueue.add(request);
    }


    private void clearArrayLists(){
        mCameraName.clear();
        mCameraPreview.clear();
        mNumberViewers.clear();
        mCameraStatus.clear();
        mCameraStream.clear();
    }
    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView appName = findViewById(R.id.homepage_appname);
        Drawable appNameIcon = AppCompatResources.getDrawable(this, R.drawable.icon_agora);
        appName.setCompoundDrawablesWithIntrinsicBounds(null, null, appNameIcon, null);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerSlideAnimationEnabled(false);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.getMenu().getItem(0).setChecked(true);
        mNavigationView.setNavigationItemSelectedListener(this);

    }



    @Override
    public void onBackPressed() {

        //Checks if navigation drawer is open
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        //Backstack count is greater than 0, moving to home
        }else if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().executePendingTransactions();

        }else{
            super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentHome(), "HOME_FRAGMENT").commit();
                break;

            case R.id.nav_search:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentSearch()).commit();
                break;

            case R.id.nav_following:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentFollowing()).commit();
                break;

            case R.id.nav_food:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentAvaliableVendors()).commit();
                break;

            case R.id.nav_car:
                Intent intent = new Intent(MainActivity.this, CarParkerActivity.class);
                startActivity(intent);
                return false;

            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentSettings()).commit();
                break;

            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentAbout()).commit();
                break;

            case R.id.nav_signout:

                AWSMobileClient.getInstance().signOut();

                Intent intentSignOut = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intentSignOut);
                this.finish();

                return false;
        }



        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
