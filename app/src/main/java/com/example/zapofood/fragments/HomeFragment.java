package com.example.zapofood.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

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
    private NavigationView navigationView;
    private Menu menu;
    private MenuItem searchItem;

    private FusedLocationProviderClient client;
    private String userCity;

    private LocationRequest mLocationRequest;

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
        startLocationUpdates();
        restaurantsAdapter = new RestaurantsAdapter(getContext(), allRestaurants);
        // allows for optimizations
        rvRestaurants.setHasFixedSize(true);
        rvRestaurants.setAdapter(restaurantsAdapter);
        // Define 2 column grid layout
        rvRestaurants.setLayoutManager(new GridLayoutManager(getContext(), 2));

        navigationView = view.findViewById(R.id.navigationView);
        menu = navigationView.getMenu();
        searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Make the query with the name written
                fetchRestaurantsName(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    // Find the user location
    protected void startLocationUpdates() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // We need check of we have the permissions
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // new Google API SDK v11 uses getFusedLocationProviderClient(this)
                    getFusedLocationProviderClient(getContext()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            String userCity = getCity(getContext(), locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                            if(userCity != null){
                                queryRestaurants(userCity.toUpperCase());
                            }
                            else {
                                Toast.makeText(getContext(), "Error to get the current city", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, Looper.myLooper());
        }
    }

    //Method for get user city
    public static String getCity(Context context, double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                String city = addresses.get(0).getLocality();
                Toast.makeText(context.getApplicationContext(), city, Toast.LENGTH_SHORT).show();
                return city;
            }
        } catch (IOException e) {
            Log.i("Location", "Error with the address" + e);
            e.printStackTrace();
        }
        return null;
    }

    //Method to get the Restaurants
    protected void queryRestaurants(String city) {
        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
        query.whereEqualTo(Restaurant.KEY_CITY, city);
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
        query.whereEqualTo(Restaurant.KEY_NAME, name);
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
        Toast.makeText(getContext(), "Llego al name", Toast.LENGTH_SHORT).show();

    }
    //Method to find restaurants
    private void fetchRestaurantsCity(String name){

    }
    //Method to find restaurants
    private void fetchRestaurantsScore(String name){

    }

}