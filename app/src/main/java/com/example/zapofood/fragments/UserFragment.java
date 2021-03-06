package com.example.zapofood.fragments;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zapofood.LoginActivity;
import com.example.zapofood.MainActivity;
import com.example.zapofood.R;
import com.example.zapofood.adapters.FriendsAdapter;
import com.example.zapofood.models.Restaurant;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.parse.Parse;
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

    private ImageButton btnUserLogout;
    private ImageView ivImageProfile;
    private TextView tvNameProfile;
    private RecyclerView rvPreviewFriends;
    private FriendsAdapter friendsAdapter;
    private List<ParseObject> friends;
    private List<ParseObject> allFriends;
    private List<ParseObject> myRequestsSent;
    private ImageButton btnAddFriends;
    private TextView tvAmountFriends;
    private Button btnSeeAllFriends;
    private Button btnRequestsFriends;
    private ImageButton btnAnalytics;
    private ImageButton btnFavorites;

    public static UserFragment newInstance(ParseUser user, List<ParseObject> friends, List<ParseObject> allFriends) {
        UserFragment fragmentDemo = new UserFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        args.putParcelableArrayList("friends", (ArrayList<? extends Parcelable>) friends);
        args.putParcelableArrayList("allFriends", (ArrayList<? extends Parcelable>) allFriends);

        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        btnAddFriends = view.findViewById(R.id.btnAddFriends);
        tvAmountFriends = view.findViewById(R.id.tvAmountFriends);
        btnSeeAllFriends = view.findViewById(R.id.btnSeeAllFriends);
        btnRequestsFriends = view.findViewById(R.id.btnRequestsFriends);
        btnAnalytics = view.findViewById(R.id.btnAnalytics);
        btnFavorites = view.findViewById(R.id.btnFavorites);

        ParseFile image = currentUser.getParseFile("image");
        Glide.with(getContext()).load(image.getUrl()).circleCrop().into(ivImageProfile);
        tvNameProfile.setText(currentUser.getUsername());

        btnAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                //RestaurantDetailsFragment fragmentDemo = RestaurantDetailsFragment.newInstance(allRestaurants.get(position), userAddress);
                FriendsFragment fragmentDemo = FriendsFragment.newInstance(allFriends, myRequestsSent, false);
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContainer, fragmentDemo).commit();
            }
        });

        friends = new ArrayList<>();
        allFriends = new ArrayList<>();
        myRequestsSent = new ArrayList<>();
        friendsAdapter = new FriendsAdapter(getContext(), friends);
        rvPreviewFriends.setHasFixedSize(true);
        rvPreviewFriends.setAdapter(friendsAdapter);
        rvPreviewFriends.setLayoutManager(new GridLayoutManager(getContext(), 3));

        friendsAdapter.setOnItemClickListener(new FriendsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                FragmentManager fragmentManager = getParentFragmentManager();
                ProfileFragment fragment = ProfileFragment.newInstance(friends.get(position), allFriends, myRequestsSent, position);
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContainer, fragment).commit();
            }
        });
        btnSeeAllFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FriendsFragment fragmentDemo = FriendsFragment.newInstance(allFriends, myRequestsSent, true);
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContainer, fragmentDemo).commit();
            }
        });

        btnRequestsFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                //RequestsFragment fragmentDemo = RequestsFragment.newInstance(currentUser.getList("requests"));
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContainer, new RequestsFragment()).commit();
            }
        });

        btnAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                //RequestsFragment fragmentDemo = RequestsFragment.newInstance(currentUser.getList("requests"));
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContainer, new AnalyticsFragment()).commit();
            }
        });

        btnFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                //RequestsFragment fragmentDemo = RequestsFragment.newInstance(currentUser.getList("requests"));
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContainer, new FavoritesFragment()).commit();
            }
        });

        btnUserLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                getFriends();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        friendsAdapter.notifyDataSetChanged();
                        tvAmountFriends.setText(allFriends.size() + " friends");
                    }
                });
            }
        }).start();
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
        allFriends = ParseUser.getCurrentUser().getList("friends");

        for(int i =0; i<allFriends.size(); i++){
            if(i>=3){
                break;
            }
            try {
                friends.add(allFriends.get(i).fetch());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        try {
            myRequestsSent = ParseUser.getCurrentUser().fetch().getList("sentRequests");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (int i = 0; i<myRequestsSent.size(); i++){
            try {
                myRequestsSent.get(i).fetch();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}