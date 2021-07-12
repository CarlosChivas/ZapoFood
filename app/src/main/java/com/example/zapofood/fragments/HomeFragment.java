package com.example.zapofood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zapofood.R;
import com.example.zapofood.adapters.RestaurantsAdapter;
import com.example.zapofood.models.Restaurant;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final String TAG = "HomeFragment";
    protected List<Restaurant> allRestaurants;
    private RecyclerView rvRestaurants;
    protected RestaurantsAdapter restaurantsAdapter;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvRestaurants = view.findViewById(R.id.rvRestaurants);
        allRestaurants = new ArrayList<>();
        restaurantsAdapter = new RestaurantsAdapter(getContext(), allRestaurants);
        // allows for optimizations
        rvRestaurants.setHasFixedSize(true);
        rvRestaurants.setAdapter(restaurantsAdapter);
        // Define 2 column grid layout
        rvRestaurants.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // get the restaurants for mmy database
        queryRestaurants();
    }

    //Method to get the Posts
    protected void queryRestaurants() {
        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
        //query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(Restaurant.KEY_CREATED);
        query.findInBackground(new FindCallback<Restaurant>() {
            @Override
            public void done(List<Restaurant> restaurants, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Restaurant restaurant : restaurants) {
                    Log.i(TAG, "Posts: " + restaurant.getDescription());
                }
                allRestaurants.clear();
                allRestaurants.addAll(restaurants);
                restaurantsAdapter.notifyDataSetChanged();
            }
        });
    }
}


