package com.example.zapofood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import android.os.Bundle;
import android.view.MenuItem;

import com.example.zapofood.fragments.HomeFragment;
import com.example.zapofood.fragments.MyReservationsFragment;
import com.example.zapofood.fragments.RestaurantDetailsFragment;
import com.example.zapofood.fragments.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.facebook.login.LoginManager;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;
    private List<ParseObject> friends;
    private List<ParseObject> allFriends;
    private List<ParseObject> requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        friends = new ArrayList<>();
        allFriends = new ArrayList<>();
        requests = new ArrayList<>();
        //configUser();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_myreservations:
                        fragment = new MyReservationsFragment();
                        break;
                    case R.id.action_user:
                    default:
                        fragment = UserFragment.newInstance(ParseUser.getCurrentUser(), friends, allFriends, requests);
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    private void configUser(){
        allFriends = ParseUser.getCurrentUser().getList("friends");
        for (ParseObject parseObject : allFriends){
            try {
                parseObject.fetch();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        for(int i =0; i<3; i++){
            friends.add(allFriends.get(i));
        }

        requests = ParseUser.getCurrentUser().getList("requests");
        for(ParseObject parseObject : requests){
            try {
                parseObject.fetch();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}