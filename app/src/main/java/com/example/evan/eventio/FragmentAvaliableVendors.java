package com.example.evan.eventio;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class FragmentAvaliableVendors extends Fragment {



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food, container, false);


        ImageView imageView = (ImageView) view.findViewById(R.id.food_image);
        Glide.with(this).load(R.drawable.stadiumvendors).apply(RequestOptions.fitCenterTransform()).into(imageView);

        ImageView beer = (ImageView) view.findViewById(R.id.icon_beer);
        ImageView hotdog = (ImageView) view.findViewById(R.id.icon_hotdogs);
        ImageView burger = (ImageView) view.findViewById(R.id.icon_burgers);
        ImageView fries = (ImageView) view.findViewById(R.id.icon_fries);
        ImageView jerseys = (ImageView) view.findViewById(R.id.icon_jerseys);
        ImageView hat = (ImageView) view.findViewById(R.id.icon_hat);

        Glide.with(this).load(R.drawable.food_beer).apply(RequestOptions.centerCropTransform()).into(beer);
        Glide.with(this).load(R.drawable.food_hotdog).apply(RequestOptions.centerCropTransform()).into(hotdog);
        Glide.with(this).load(R.drawable.food_burger).apply(RequestOptions.centerCropTransform()).into(burger);
        Glide.with(this).load(R.drawable.food_fries).apply(RequestOptions.centerCropTransform()).into(fries);
        Glide.with(this).load(R.drawable.food_jersey).apply(RequestOptions.centerCropTransform()).into(jerseys);
        Glide.with(this).load(R.drawable.hat).apply(RequestOptions.centerCropTransform()).into(hat);

        return view;
    }
}
