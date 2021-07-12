package com.example.zapofood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.zapofood.fragments.HomeFragment;
import com.example.zapofood.fragments.MyReservationsFragment;
import com.example.zapofood.fragments.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        /*ImageView iconView = (ImageView) findViewById(R.id.icon);

        iconView.setColorFilter(selected ?
                getResources().getColor(R.color.navdrawer_icon_selected_tint) :
                getResources().getColor(R.color.navdrawer_icon_tint));*/
        //bottomNavigationView.setItemIconTintList(null);
        //bottomNavigationView.setItemTextColor();
        //bottomNavigationView.setItemIconTintList(ContextCompat.getColorStateList(this, R.color.my_icon_tint));

        //bottomNavigationView.getMenu().findItem(R.id.action_myreservations).getIcon().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

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
                        fragment = new UserFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
    }
}