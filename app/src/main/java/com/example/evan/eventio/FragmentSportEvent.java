package com.example.evan.eventio;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import static android.support.constraint.Constraints.TAG;


public class FragmentSportEvent extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView cameraRecycler, followingRecycler;
    private CameraAdapter cameraAdapter;
    private FollowingAdapter followingAdapter;

    private ArrayList<String> mCameraPreview = new ArrayList<>();
    private ArrayList<String> mCameraName = new ArrayList<>();
    private ArrayList<String> mNumberViewers = new ArrayList<>();
    private ArrayList<Boolean> mCameraStatus = new ArrayList<>();
    private ArrayList<Object> mCameraStream = new ArrayList<>();

    private ArrayList<String> mFollowingChannelName = new ArrayList<>();
    private ArrayList<String> mFollowingTempToken = new ArrayList<>();
    private ArrayList<String> mFollowingPreview = new ArrayList<>();
    private ArrayList<String> mFollowingCameraName = new ArrayList<>();
    private ArrayList<String> mFollowingNumberViewers = new ArrayList<>();
    private ArrayList<Boolean> mFollowingStreamStatus = new ArrayList<>();

    private TextView mTextViewCountDown;
    private static final long START_TIME_IN_MILLIS = 720000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RequestQueue mQueue;

    private TextView mFollowing;
    private Context mContext;


    public void recieveTicketId(ArrayList<String> camera_name, ArrayList<String> camera_preview, ArrayList<String> number_viewers,
                                ArrayList<Boolean> camera_status, ArrayList<Object> camera_stream){

        Log.d(TAG, "recieveTicketId: Sports: " + camera_name);

        mCameraName = camera_name;
        mCameraPreview = camera_preview;
        mNumberViewers = number_viewers;
        mCameraStatus = camera_status;
        mCameraStream = camera_stream;

        cameraAdapter.notifyDataSetChanged();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();

        cameraAdapter = new CameraAdapter(mCameraPreview, mCameraName, mNumberViewers, mCameraStatus, mCameraStream, mContext);
        followingAdapter = new FollowingAdapter(mFollowingChannelName, mFollowingTempToken, mFollowingPreview,
                                                mFollowingCameraName, mFollowingNumberViewers, mFollowingStreamStatus, mContext);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sport_events, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mFollowing = (TextView) view.findViewById(R.id.textview_following);

        ImageView matchImage = (ImageView) view.findViewById(R.id.match_image);
        Glide.with(this).load(R.drawable.blackwall).apply(RequestOptions.centerCropTransform()).into(matchImage);

        ImageView rightTeam = (ImageView) view.findViewById(R.id.right_team);
        Glide.with(this).load(R.drawable.raptors_transparent).apply(RequestOptions.fitCenterTransform()).into(rightTeam);


        ImageView leftTeam = (ImageView) view.findViewById(R.id.left_team);
        Glide.with(this).load(R.drawable.testlogo).apply(RequestOptions.fitCenterTransform()).into(leftTeam);

        mTextViewCountDown = view.findViewById(R.id.textview_timer);
        startTimer();







        cameraRecycler = view.findViewById(R.id.sport_camera_recycler);
        cameraRecycler.setAdapter(cameraAdapter);
        cameraRecycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        followingRecycler = view.findViewById(R.id.following_recycler);
        followingRecycler.setAdapter(followingAdapter);
        followingRecycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));




        return view;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                refreshPage();
                clearArrays();

            }
        }, 2000);
    }

    private void clearArrays(){
        mFollowingChannelName.clear();
        mFollowingTempToken.clear();
        mFollowingPreview.clear();
        mFollowingCameraName.clear();
        mFollowingNumberViewers.clear();
        mFollowingStreamStatus.clear();
    }

    private void refreshPage(){
        mQueue = Volley.newRequestQueue(mContext);
        String url = "https://vhhuc3efeb.execute-api.us-east-1.amazonaws.com/eventio/followedstreamers";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONArray streamArray = response.getJSONArray("Streaming");

                    Log.d(TAG, "onResponse: STREAMARRAY: " + streamArray);

                    if(streamArray.length() > 0){
                        for (int i = 0; i <streamArray.length(); i++){
                            JSONObject streamInfo = streamArray.getJSONObject(i);

                            String channel_name = streamInfo.getString("channel_name");
                            String temp_token = streamInfo.getString("temp_token");

                            String follow_stream_preview = streamInfo.getString("follow_stream_preview");
                            String follow_stream_name = streamInfo.getString("follow_stream_name");
                            int number_viewers = streamInfo.getInt("number_viewers");
                            boolean stream_status = streamInfo.getBoolean("stream_status");

                            mFollowingChannelName.add(channel_name);
                            mFollowingTempToken.add(temp_token);
                            mFollowingPreview.add(follow_stream_preview);
                            mFollowingCameraName.add(follow_stream_name);
                            mFollowingNumberViewers.add(String.valueOf(number_viewers));
                            mFollowingStreamStatus.add(stream_status);



                        }
                    }

                    if(mFollowingCameraName.size() > 0){
                        mFollowing.setVisibility(View.VISIBLE);
                    }else{
                        mFollowing.setVisibility(View.INVISIBLE);
                    }

                    followingAdapter.notifyDataSetChanged();



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

    private void startTimer(){
        CountDownTimer mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    private void updateCountDownText(){
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        mTextViewCountDown.setText(timeLeftFormatted);
    }

}
