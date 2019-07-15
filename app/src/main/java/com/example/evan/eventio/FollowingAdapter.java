package com.example.evan.eventio;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ViewHolder> {


    private ArrayList<String> mFollowingChannelName = new ArrayList<>();
    private ArrayList<String> mFollowingTempToken = new ArrayList<>();
    private ArrayList<String> mFollowingPreview = new ArrayList<>();
    private ArrayList<String> mFollowingCameraName = new ArrayList<>();
    private ArrayList<String> mFollowingNumberViewers = new ArrayList<>();
    private ArrayList<Boolean> mFollowingStreamStatus = new ArrayList<>();
    private Context mContext;

    public FollowingAdapter(ArrayList<String> mFollowingChannelName, ArrayList<String> mFollowingTempToken,
                            ArrayList<String> mFollowingPreview, ArrayList<String> mFollowingCameraName,
                            ArrayList<String> mFollowingNumberViewers, ArrayList<Boolean> mFollowingStreamStatus,
                            Context mContext) {
        this.mFollowingChannelName = mFollowingChannelName;
        this.mFollowingTempToken = mFollowingTempToken;
        this.mFollowingPreview = mFollowingPreview;
        this.mFollowingCameraName = mFollowingCameraName;
        this.mFollowingNumberViewers = mFollowingNumberViewers;
        this.mFollowingStreamStatus = mFollowingStreamStatus;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public FollowingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.model_rv_single_camera_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FollowingAdapter.ViewHolder viewHolder, final int position) {

        viewHolder.viewHolderCameraName.setText(mFollowingCameraName.get(position));
        viewHolder.viewHolderNumberViewers.setText(mFollowingNumberViewers.get(position));

        TextView numberViewers = viewHolder.itemView.findViewById(R.id.number_viewers);
        Drawable onlineIcon = AppCompatResources.getDrawable(mContext, R.drawable.icon_online);
        numberViewers.setCompoundDrawablesWithIntrinsicBounds(onlineIcon, null, null, null);


        Glide.with(mContext).load(mFollowingPreview.get(position)).apply(RequestOptions.centerCropTransform()).into(viewHolder.viewHolderCameraPreview);


        viewHolder.viewHolderRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(viewHolder.itemView.getContext(), CameraActivity.class);
                Bundle b = new Bundle();
                    b.putString("channel_name", mFollowingChannelName.get(position));
                    b.putString("temp_token", mFollowingTempToken.get(position));
                    b.putString("camera_name", mFollowingCameraName.get(position));
                    intent.putExtras(b);


                viewHolder.itemView.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mFollowingCameraName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView viewHolderCameraPreview;
        TextView viewHolderCameraName, viewHolderNumberViewers;
        RelativeLayout viewHolderRelativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            viewHolderCameraPreview = itemView.findViewById(R.id.camera_preview);
            viewHolderCameraName = itemView.findViewById(R.id.camera_angle);
            viewHolderNumberViewers = itemView.findViewById(R.id.number_viewers);

            viewHolderRelativeLayout = itemView.findViewById(R.id.recycler_camera_rel_layout);
        }
    }
}
