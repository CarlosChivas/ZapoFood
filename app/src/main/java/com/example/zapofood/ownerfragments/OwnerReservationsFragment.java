package com.example.zapofood.ownerfragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.zapofood.R;
import com.example.zapofood.adapters.OwnerReservationsAdapter;
import com.example.zapofood.adapters.ReservationsAdapter;
import com.example.zapofood.fragments.RestaurantDetailsFragment;
import com.example.zapofood.models.Reservation;
import com.example.zapofood.models.Restaurant;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class OwnerReservationsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final String TAG = "OwnerReservationsFragment";
    private List<Reservation> ownerReservations;
    private Restaurant restaurant;
    private RecyclerView rvOwnerReservations;
    private OwnerReservationsAdapter ownerReservationsAdapter;

    public static OwnerReservationsFragment newInstance(Restaurant restaurant) {
        OwnerReservationsFragment fragmentDemo = new OwnerReservationsFragment();
        Bundle args = new Bundle();
        args.putParcelable("restaurant", restaurant);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    public OwnerReservationsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static OwnerReservationsFragment newInstance(String param1, String param2) {
        OwnerReservationsFragment fragment = new OwnerReservationsFragment();
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
        return inflater.inflate(R.layout.fragment_owner_reservations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ownerReservations = new ArrayList<>();
        restaurant = getArguments().getParcelable("restaurant");
        rvOwnerReservations = view.findViewById(R.id.rvOwnerReservations);
        ownerReservationsAdapter = new OwnerReservationsAdapter(getContext(), ownerReservations);
        rvOwnerReservations.setAdapter(ownerReservationsAdapter);
        rvOwnerReservations.setLayoutManager(new LinearLayoutManager(getContext()));

        //We create a pop-up for the post details
        ownerReservationsAdapter.setOnItemClickListener(new OwnerReservationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {

            }
        });

        getOwnerReservations();
    }

    private void getOwnerReservations(){
        ParseQuery<Reservation> query = ParseQuery.getQuery(Reservation.class);
        query.whereEqualTo(Reservation.KEY_RESTAURANT, restaurant);
        query.setLimit(20);
        query.findInBackground(new FindCallback<Reservation>() {
            @Override
            public void done(List<Reservation> reservations, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                ownerReservations.clear();
                ownerReservations.addAll(reservations);
                ownerReservationsAdapter.notifyDataSetChanged();
            }
        });
    }
}