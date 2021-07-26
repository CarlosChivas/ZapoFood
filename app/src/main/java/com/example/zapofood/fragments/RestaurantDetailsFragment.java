package com.example.zapofood.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zapofood.R;
import com.example.zapofood.adapters.ReviewsAdapter;
import com.example.zapofood.models.Reservation;
import com.example.zapofood.models.Restaurant;
import com.example.zapofood.models.Review;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RestaurantDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView tvTitleRestaurant;
    private ImageButton btnBackHome;
    private ImageView ivRestaurantImage;
    RatingBar rbVoteAverage;
    private TextView tvDescriptionRestaurant;
    private TextView tvAddressRestaurant;
    private Button btnMakeReservation;
    private ImageButton ibShowLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private List<Review> reviews;
    private RecyclerView rvReviews;
    private ReviewsAdapter reviewsAdapter;
    private Restaurant restaurant;
    private ImageButton btnSendReview;
    private EditText etTextReview;
    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;

    public static RestaurantDetailsFragment newInstance(Restaurant restaurant, Address address) {
        RestaurantDetailsFragment fragmentDemo = new RestaurantDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("restaurant", restaurant);
        args.putParcelable("address", address);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    public RestaurantDetailsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RestaurantDetailsFragment newInstance(String param1, String param2) {
        RestaurantDetailsFragment fragment = new RestaurantDetailsFragment();
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
        return inflater.inflate(R.layout.fragment_restaurant_details, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        restaurant = getArguments().getParcelable("restaurant");
        tvTitleRestaurant = view.findViewById(R.id.tvTitlteRestaurant);
        btnBackHome = view.findViewById(R.id.btnBackHome);
        btnMakeReservation = view.findViewById(R.id.btnMakeReservation);
        ivRestaurantImage = view.findViewById(R.id.ivRestaurantImage);
        rbVoteAverage = view.findViewById(R.id.rbVoteAverage);
        tvDescriptionRestaurant = view.findViewById(R.id.tvDescriptionRestaurant);
        tvAddressRestaurant = view.findViewById(R.id.tvAddressRestaurant);
        ibShowLocation = view.findViewById(R.id.ibShowLocation);
        rvReviews = view.findViewById(R.id.rvReviews);
        etTextReview = view.findViewById(R.id.etTextReview);
        btnSendReview = view.findViewById(R.id.btnSendReview);

        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        });
        tvTitleRestaurant.setText(restaurant.getName());
        btnMakeReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                MakeReservationFragment fragmentDemo = MakeReservationFragment.newInstance(restaurant);
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContainer, fragmentDemo).commit();
            }
        });

        ibShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getLocation();
                Address userAddress = getArguments().getParcelable("address");
                Uri uri = Uri.parse("https://www.google.co.in/maps/dir/"+ restaurant.getAddress() + "/"+userAddress.getAddressLine(0));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.google.android.apps.maps");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        ParseFile image = restaurant.getImage();
        Glide.with(getContext()).load(image.getUrl()).into(ivRestaurantImage);
        rbVoteAverage.setRating((float) restaurant.getScore());
        tvDescriptionRestaurant.setText(restaurant.getDescription());
        tvAddressRestaurant.setText(restaurant.getAddress());

        reviews = new ArrayList<>();
        reviewsAdapter = new ReviewsAdapter(getContext(), reviews);
        rvReviews.setAdapter(reviewsAdapter);
        rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        getReviews();

        btnSendReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textReview = etTextReview.getText().toString();
                if(textReview.isEmpty()){
                    Toast.makeText(getContext(), "Review is empty", Toast.LENGTH_SHORT).show();
                }else {
                    confirmSendReview(textReview);
                }
            }
        });
    }

    public void getLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if(ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if(location != null){
                        try {
                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                            //Initialize address list
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            Toast.makeText(getContext(), "Address: " + addresses.get(0).getLocality()/*getAddressLine(0)*/, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    public void getReviews(){
        ParseQuery<Review> query = ParseQuery.getQuery(Review.class);
        query.whereEqualTo(Review.KEY_RESTAURANT, restaurant);
        query.setLimit(10);
        query.findInBackground(new FindCallback<Review>() {
            @Override
            public void done(List<Review> objects, ParseException e) {
                if (e != null) {
                    return;
                }
                Log.i("Test", "Size reservations: " + objects.size());
                reviews.clear();
                reviews.addAll(objects);
                reviewsAdapter.notifyDataSetChanged();
            }
        });
    }

    public void sendReview(String textReview){
        Review review = new Review();
        review.setRestaurant(restaurant);
        review.setUser(ParseUser.getCurrentUser());
        review.setText(textReview);
        review.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Toast.makeText(getContext(), "Error saving the review", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getContext(), "Review save was successful", Toast.LENGTH_SHORT).show();
                reviews.add(review);
                reviewsAdapter.notifyItemInserted(reviews.size()-1);
                etTextReview.setText("");
            }
        });
    }

    public void confirmSendReview(String textReview) {
        dialogBuilder = new AlertDialog.Builder(getContext());
        final View deleteReservationView = LayoutInflater.from(getContext()).inflate(R.layout.confirm_review, null);
        Button btnConfirmReview;
        Button btnCancelReview;

        btnConfirmReview = deleteReservationView.findViewById(R.id.btnConfirmReview);
        btnCancelReview = deleteReservationView.findViewById(R.id.btnCancelReview);

        dialogBuilder.setView(deleteReservationView);
        dialog = dialogBuilder.create();
        dialog.show();

        btnConfirmReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReview(textReview);
                dialog.dismiss();
            }
        });
        btnCancelReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}