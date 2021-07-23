package com.example.zapofood.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.zapofood.R;
import com.example.zapofood.models.Reservation;
import com.example.zapofood.models.Restaurant;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Headers;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ViewHolder>{


    private Context context;
    private List<Reservation> reservations;
    // Define listener member variable
    private OnItemClickListener listenerReservation;

    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;

    public ReservationsAdapter(Context context, List<Reservation> reservations){
        this.context = context;
        this.reservations = reservations;
    }

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
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
        final View rootView;

    public ViewHolder(@NonNull View itemView, final OnItemClickListener clickListener) {
        super(itemView);
        rootView = itemView;
        ivReservationImage = itemView.findViewById(R.id.ivReservationImage);
        tvReservationTitle = itemView.findViewById(R.id.tvReservationTitle);
        tvReservationDate = itemView.findViewById(R.id.tvReservationDate);
        tvReservationTime = itemView.findViewById(R.id.tvReservationTime);
        ibReservationDelete = itemView.findViewById(R.id.ibReservationDelete);

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
        tvReservationDate.setText(month(calendar.get(Calendar.MONTH)) + " " + calendar.get(Calendar.DAY_OF_MONTH));
        tvReservationTime.setText(" - " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
        ibReservationDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeleteReservation(reservation);
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
}
}
