package com.example.zapofood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.zapofood.fragments.HomeFragment;
import com.example.zapofood.fragments.MakeReservationFragment;
import com.example.zapofood.fragments.MyReservationsFragment;
import com.example.zapofood.fragments.RestaurantDetailsFragment;
import com.example.zapofood.fragments.UserFragment;
import com.example.zapofood.models.Reservation;
import com.example.zapofood.models.Restaurant;
import com.example.zapofood.ownerfragments.OwnerHomeFragment;
import com.example.zapofood.ownerfragments.OwnerReservationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class OwnerMainActivity extends AppCompatActivity{

    public static final String TAG = "OwnerMainActivity";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;
    Restaurant restaurant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        restaurant = new Restaurant();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        findRestaurant();
    }

    private void findRestaurant(){
        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
        query.whereEqualTo(Restaurant.KEY_OWNER, ParseUser.getCurrentUser());
        query.setLimit(20);
        query.findInBackground(new FindCallback<Restaurant>() {
            @Override
            public void done(List<Restaurant> restaurants, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                restaurant = restaurants.get(0);
                startBottomNavigation();
            }
        });
    }

    private void startBottomNavigation(){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = new OwnerHomeFragment();
                        break;
                    case R.id.action_myreservations:
                        fragment = OwnerReservationsFragment.newInstance(restaurant);
                        break;
                    case R.id.action_user:
                    default:
                        fragment = new UserFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

}