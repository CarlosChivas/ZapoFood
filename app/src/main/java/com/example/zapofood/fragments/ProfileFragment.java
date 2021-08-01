package com.example.zapofood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zapofood.R;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageButton btnBackProfile;
    private ImageView ivImageProfile;
    private TextView tvNameProfile;
    private ParseUser userSelected;
    private List<ParseObject> myFriends;
    private RelativeLayout containerStatusFriend;


    public static ProfileFragment newInstance(ParseObject parseObject, List<ParseObject> myFriends, List<ParseObject> myRequestsSent, int position) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", parseObject);
        args.putParcelableArrayList("myFriends", (ArrayList<? extends Parcelable>) myFriends);
        args.putParcelableArrayList("myRequestsSent", (ArrayList<? extends Parcelable>) myRequestsSent);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myFriends = getArguments().getParcelableArrayList("myFriends");
        btnBackProfile = view.findViewById(R.id.btnBackProfile);
        userSelected = getArguments().getParcelable("user");
        ivImageProfile = view.findViewById(R.id.ivImageProfile);
        tvNameProfile = view.findViewById(R.id.tvNameProfile);

        btnBackProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        ParseFile image = userSelected.getParseFile("image");
        Glide.with(getContext()).load(image.getUrl()).circleCrop().into(ivImageProfile);
        tvNameProfile.setText(userSelected.getUsername());

        if(checkFriend()){
            containerDeleteFriend(view);
        }else if(checkRequestSent()){
            containerRequestSent(view);
        }
        else{
            containerAddFriend(view);
        }
    }

    private boolean checkFriend(){
        for(ParseObject parseObject : myFriends){
            if(parseObject.getObjectId().equals(userSelected.getObjectId())){
                return true;
            }
        }
        return false;
    }
    private boolean checkRequestSent(){
        List<ParseObject> myRequestsSent = getArguments().getParcelableArrayList("myRequestsSent");
        for(ParseObject parseObject : myRequestsSent){
            if(parseObject.getObjectId().equals(userSelected.getObjectId())){
                return true;
            }
        }
        return false;
    }

    private void deleteFriend(List<ParseObject> myFriends){
        ParseUser newUser = ParseUser.getCurrentUser();
        newUser.put("friends", myFriends);
        newUser.saveInBackground();
    }
    public void containerDeleteFriend(View view){
        containerStatusFriend = view.findViewById(R.id.containerDeleteFriend);
        containerStatusFriend.setVisibility(View.VISIBLE);

        containerStatusFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("objectId", userSelected.getObjectId());
                params.put("deleteFriendId", ParseUser.getCurrentUser().getObjectId());
                ParseCloud.callFunctionInBackground("deleteFriend", params, new FunctionCallback<String>() {
                    @Override
                    public void done(String object, ParseException e) {
                        if(e != null){
                            Toast.makeText(getContext(), "Error deleting friend", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                myFriends.remove(getArguments().getInt("position"));
                deleteFriend(myFriends);
                containerStatusFriend.setVisibility(View.GONE);
                containerAddFriend(view);
            }
        });
    }
    public void containerAddFriend(View view){
        containerStatusFriend = view.findViewById(R.id.containerAddFriend);
        containerStatusFriend.setVisibility(View.VISIBLE);
        containerStatusFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("objectId", userSelected.getObjectId());
                params.put("newObjectId", ParseUser.getCurrentUser().getObjectId());
                ParseCloud.callFunctionInBackground("editUserProperty", params, new FunctionCallback<String>() {
                    @Override
                    public void done(String object, ParseException e) {
                        if(e != null){
                            Toast.makeText(getContext(), "Error sending the request", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                List<ParseObject> sentRequests  = ParseUser.getCurrentUser().getList("sentRequests");
                sentRequests.add(userSelected);
                ParseUser newUser = ParseUser.getCurrentUser();
                newUser.put("sentRequests", sentRequests);
                newUser.saveInBackground();
                containerStatusFriend.setVisibility(View.GONE);
                containerRequestSent(view);
            }
        });
    }

    public void containerRequestSent(View view){
        containerStatusFriend = view.findViewById(R.id.containerRequestSent);
        containerStatusFriend.setVisibility(View.VISIBLE);
    }
}