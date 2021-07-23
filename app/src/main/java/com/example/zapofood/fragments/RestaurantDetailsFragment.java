package com.example.zapofood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zapofood.R;
import com.example.zapofood.models.Restaurant;
import com.parse.ParseFile;

public class RestaurantDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView tvTitleRestaurant;
    private ImageButton btnBackHome;
    private ImageView ivRestaurantImage;
    RatingBar rbVoteAverage;
    private TextView tvDescriptionRestaurant;
    private TextView tvAddressRestaurant;
    private Button btnMakeReservation;

    public static RestaurantDetailsFragment newInstance(Restaurant restaurant) {
        RestaurantDetailsFragment fragmentDemo = new RestaurantDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("restaurant", restaurant);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    public RestaurantDetailsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RestaurantDetailsFragment newInstance(String param1, String param2) {
        RestaurantDetailsFragment fragment = new RestaurantDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_details, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Restaurant restaurant = getArguments().getParcelable("restaurant");
        tvTitleRestaurant = view.findViewById(R.id.tvTitlteRestaurant);
        btnBackHome = view.findViewById(R.id.btnBackHome);
        btnMakeReservation = view.findViewById(R.id.btnMakeReservation);
        ivRestaurantImage = view.findViewById(R.id.ivRestaurantImage);
        rbVoteAverage = view.findViewById(R.id.rbVoteAverage);
        tvDescriptionRestaurant = view.findViewById(R.id.tvDescriptionRestaurant);
        tvAddressRestaurant = view.findViewById(R.id.tvAddressRestaurant);
        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        });
        tvTitleRestaurant.setText(restaurant.getName());
        btnMakeReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                MakeReservationFragment fragmentDemo = MakeReservationFragment.newInstance(restaurant);
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContainer, fragmentDemo).commit();
            }
        });
        ParseFile image = restaurant.getImage();
        Glide.with(getContext()).load(image.getUrl()).into(ivRestaurantImage);
        rbVoteAverage.setRating((float) restaurant.getScore());
        tvDescriptionRestaurant.setText(restaurant.getDescription());
        tvAddressRestaurant.setText(restaurant.getAddress());
    }
}