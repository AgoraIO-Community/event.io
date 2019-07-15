package com.example.evan.eventio;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import static android.support.constraint.Constraints.TAG;

public class FragmentVideo extends Fragment {

    private String mVideoUrl, mCameraName;
    private VideoView videoView;
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        if(b != null){

            mVideoUrl = b.getString("camera_stream");
            mCameraName = b.getString("camera_name");

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        initToolBar(view);

        videoView = (VideoView) view.findViewById(R.id.fragment_video_video_view);
        progressBar = (ProgressBar) view.findViewById(R.id.video_progress_bar);
        Uri videoUrl = Uri.parse(mVideoUrl);
        videoView.setVideoURI(videoUrl);
        videoView.start();
        progressBar.setVisibility(View.VISIBLE);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int arg1,
                                                   int arg2) {
                        progressBar.setVisibility(View.GONE);
                        mp.start();
                    }
                });
            }
        });




        return view;
    }

    private void initToolBar(View view){
        ImageView closeFragment = (ImageView) view.findViewById(R.id.fragment_video_white_x_mark);
        closeFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked white x mark");
                getFragmentManager().popBackStack();
            }
        });

        TextView cameraName = (TextView) view.findViewById(R.id.fragment_video_toolbar_name);
        cameraName.setText(mCameraName);
    }

}
