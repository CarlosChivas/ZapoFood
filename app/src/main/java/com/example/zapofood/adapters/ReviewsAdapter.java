package com.example.zapofood.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zapofood.R;
import com.example.zapofood.models.Review;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder>{
    Context context;
    List<Review> reviews;

    public ReviewsAdapter(Context context, List<Review> reviews){
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapter.ViewHolder holder, int position) {
        Review review = reviews.get(position);
        try {
            holder.bind(review);
        } catch (ParseException e) {
            Log.i("Review", "Error with the review");
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivUserImage;
        private TextView tvUserName;
        private TextView tvReviewText;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            ivUserImage = itemView.findViewById(R.id.ivUserImage);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvReviewText = itemView.findViewById(R.id.tvReviewText);
        }

        public void bind(Review review) throws ParseException {
            ParseUser user = review.getUser().fetchIfNeeded();
            ParseFile image = user.getParseFile("image");
            Glide.with(context).load(image.getUrl()).into(ivUserImage);
            tvUserName.setText(review.getUser().getUsername());
            tvReviewText.setText(review.getText());
        }
    }
}
