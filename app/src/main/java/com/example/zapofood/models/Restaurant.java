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

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }
    public String getName() { return getString(KEY_NAME);}
    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }
}