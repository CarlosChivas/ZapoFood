package com.example.zapofood.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Review")
public class Review extends ParseObject{
    public static final String KEY_USER = "user";
    public static final String KEY_RESTAURANT = "restaurant";
    public static final String KEY_TEXT = "text";
    public static final String KEY_DATE = "date";
    public static final String KEY_STATUS = "status";


    public ParseObject getRestaurant(){
        try {
            return getParseObject("restaurant").fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setRestaurant(Restaurant restaurant){
        put(KEY_RESTAURANT, restaurant);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser user){ put(KEY_USER, user);}

    public String getText(){return  getString(KEY_TEXT);}
    public void setText(String text) { put(KEY_TEXT, text);}
}