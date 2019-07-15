package com.example.evan.eventio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class CameraActivity extends AppCompatActivity  {

    private RtcEngine mRtcEngine;
    private IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                }
            });
        }

    };

    private static final String TAG = "CameraActivity";
    private String mToken, mChannelName, mCameraName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mChannelName = getIntent().getExtras().getString("channel_name");
        mToken = getIntent().getExtras().getString("temp_token");
        mCameraName = getIntent().getExtras().getString("camera_name");

        Log.d(TAG, "onCreate: Channel Name: " + mChannelName);
        Log.d(TAG, "onCreate: Temp Token: " + mToken);

        TextView streamButton = (TextView) findViewById(R.id.stream_button);
        streamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initAgoraEngineAndJoinChannel();
            }
        });


        ImageView xmark = findViewById(R.id.xmark);
        xmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mRtcEngine != null){
                    leaveChannel();
                }

                finish();

            }
        });

        ImageView switchCamera = findViewById(R.id.imageview_switch_camera);
        switchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRtcEngine.switchCamera();
            }
        });

        TextView cameraName = findViewById(R.id.activity_camera_camera_name);
        cameraName.setText(mCameraName);

    }

    private void initAgoraEngineAndJoinChannel(){
        initializeAgoraEngine();
        setupVideoProfile();
        setupLocalVideo();
        joinChannel();

    }
    private void initializeAgoraEngine() {

        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
            mRtcEngine.switchCamera();
        } catch (Exception e) {
            Log.d(TAG, "SOMETHING WENT WRONG: " + e);
        }
    }

    private void setupVideoProfile() {
        mRtcEngine.enableVideo();
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x360, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideo() {
        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));

    }

    private void joinChannel() {
        if(mToken.isEmpty()) {
            mToken = null;
        }
        mRtcEngine.joinChannel(mToken, mChannelName, "Extra Optional Data", 0);
        // if you do not specify the uid, we will generate the uid for you
    }

    private void setupRemoteVideo(int uid) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        if (container.getChildCount() >= 1) {
            return;
        }

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));


        surfaceView.setTag(uid);

    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }
}



