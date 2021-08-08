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
import android.widget.Toast;

import com.example.zapofood.R;
import com.example.zapofood.adapters.ReservationsAdapter;
import com.example.zapofood.adapters.RestaurantsAdapter;
import com.example.zapofood.models.Reservation;
import com.example.zapofood.models.Restaurant;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MyReservationsFragment extends Fragment {

    public static final String TAG = "MyReservationsFragment";
    private List<Reservation> myReservations;
    private RecyclerView rvMyReservations;
    private ReservationsAdapter reservationsAdapter;

    public MyReservationsFragment() {
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
        return inflater.inflate(R.layout.fragment_my_reservations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myReservations = new ArrayList<>();

        rvMyReservations = view.findViewById(R.id.rvMyReservations);
        reservationsAdapter = new ReservationsAdapter(getContext(), myReservations);
        rvMyReservations.setAdapter(reservationsAdapter);
        rvMyReservations.setLayoutManager(new LinearLayoutManager(getContext()));

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

        //Find the reservations in where the user was invited
        ParseQuery<Reservation> query2 = ParseQuery.getQuery(Reservation.class);
        query2.whereContains("persons.objectId", ParseUser.getCurrentUser().getObjectId());

        List<ParseQuery<Reservation>> queries = new ArrayList<ParseQuery<Reservation>>();
        queries.add(query);
        queries.add(query2);
        ParseQuery<Reservation> mainQuery = ParseQuery.or(queries);
        mainQuery.findInBackground((myReservationsNew, e) -> {
            if(e == null){
                myReservations.clear();
                myReservations.addAll(myReservationsNew);
                reservationsAdapter.notifyDataSetChanged();
            }else{
                Toast.makeText(getContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}