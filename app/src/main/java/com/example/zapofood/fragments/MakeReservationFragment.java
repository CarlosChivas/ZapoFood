package com.example.zapofood.fragments;

import android.app.DatePickerDialog;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.zapofood.R;
import com.example.zapofood.models.Reservation;
import com.example.zapofood.models.Restaurant;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MakeReservationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final String TAG = "MakeReservationFragment";
    private EditText etSelectDate;
    private ImageButton btnCancelReservation;
    private Button btnReservationDone;
    private Date reservationDate;

    public static MakeReservationFragment newInstance(Restaurant restaurant) {
        MakeReservationFragment fragmentDemo = new MakeReservationFragment();
        Bundle args = new Bundle();
        args.putParcelable("restaurant", restaurant);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    public MakeReservationFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MakeReservationFragment newInstance(String param1, String param2) {
        MakeReservationFragment fragment = new MakeReservationFragment();
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
        return inflater.inflate(R.layout.fragment_make_reservation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etSelectDate = view.findViewById(R.id.etSelectDate);
        btnCancelReservation = view.findViewById(R.id.btnCancelReservation);
        btnReservationDone = view.findViewById(R.id.btnReservationDone);

        btnCancelReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        etSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int newYear, int newMonth, int newDayOfMonth) {
                        calendar.set(newYear, newMonth, newDayOfMonth, 0, 0);
                        reservationDate = calendar.getTime();
                        etSelectDate.setText(newDayOfMonth+"/"+(newMonth+1)+"/"+newYear);
                        Toast.makeText(getContext(), "Calendar: "+ calendar.getTime().toString(), Toast.LENGTH_SHORT).show();
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        btnReservationDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Restaurant restaurant = getArguments().getParcelable("restaurant");
                Reservation reservation = new Reservation();
                reservation.setRestaurant(restaurant);
                reservation.setUser(ParseUser.getCurrentUser());
                reservation.setDate(reservationDate);
                reservation.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null){
                            Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                            Log.i("Saving", "Error: " + e.toString());
                            return;
                        }
                        Toast.makeText(getContext(), "Reservation save was successful!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}