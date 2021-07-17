package com.example.zapofood.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Reservation")
public class Reservation extends ParseObject{
    public static final String KEY_USER = "user";
    public static final String KEY_RESTAURANT = "restaurant";
    public static final String KEY_DATE = "date";

    /*public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }
    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }
    public int getLikes(){ return getInt(KEY_LIKES);}
    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }
    public void setImage(ParseFile parseFile){
        put(KEY_IMAGE, parseFile);
    }*/
    public void setDate(Date date){
        put(KEY_DATE, date);
    }
    public ParseObject getRestaurant(){
        return getParseObject("restaurant");
    }
    public void setRestaurant(Restaurant restaurant){
        put(KEY_RESTAURANT, restaurant);
    }
    //public void setRestaurant(Re)
    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }
}
