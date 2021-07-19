package com.example.zapofood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zapofood.R;
import com.example.zapofood.adapters.ReservationsAdapter;
import com.example.zapofood.adapters.RestaurantsAdapter;
import com.example.zapofood.models.Reservation;
import com.example.zapofood.models.Restaurant;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyReservationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyReservationsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final String TAG = "MyReservationsFragment";
    private List<Reservation> myReservations;
    private RecyclerView rvMyReservations;
    private ReservationsAdapter reservationsAdapter;

    public MyReservationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyReservationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyReservationsFragment newInstance(String param1, String param2) {
        MyReservationsFragment fragment = new MyReservationsFragment();
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
        return inflater.inflate(R.layout.fragment_my_reservations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myReservations = new ArrayList<>();

        rvMyReservations = view.findViewById(R.id.rvMyReservations);
        reservationsAdapter = new ReservationsAdapter(getContext(), myReservations);
        // allows for optimizations
        //rvRestaurants.setHasFixedSize(true);
        rvMyReservations.setAdapter(reservationsAdapter);
        // Define 2 column grid layout
        rvMyReservations.setLayoutManager(new LinearLayoutManager(getContext()));
        //rvRestaurants.setLayoutManager(new GridLayoutManager(getContext(), 2));
        //We create a pop-up for the post details
        reservationsAdapter.setOnItemClickListener(new ReservationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {

            }
        });
        getMyReservations();
    }

    private void getMyReservations(){
        ParseQuery<Reservation> query = ParseQuery.getQuery(Reservation.class);
        query.whereEqualTo(Reservation.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20);
        query.findInBackground(new FindCallback<Reservation>() {
            @Override
            public void done(List<Reservation> reservations, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                myReservations.clear();
                myReservations.addAll(reservations);
                reservationsAdapter.notifyDataSetChanged();
            }
        });
    }
}