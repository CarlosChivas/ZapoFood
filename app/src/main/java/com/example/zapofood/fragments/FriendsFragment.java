package com.example.zapofood.fragments;

import android.location.Address;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.zapofood.MainActivity;
import com.example.zapofood.R;
import com.example.zapofood.adapters.FriendsAdapter;
import com.example.zapofood.models.Restaurant;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView rvFriends;
    private FriendsAdapter friendsAdapter;
    private List<ParseObject> allUsers;
    private ImageButton btnBackUser;


    public static FriendsFragment newInstance(List<ParseObject> myFriends) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("myFriends", (ArrayList<? extends Parcelable>) myFriends);
        fragment.setArguments(args);
        return fragment;
    }

    public FriendsFragment() {
        // Required empty public constructor
    }

    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
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
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_friends, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.tbSearchFriends);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        btnBackUser = view.findViewById(R.id.btnBackUser);
        rvFriends = view.findViewById(R.id.rvFriends);
        allUsers = new ArrayList<>();
        friendsAdapter = new FriendsAdapter(getContext(), allUsers);
        rvFriends.setHasFixedSize(true);
        rvFriends.setAdapter(friendsAdapter);
        rvFriends.setLayoutManager(new GridLayoutManager(getContext(), 3));

        friendsAdapter.setOnItemClickListener(new FriendsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                FragmentManager fragmentManager = getParentFragmentManager();
                ProfileFragment fragment = ProfileFragment.newInstance(allUsers.get(position));
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContainer, fragment).commit();
            }
        });
        if(getArguments().getParcelableArrayList("myFriends")!=null){
            allUsers.addAll(getArguments().getParcelableArrayList("myFriends"));
            friendsAdapter.notifyDataSetChanged();
        }
        btnBackUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_friends, menu);
        super.onCreateOptionsMenu(menu,inflater);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchUsers(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetchUsers(newText);
                return false;
            }
        });
    }

    private void fetchUsers(String querySearch){
        if(querySearch.isEmpty()){
            return;
        }
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereMatches("username", "("+querySearch+")", "i");
        query.setLimit(15);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e("Friends", "Issue with getting posts", e);
                    return;
                }
                allUsers.clear();
                for (ParseUser user : users){
                    try {
                        allUsers.add(user.fetch());
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }
                }
                friendsAdapter.notifyDataSetChanged();
            }
        });
    }
}