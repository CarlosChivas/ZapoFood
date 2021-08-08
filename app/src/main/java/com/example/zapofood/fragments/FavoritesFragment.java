package com.example.zapofood.fragments;

import android.app.admin.FactoryResetProtectionPolicy;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.zapofood.R;
import com.example.zapofood.adapters.ReservationsAdapter;
import com.example.zapofood.adapters.RestaurantsAdapter;
import com.example.zapofood.models.Restaurant;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageButton btnBackUser;
    private RecyclerView rvFavorites;
    private RestaurantsAdapter restaurantsAdapter;
    private List<Restaurant> favorites;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    public static FavoritesFragment newInstance(String param1, String param2) {
        FavoritesFragment fragment = new FavoritesFragment();
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
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnBackUser = view.findViewById(R.id.btnBackUser);
        rvFavorites = view.findViewById(R.id.rvFavorites);
        favorites = new ArrayList<>();

        restaurantsAdapter = new RestaurantsAdapter(getContext(), favorites);
        // allows for optimizations
        rvFavorites.setHasFixedSize(true);
        rvFavorites.setAdapter(restaurantsAdapter);
        // Define 2 column grid layout
        rvFavorites.setLayoutManager(new GridLayoutManager(getContext(), 2));
        //We create a pop-up for the post details
        restaurantsAdapter.setOnItemClickListener(new RestaurantsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                final FragmentManager fragmentManager = getParentFragmentManager();
                RestaurantDetailsFragment fragmentDemo = RestaurantDetailsFragment.newInstance(favorites.get(position), null);
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContainer, fragmentDemo).commit();
            }
        });

        btnBackUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        fetchFavorites();
    }

    public void fetchFavorites(){
        List<Restaurant> myFavorites = ParseUser.getCurrentUser().getList("favorites");
        for(Restaurant restaurant : myFavorites){
            try {
                favorites.add(restaurant.fetch());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        restaurantsAdapter.notifyDataSetChanged();
    }
}