package com.example.zapofood.adapters;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.zapofood.MainActivity;
import com.example.zapofood.R;
import com.example.zapofood.models.Reservation;
import com.example.zapofood.models.Restaurant;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.ViewHolder> {

    private Context context;
    private List<Restaurant> restaurants;
    // Define listener member variable
    private OnItemClickListener listener;

    public RestaurantsAdapter(Context context, List<Restaurant> posts){
        this.context = context;
        this.restaurants = posts;
    }

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.bind(restaurant);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void clear() {
        restaurants.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Restaurant> list) {
        restaurants.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemName;
        private ImageView ivItemImage;
        private View vItemPalette;
        final View rootView;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener clickListener) {
            super(itemView);
            rootView = itemView;
            tvItemName = itemView.findViewById(R.id.tvItemName);
            vItemPalette = itemView.findViewById(R.id.vItemPalette);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(itemView, getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    addFavorites();
                    return false;
                }
            });
        }

        public void bind(Restaurant restaurant) {
            // Bind the post data to the view elements
            tvItemName.setText(restaurant.getName());
            ParseFile image = restaurant.getImage();
            Glide.with(context).load(image.getUrl()).into(ivItemImage);
        }
        public void addFavorites(){
            AlertDialog.Builder dialogBuilder;
            AlertDialog dialog;
            dialogBuilder = new AlertDialog.Builder(itemView.getContext());
            final View addFavoritesView = LayoutInflater.from(context).inflate(R.layout.add_favorites, null);
            RelativeLayout containerAddToFavorites = addFavoritesView.findViewById(R.id.containerAddToFavorites);
            dialogBuilder.setView(addFavoritesView);
            dialog = dialogBuilder.create();
            dialog.show();
            containerAddToFavorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isInFavorites(restaurants.get(getAdapterPosition()))){
                        Toast.makeText(itemView.getContext(), "Restaurant already is in favorites", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else{
                        dialog.dismiss();
                        Toast.makeText(itemView.getContext(), "Restaurant saved in favorites", Toast.LENGTH_SHORT).show();
                        List<ParseObject> myFavorites = null;
                        try {
                            myFavorites = ParseUser.getCurrentUser().fetch().getList("favorites");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        myFavorites.add(restaurants.get(getAdapterPosition()));
                        ParseUser.getCurrentUser().put("favorites", myFavorites);
                        ParseUser.getCurrentUser().saveInBackground();
                    };
                }
            });
        }
        public boolean isInFavorites(Restaurant restaurant){
            List<ParseObject> myFavorites = null;
            try {
                myFavorites = ParseUser.getCurrentUser().fetch().getList("favorites");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for(ParseObject parseObject : myFavorites){
                try {
                    if(parseObject.fetch().getObjectId().equals(restaurant.getObjectId())){
                        return true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }
}