package com.example.zapofood.adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.zapofood.R;
import com.example.zapofood.models.Reservation;
import com.example.zapofood.models.Restaurant;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ViewHolder>{


    private Context context;
    private List<Reservation> reservations;
    // Define listener member variable
    private OnItemClickListener listenerReservation;

    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;
    private FusedLocationProviderClient fusedLocationProviderClient;


    public ReservationsAdapter(Context context, List<Reservation> reservations){
        this.context = context;
        this.reservations = reservations;
    }

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    public interface Rute{
        void showRute(String address);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listenerReservation = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reservation, parent, false);
        return new ViewHolder(view, this.listenerReservation);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationsAdapter.ViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        try {
            holder.bind(reservation);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivReservationImage;
        private TextView tvReservationTitle;
        private TextView tvReservationDate;
        private TextView tvReservationTime;
        private ImageButton ibReservationDelete;
        private ImageButton btnShowRute;
        private ImageButton btnShowAssistants;
        private Address address;
        private TextView tvInvitedBy;
        final View rootView;

    public ViewHolder(@NonNull View itemView, final OnItemClickListener clickListener) {
        super(itemView);
        rootView = itemView;
        ivReservationImage = itemView.findViewById(R.id.ivReservationImage);
        tvReservationTitle = itemView.findViewById(R.id.tvReservationTitle);
        tvReservationDate = itemView.findViewById(R.id.tvReservationDate);
        tvReservationTime = itemView.findViewById(R.id.tvReservationTime);
        ibReservationDelete = itemView.findViewById(R.id.ibReservationDelete);
        btnShowRute = itemView.findViewById(R.id.btnShowRute);
        btnShowAssistants = itemView.findViewById(R.id.btnShowAssistants);
        tvInvitedBy = itemView.findViewById(R.id.tvInvitedBy);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(itemView, getAdapterPosition());
            }
        });
    }

    public void bind(Reservation reservation) throws ParseException {
        // Bind the post data to the view elements
        Restaurant restaurant = reservation.getRestaurant().fetchIfNeeded();
        tvReservationTitle.setText(restaurant.getName());
        Glide.with(context).load(restaurant.getImage().getUrl()).into(ivReservationImage);
        Date dateReservation = reservation.getDateReservation();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateReservation);
        String curTime = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        tvReservationDate.setText(month(calendar.get(Calendar.MONTH)) + " " + calendar.get(Calendar.DAY_OF_MONTH));
        tvReservationTime.setText(" - " + curTime);
        if(!reservation.getUser().fetch().getUsername().equals(ParseUser.getCurrentUser().getUsername())){
            tvInvitedBy.setVisibility(View.VISIBLE);
            tvInvitedBy.setText("Invited by "+reservation.getUser().fetch().getUsername());
            ibReservationDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDeleteInvitation(reservation);
                }
            });
        }else{
            ibReservationDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDeleteReservation(reservation);
                }
            });
        }
        if(reservation.getList("persons").size()>0) {
            btnShowAssistants.setVisibility(View.VISIBLE);
            btnShowAssistants.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAssistants(reservation);
                }
            });
        }
        btnShowRute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation(restaurant);
            }
        });

    }

    private String month(int num){
        switch (num){
            case 0:
                return "January";
            case 1:
                return "February";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "August";
            case 8:
                return "September";
            case 9:
                return "October";
            case 10:
                return "November";
            default:
                return "December";
        }
    }

    public void confirmDeleteInvitation(Reservation reservation){
        dialogBuilder = new AlertDialog.Builder(itemView.getContext());
        final View deleteReservationView = LayoutInflater.from(context).inflate(R.layout.confirm_delete, null);
        Button btnConfirmDeletion;
        Button btnCancelDeletion;

        btnConfirmDeletion = deleteReservationView.findViewById(R.id.btnConfirmDeletion);
        btnCancelDeletion = deleteReservationView.findViewById(R.id.btnCancelDeletion);

        dialogBuilder.setView(deleteReservationView);
        dialog = dialogBuilder.create();
        dialog.show();

        btnConfirmDeletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteInvitation(reservation);
                dialog.dismiss();
            }
        });
        btnCancelDeletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    public void confirmDeleteReservation(Reservation reservation){
        dialogBuilder = new AlertDialog.Builder(itemView.getContext());
        final View deleteReservationView = LayoutInflater.from(context).inflate(R.layout.confirm_delete, null);
        Button btnConfirmDeletion;
        Button btnCancelDeletion;

        btnConfirmDeletion = deleteReservationView.findViewById(R.id.btnConfirmDeletion);
        btnCancelDeletion = deleteReservationView.findViewById(R.id.btnCancelDeletion);

        dialogBuilder.setView(deleteReservationView);
        dialog = dialogBuilder.create();
        dialog.show();

        btnConfirmDeletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteObject(reservation);
                int position = getAdapterPosition();
                reservations.remove(position);
                notifyItemRemoved(position);
                dialog.dismiss();
            }
        });
        btnCancelDeletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    public void deleteObject(Reservation reservation) {
        ParseQuery<Reservation> query = ParseQuery.getQuery(Reservation.class);
        // Retrieve the object by id
        query.getInBackground(reservation.getObjectId(), (object, e) -> {
            if (e == null) {
                //Object was fetched
                //Deletes the fetched ParseObject from the database
                object.deleteInBackground(e2 -> {
                    if(e2==null){
                        Toast.makeText(itemView.getContext(), "Delete Successful", Toast.LENGTH_SHORT).show();
                    }else{
                        //Something went wrong while deleting the Object
                        Toast.makeText(itemView.getContext(), "Error: "+e2.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                //Something went wrong
                Toast.makeText(itemView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void deleteInvitation(Reservation reservation){
        List<ParseObject> assistants = reservation.getList("persons");
        int position=-1;
        for (ParseObject parseObject : assistants){
            position++;
            if(parseObject.equals(ParseUser.getCurrentUser())){
                break;
            }
        }
        Log.i("Reservations", "Size: " + reservation.getList("persons").size());
        Log.i("Reservations", "Size: " + assistants.size() + " position: "+position);
        assistants.remove(position);
        Log.i("Reservations", "Size: " + assistants.size());
        reservation.put("persons", assistants);
        reservation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Toast.makeText(context.getApplicationContext(), "Error deleting", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context.getApplicationContext(), "Todo cool", Toast.LENGTH_SHORT).show();
                }
            }
        });
        reservations.remove(reservation);
        notifyItemRemoved(getAdapterPosition());
    }

    public void getLocation(Restaurant restaurant){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if(ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if(location != null){
                        try {
                            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                            //Initialize address list
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            //address = addresses.get(0);
                            Toast.makeText(context, "Address: " + addresses.get(0).getLocality()/*getAddressLine(0)*/, Toast.LENGTH_SHORT).show();
                            showRute(addresses.get(0), restaurant);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }else {
            Toast.makeText(context, "We need to access your location, please give us your permission in the settings phone", Toast.LENGTH_SHORT).show();
        }
    }

    private void showRute(Address address, Restaurant restaurant){
        Uri uri = Uri.parse("https://www.google.co.in/maps/dir/"+ restaurant.getAddress() + "/"+address.getAddressLine(0));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void showAssistants(Reservation reservation){
        dialogBuilder = new AlertDialog.Builder(itemView.getContext());
        final View assistantsView = LayoutInflater.from(context).inflate(R.layout.invite_friend, null);
        RecyclerView rvAssistants = assistantsView.findViewById(R.id.rvFriends);
        List<ParseObject> assistants = new ArrayList<>();
        FriendsAdapter friendsAdapter = new FriendsAdapter(assistantsView.getContext(), assistants);
        rvAssistants.setHasFixedSize(true);
        rvAssistants.setAdapter(friendsAdapter);
        rvAssistants.setLayoutManager(new GridLayoutManager(assistantsView.getContext(), 3));
        friendsAdapter.setOnItemClickListener(new FriendsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {

            }
        });

        dialogBuilder.setView(assistantsView);
        dialog = dialogBuilder.create();
        dialog.show();

        List<ParseObject> parseObjectsList = reservation.getList("persons");
        parseObjectsList.add(reservation.getUser());
        for(ParseObject parseObject : parseObjectsList){
            try {
                assistants.add(parseObject.fetch());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        friendsAdapter.notifyDataSetChanged();
    }
    }
}
