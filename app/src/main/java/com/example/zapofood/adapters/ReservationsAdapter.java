package com.example.zapofood.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zapofood.R;
import com.example.zapofood.models.Reservation;
import com.example.zapofood.models.Restaurant;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ViewHolder>{


    private Context context;
    private List<Reservation> reservations;
    // Define listener member variable
    private OnItemClickListener listenerReservation;

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
        Log.i("Dates", "Dates: "+calendar.getTime().toString());
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
}
}
