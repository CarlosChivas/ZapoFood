package com.example.zapofood.fragments;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.zapofood.LoginActivity;
import com.example.zapofood.R;
import com.example.zapofood.adapters.FriendsAdapter;
import com.example.zapofood.models.Restaurant;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button btnUserLogout;
    private ImageView ivImageProfile;
    private TextView tvNameProfile;
    private RecyclerView rvPreviewFriends;
    private FriendsAdapter friendsAdapter;
    private List<ParseObject> friends;

    public static UserFragment newInstance(ParseUser user) {
        UserFragment fragmentDemo = new UserFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    public UserFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ParseUser currentUser = getArguments().getParcelable("user");
        ivImageProfile = view.findViewById(R.id.ivImageProfile);
        tvNameProfile = view.findViewById(R.id.tvNameProfile);
        rvPreviewFriends = view.findViewById(R.id.rvPreviewFriends);
        btnUserLogout = view.findViewById(R.id.btnUserLogout);

        ParseFile image = currentUser.getParseFile("image");
        Glide.with(getContext()).load(image.getUrl()).circleCrop().into(ivImageProfile);
        tvNameProfile.setText(currentUser.getUsername());
        friends = new ArrayList<>();
        friendsAdapter = new FriendsAdapter(getContext(), friends);
        rvPreviewFriends.setHasFixedSize(true);
        rvPreviewFriends.setAdapter(friendsAdapter);
        rvPreviewFriends.setLayoutManager(new GridLayoutManager(getContext(), 3));
        friendsAdapter.setOnItemClickListener(new FriendsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Log.i("Friends", "Hola mundo");
            }
        });

        getFriends();

        btnUserLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout(){
        //User will be null
        ParseUser.logOutInBackground(e -> {
            if (e == null)
                Log.i("Logout", "LogOut success");
        });
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
    }
    private void getFriends(){
        //ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        /*JSONArray jsonArray = ParseUser.getCurrentUser().getJSONArray("friends");
        List<ParseUser> userFriends = new ArrayList<>();
        for(int i = 0; i<jsonArray.length(); i++){
            try {
                jsonArray.get
                friends.add((ParseUser) jsonArray.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
        List<ParseObject> userFriends = ParseUser.getCurrentUser().getList("friends");
        friends.addAll(userFriends);
        friendsAdapter.notifyDataSetChanged();

        /*try {
            Log.d("Friends", parseObject.fetchIfNeeded().getString("username"));
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        /*friends.addAll(ParseUser.getCurrentUser().getList("friends"));
        friendsAdapter.notifyDataSetChanged();*/
    }
}