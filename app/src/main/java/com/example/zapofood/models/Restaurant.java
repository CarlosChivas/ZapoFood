package com.example.zapofood.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Restaurant")
public class Restaurant extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_NAME = "name";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_CITY = "city";
    public static final String KEY_SCORE = "score";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_OWNER = "owner";

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }
    public String getName() { return getString(KEY_NAME);}
    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }
    public String getCity(){return getString(KEY_CITY);}
    public int getScore(){return getInt(KEY_SCORE);}
    public String getAddress(){ return getString(KEY_ADDRESS); }
    public ParseUser getOwner() { return getParseUser(KEY_OWNER);}

}
