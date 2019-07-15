package com.example.evan.eventio;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.ViewHolder> {

    private ArrayList<String> mCameraPreview;
    private ArrayList<String> mCameraType;
    private ArrayList<String> mNumberViewers;
    private ArrayList<Boolean> mCameraStatus = new ArrayList<>();
    private ArrayList<Object> mCameraStream = new ArrayList<>();
    private Context mContext;


    public CameraAdapter(ArrayList<String> mCameraPreview, ArrayList<String> mCameraType, ArrayList<String> mNumberViewers,
                         ArrayList<Boolean> mCameraStatus, ArrayList<Object> mCameraStream, Context mContext) {
        this.mCameraPreview = mCameraPreview;
        this.mCameraType = mCameraType;
        this.mNumberViewers = mNumberViewers;
        this.mCameraStatus = mCameraStatus;
        this.mCameraStream = mCameraStream;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.model_rv_single_camera_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        viewHolder.viewHolderCameraType.setText(mCameraType.get(position));
        viewHolder.viewHolderNumberViewers.setText(mNumberViewers.get(position));

        if(!mCameraType.get(position).equals("Offline")){
            TextView numberViewers = viewHolder.itemView.findViewById(R.id.number_viewers);
            Drawable onlineIcon = AppCompatResources.getDrawable(mContext, R.drawable.icon_online);
            numberViewers.setCompoundDrawablesWithIntrinsicBounds(onlineIcon, null, null, null);
        }else if(mCameraType.get(position).equals("Offline")){
            TextView numberViewers = viewHolder.itemView.findViewById(R.id.number_viewers);
            Drawable offline = AppCompatResources.getDrawable(mContext, R.drawable.icon_offline);
            numberViewers.setCompoundDrawablesWithIntrinsicBounds(offline, null, null, null);
        }



        Glide.with(mContext).load(mCameraPreview.get(position)).apply(RequestOptions.centerCropTransform()).into(viewHolder.viewHolderCameraPreview);




        viewHolder.viewHolderRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentVideo fragmentVideo = (FragmentVideo) new FragmentVideo();
                Bundle b = new Bundle();

                if(mCameraStream.get(position) != null){

                    String camera_stream = (String) mCameraStream.get(position);
                    b.putString("camera_stream", camera_stream);
                    b.putString("camera_name", mCameraType.get(position));


                    fragmentVideo.setArguments(b);

                    AppCompatActivity activity = (AppCompatActivity) viewHolder.itemView.getContext();
                    activity.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragmentVideo).addToBackStack(null).commit();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mCameraType.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView viewHolderCameraPreview;
        TextView viewHolderCameraType, viewHolderNumberViewers;
        RelativeLayout viewHolderRelativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            viewHolderCameraPreview = itemView.findViewById(R.id.camera_preview);
            viewHolderCameraType = itemView.findViewById(R.id.camera_angle);
            viewHolderNumberViewers = itemView.findViewById(R.id.number_viewers);
            viewHolderRelativeLayout = itemView.findViewById(R.id.recycler_camera_rel_layout);
        }
    }
}
