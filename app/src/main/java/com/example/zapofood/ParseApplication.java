package com.example.zapofood;

import android.app.Application;

import com.example.zapofood.models.Reservation;
import com.example.zapofood.models.Restaurant;
import com.example.zapofood.models.Review;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Register the parse model
        ParseObject.registerSubclass(Restaurant.class);
        ParseObject.registerSubclass(Reservation.class);
        ParseObject.registerSubclass(Review.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());
    }
}

