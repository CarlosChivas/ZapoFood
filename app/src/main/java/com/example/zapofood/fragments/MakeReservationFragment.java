package com.example.zapofood.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zapofood.R;
import com.example.zapofood.adapters.FriendsAdapter;
import com.example.zapofood.models.Reservation;
import com.example.zapofood.models.Restaurant;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MakeReservationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final String TAG = "MakeReservationFragment";
    private TextView tvSelectDate;
    private ImageButton ibSelectDate;
    private ImageButton btnCancelReservation;
    private Button btnReservationDone;
    private Date reservationDate;
    private TextView tvSelectTime;
    private ImageButton ibSelectTime;
    private int hourDate, minuteDate;
    private ImageView ivImageMakeReservation;
    private TextView tvNameMakeReservation;
    private Button btnInviteFriends;
    private RecyclerView rvFriendsInvited;
    private RecyclerView rvInviteFriends;
    private FriendsAdapter friendsInvitedAdapter;
    private FriendsAdapter friendsAdapter;
    private List<ParseObject> myFriends;
    private List<ParseObject> friendsInvited;
    Restaurant restaurant;

    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;

    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);

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
        tvSelectDate = view.findViewById(R.id.tvSelectDate);
        ibSelectDate = view.findViewById(R.id.btnSelectDate);
        btnCancelReservation = view.findViewById(R.id.btnCancelReservation);
        btnReservationDone = view.findViewById(R.id.btnReservationDone);
        tvSelectTime = view.findViewById(R.id.tvTime);
        ibSelectTime = view.findViewById(R.id.btnSelectTime);
        ivImageMakeReservation = view.findViewById(R.id.ivImageMakeReservation);
        tvNameMakeReservation = view.findViewById(R.id.tvNameMakeReservation);
        btnInviteFriends = view.findViewById(R.id.btnInviteFriends);

        friendsInvited = new ArrayList<>();
        rvFriendsInvited = view.findViewById(R.id.rvFriendsInvited);
        friendsInvitedAdapter = new FriendsAdapter(getContext(), friendsInvited);
        rvFriendsInvited.setAdapter(friendsInvitedAdapter);

        LinearLayoutManager mn = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                // force height of viewHolder here, this will override layout_height from xml
                lp.width = getWidth() / 4;
                return true;
            }
        };

        rvFriendsInvited.setLayoutManager(mn);
        friendsInvitedAdapter.setOnItemClickListener(new FriendsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                friendsInvited.remove(position);
                friendsInvitedAdapter.notifyItemRemoved(position);
            }
        });

        restaurant = getArguments().getParcelable("restaurant");

        ParseFile image = restaurant.getImage();
        Glide.with(getContext()).load(image.getUrl()).into(ivImageMakeReservation);
        tvNameMakeReservation.setText(restaurant.getName());

        btnInviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteFriends();
            }
        });

        ibSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                hourDate = hourOfDay;
                                minuteDate = minute;
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0,0,0, hourDate, minuteDate);
                                tvSelectTime.setText(android.text.format.DateFormat.format("hh:mm aa", calendar));
                            }
                        }, 12, 0, false
                );
                //Displayed previous selected time
                timePickerDialog.updateTime(hourDate, minuteDate);
                //Show dialog
                timePickerDialog.show();
            }
        });
        btnCancelReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        ibSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int newYear, int newMonth, int newDayOfMonth) {
                        year = newYear;
                        month = newMonth;
                        day = newDayOfMonth;
                        tvSelectDate.setText(newDayOfMonth+"/"+(newMonth+1)+"/"+newYear);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        btnReservationDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reservation reservation = new Reservation();
                reservation.setRestaurant(restaurant);
                reservation.setUser(ParseUser.getCurrentUser());
                calendar.set(year, month, day, hourDate, minuteDate);
                reservation.setDate(calendar.getTime());
                reservation.setPersons(friendsInvited);
                reservation.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null){
                            Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                            Log.i("Saving", "Error: " + e.toString());
                            return;
                        }
                        Toast.makeText(getContext(), "Reservation save was successful!!", Toast.LENGTH_SHORT).show();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        fragmentManager.popBackStack();
                    }
                });
                String date = new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());
                int month = Integer.parseInt(date);
                List<Integer> historyReservations = new ArrayList<>();
                try {
                    historyReservations = ParseUser.getCurrentUser().fetch().getList("historyReservations");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                historyReservations.set(month-1, historyReservations.get(month-1)+1);
                int amount = (int) ParseUser.getCurrentUser().getNumber("reservations");
                List<ParseObject> restaurants = ParseUser.getCurrentUser().getList("historyRestaurant");
                restaurants.add(restaurant);
                ParseUser.getCurrentUser().put("reservations", amount+1);
                ParseUser.getCurrentUser().put("historyRestaurant", restaurants);
                ParseUser.getCurrentUser().put("historyReservations", historyReservations);
                ParseUser.getCurrentUser().saveInBackground();
            }
        });
    }

    public void inviteFriends(){
        dialogBuilder = new AlertDialog.Builder(getContext());
        final View inviteFriendsView = LayoutInflater.from(getContext()).inflate(R.layout.invite_friend, null);
        rvInviteFriends = inviteFriendsView.findViewById(R.id.rvFriends);
        myFriends = new ArrayList<>();
        friendsAdapter = new FriendsAdapter(getContext(), myFriends);
        rvInviteFriends.setHasFixedSize(true);
        rvInviteFriends.setAdapter(friendsAdapter);
        rvInviteFriends.setLayoutManager(new GridLayoutManager(getContext(), 3));
        friendsAdapter.setOnItemClickListener(new FriendsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                if(friendsInvited.contains(myFriends.get(position))){
                    Toast.makeText(getContext(), "This user already was invited", Toast.LENGTH_SHORT).show();
                 }
                else{
                    Toast.makeText(getContext(), "Invited", Toast.LENGTH_SHORT).show();
                    friendsInvited.add(myFriends.get(position));
                    friendsInvitedAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });

        dialogBuilder.setView(inviteFriendsView);
        dialog = dialogBuilder.create();
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ParseObject> bFriends = ParseUser.getCurrentUser().getList("friends");
                for(ParseObject parseObject : bFriends){
                    try {
                        myFriends.add(parseObject.fetch());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        friendsAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
}