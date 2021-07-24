package com.example.zapofood.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.zapofood.LoginActivity;
import com.example.zapofood.MainActivity;
import com.example.zapofood.R;
import com.example.zapofood.adapters.RestaurantsAdapter;
import com.example.zapofood.models.Restaurant;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class HomeFragment extends Fragment {

    /// TODO: Rename parameter arguments, choose names that match
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
    private NavigationView navigationView;
    private Menu menu;
    private MenuItem searchItem;
    Toolbar toolbar;
    Toolbar toolbar2;
    private String typeSearch = "location";
    private int score;
    private RatingBar ratingBar;

    private FusedLocationProviderClient client;
    private String userCurrentCity = "Guadalajara";
    private LocationRequest mLocationRequest;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Address userAddress;

    public HomeFragment() {
        // Required empty public constructor
    }

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvRestaurants = view.findViewById(R.id.rvRestaurants);
        allRestaurants = new ArrayList<>();
        toolbar2 = (Toolbar) view.findViewById(R.id.toolbarSearchOptions);
        startLocationUpdates();
        //fetchRestaurantsScore(4);
        toolbar = (Toolbar) view.findViewById(R.id.toolbarSearch);
        configToolbar(toolbar);
        restaurantsAdapter = new RestaurantsAdapter(getContext(), allRestaurants);
        // allows for optimizations
        rvRestaurants.setHasFixedSize(true);
        rvRestaurants.setAdapter(restaurantsAdapter);
        // Define 2 column grid layout
        rvRestaurants.setLayoutManager(new GridLayoutManager(getContext(), 2));
        //We create a pop-up for the post details
        restaurantsAdapter.setOnItemClickListener(new RestaurantsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                final FragmentManager fragmentManager = getParentFragmentManager();
                RestaurantDetailsFragment fragmentDemo = RestaurantDetailsFragment.newInstance(allRestaurants.get(position), userAddress);
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContainer, fragmentDemo).commit();
            }
        });
    }
    private void configToolbar(Toolbar toolbar){
        ImageButton imageButtonSearch = toolbar.findViewById(R.id.ibSearch);
        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText objectToSearch = toolbar.findViewById(R.id.etSearch);
                String objectSearch = objectToSearch.getText().toString();
                Toast.makeText(getContext(), "Button pressed", Toast.LENGTH_SHORT).show();
                switch (typeSearch) {
                    case "city":
                        queryRestaurants(objectSearch.toUpperCase());
                        break;
                    case "name":
                        fetchRestaurantsName(objectSearch);
                        break;
                    case "score":
                        fetchRestaurantsScore(score);
                        break;
                    case "location":
                    default:
                        startLocationUpdates();
                        break;
                }
            }
        });
    }

    private void configToolbar2(Toolbar toolbar2){
        Log.i("Testing", "Toolbar 2 selected");
        toolbar2.inflateMenu(R.menu.menu_options_search);
        TextView textViewTitleSearch = toolbar2.findViewById(R.id.tvTitleOptionsSearch);
        textViewTitleSearch.setText("Search by my location (" + userCurrentCity + ")");
        toolbar2.getMenu().findItem(R.id.action_search_location).getIcon().setColorFilter(Color.rgb(255, 127, 35), PorterDuff.Mode.SRC_IN);
        toolbar.setTitle("Automatic");
        toolbar2.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search_location:
                        toolbar.findViewById(R.id.contentToolbarSearch).setVisibility(View.GONE);
                        toolbar.findViewById(R.id.contentToolbarScore).setVisibility(View.GONE);
                        toolbar.setTitle("Automatic");
                        item.getIcon().setColorFilter(Color.rgb(255, 127, 35), PorterDuff.Mode.SRC_IN);
                        toolbar2.getMenu().findItem(R.id.action_search_name).getIcon().setColorFilter(Color.rgb(130, 130, 130), PorterDuff.Mode.SRC_IN);
                        toolbar2.getMenu().findItem(R.id.action_search_score).getIcon().setColorFilter(Color.rgb(130, 130, 130), PorterDuff.Mode.SRC_IN);
                        toolbar2.getMenu().findItem(R.id.action_search_city).getIcon().setColorFilter(Color.rgb(130, 130, 130), PorterDuff.Mode.SRC_IN);
                        textViewTitleSearch.setText("Search by my location (" + userCurrentCity + ")");
                        typeSearch = "location";
                        break;
                    case R.id.action_search_city:
                        toolbar.findViewById(R.id.contentToolbarSearch).setVisibility(View.VISIBLE);
                        toolbar.findViewById(R.id.contentToolbarScore).setVisibility(View.GONE);
                        item.getIcon().setColorFilter(Color.rgb(255, 127, 35), PorterDuff.Mode.SRC_IN);
                        toolbar2.getMenu().findItem(R.id.action_search_name).getIcon().setColorFilter(Color.rgb(130, 130, 130), PorterDuff.Mode.SRC_IN);
                        toolbar2.getMenu().findItem(R.id.action_search_score).getIcon().setColorFilter(Color.rgb(130, 130, 130), PorterDuff.Mode.SRC_IN);
                        toolbar2.getMenu().findItem(R.id.action_search_location).getIcon().setColorFilter(Color.rgb(130, 130, 130), PorterDuff.Mode.SRC_IN);
                        textViewTitleSearch.setText("Search by City");
                        typeSearch = "city";
                        break;
                    case R.id.action_search_name:
                        toolbar.findViewById(R.id.contentToolbarSearch).setVisibility(View.VISIBLE);
                        toolbar.findViewById(R.id.contentToolbarScore).setVisibility(View.GONE);
                        item.getIcon().setColorFilter(Color.rgb(255, 127, 35), PorterDuff.Mode.SRC_IN);
                        toolbar2.getMenu().findItem(R.id.action_search_city).getIcon().setColorFilter(Color.rgb(130, 130, 130), PorterDuff.Mode.SRC_IN);
                        toolbar2.getMenu().findItem(R.id.action_search_score).getIcon().setColorFilter(Color.rgb(130, 130, 130), PorterDuff.Mode.SRC_IN);
                        toolbar2.getMenu().findItem(R.id.action_search_location).getIcon().setColorFilter(Color.rgb(130, 130, 130), PorterDuff.Mode.SRC_IN);
                        textViewTitleSearch.setText("Search by name");
                        typeSearch = "name";
                        break;
                    case R.id.action_search_score:
                    default:
                        toolbar.findViewById(R.id.contentToolbarSearch).setVisibility(View.GONE);
                        toolbar.findViewById(R.id.contentToolbarScore).setVisibility(View.VISIBLE);
                        ratingBar = toolbar.findViewById(R.id.rbSearchScore);
                        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                            @Override
                            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                score = (int) rating;
                            }
                        });
                        ImageButton searchScore = toolbar.findViewById(R.id.ibSearchScore);
                        searchScore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fetchRestaurantsScore(score);
                            }
                        });
                        item.getIcon().setColorFilter(Color.rgb(255, 127, 35), PorterDuff.Mode.SRC_IN);
                        toolbar2.getMenu().findItem(R.id.action_search_name).getIcon().setColorFilter(Color.rgb(130, 130, 130), PorterDuff.Mode.SRC_IN);
                        toolbar2.getMenu().findItem(R.id.action_search_city).getIcon().setColorFilter(Color.rgb(130, 130, 130), PorterDuff.Mode.SRC_IN);
                        toolbar2.getMenu().findItem(R.id.action_search_location).getIcon().setColorFilter(Color.rgb(130, 130, 130), PorterDuff.Mode.SRC_IN);
                        textViewTitleSearch.setText("Search by score");
                        typeSearch = "score";
                        break;
                }
                return true;
            }
        });
    }

    // Find the user location
    protected void startLocationUpdates() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                            //Initialize address list
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            Toast.makeText(getContext(), addresses.get(0).getLocality()/*getAddressLine(0)*/, Toast.LENGTH_SHORT).show();
                            userAddress = addresses.get(0);
                            userCurrentCity = addresses.get(0).getLocality();
                            configToolbar2(toolbar2);
                            queryRestaurants(userCurrentCity.toUpperCase());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    //Method to get the Restaurants
    protected void queryRestaurants(String city) {
        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
        //query.whereEqualTo(Restaurant.KEY_CITY, city);
        query.whereMatches("city", "("+city+")", "i");
        query.setLimit(20);
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

    //Method to find restaurants
    private void fetchRestaurantsName(String name){
        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
        //query.whereEqualTo(Restaurant.KEY_NAME, name);
        //query.whereContains("name", name);
        query.whereMatches("name", "("+name+")", "i");
        query.setLimit(20);
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

    //Method to find restaurants
    private void fetchRestaurantsScore(int score){
        Log.i("Score", "score: "+score);
        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
        query.whereEqualTo(Restaurant.KEY_SCORE, score);
        query.setLimit(20);
        query.findInBackground(new FindCallback<Restaurant>() {
            @Override
            public void done(List<Restaurant> restaurants, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                allRestaurants.clear();
                allRestaurants.addAll(restaurants);
                restaurantsAdapter.notifyDataSetChanged();
            }
        });
    }

}