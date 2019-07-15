package com.example.evan.eventio;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;


public class FragmentMusicEvent extends Fragment {

    private RecyclerView cameraRecycler, followingRecycler;
    private CameraAdapter cameraAdapter;
    private FollowingAdapter followingAdapter;

    private ArrayList<String> mCameraPreview = new ArrayList<>();
    private ArrayList<String> mCameraName = new ArrayList<>();
    private ArrayList<String> mNumberViewers = new ArrayList<>();
    private ArrayList<Boolean> mCameraStatus = new ArrayList<>();
    private ArrayList<Object> mCameraStream = new ArrayList<>();

    private ArrayList<String> mFollowingPreview = new ArrayList<>();
    private ArrayList<String> mFollowingCameraType = new ArrayList<>();
    private ArrayList<String> mFollowingNumberViewers = new ArrayList<>();

    private Context mContext;

    public void recieveTicketId(ArrayList<String> camera_name, ArrayList<String> camera_preview, ArrayList<String> number_viewers,
                                ArrayList<Boolean> camera_status, ArrayList<Object> camera_stream){

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
        //followingAdapter = new FollowingAdapter(mFollowingPreview, mFollowingCameraType, mFollowingNumberViewers, mContext);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_events, container, false);

        ImageView musicLayout = (ImageView) view.findViewById(R.id.music_image);
        Glide.with(this).load(R.drawable.coachella).apply(RequestOptions.fitCenterTransform()).into(musicLayout);


        cameraRecycler = view.findViewById(R.id.music_camera_recycler);
        cameraRecycler.setAdapter(cameraAdapter);
        cameraRecycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        //followingRecycler = view.findViewById(R.id.following_recycler);
        //followingRecycler.setAdapter(followingAdapter);
        //followingRecycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));


        return view;
    }

}
