package com.example.zapofood.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.zapofood.R;
import com.example.zapofood.models.Restaurant;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {

    private Context context;
    private List<ParseObject> requests;
    // Define listener member variable
    private OnItemClickListener listener;

    public RequestsAdapter(Context context, List<ParseObject> requests){
        this.context = context;
        this.requests = requests;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseObject request = requests.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public void clear() {
        requests.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<ParseUser> list) {
        requests.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNameRequest;
        private ImageView ivImageRequest;
        private Button btnRejectRequest;
        private Button btnAcceptRequest;
        private TextView tvRequestChanged;

        final View rootView;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener clickListener) {
            super(itemView);
            rootView = itemView;
            tvNameRequest = itemView.findViewById(R.id.tvNameRequest);
            ivImageRequest = itemView.findViewById(R.id.ivImageRequest);
            btnAcceptRequest = itemView.findViewById(R.id.btnAcceptRequest);
            btnRejectRequest = itemView.findViewById(R.id.btnRejectRequest);
            tvRequestChanged = itemView.findViewById(R.id.tvRequestChanged);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(itemView, getAdapterPosition());
                }
            });
        }

        public void bind(ParseObject request) {
            // Bind the post data to the view elements
            tvNameRequest.setText(request.getString("username"));
            ParseFile image = request.getParseFile("image");
            Glide.with(context).load(image.getUrl()).into(ivImageRequest);
            btnRejectRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<ParseObject> requestsUpdate = ParseUser.getCurrentUser().getList("requests");
                    requestsUpdate.remove(getAdapterPosition());
                    ParseUser newUser = ParseUser.getCurrentUser();
                    newUser.put("requests", requestsUpdate);
                    newUser.saveInBackground();
                    updateRequestUI("Request rejected");
                }
            });
            btnAcceptRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<ParseObject> requestsUpdate = ParseUser.getCurrentUser().getList("requests");
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    try {
                        params.put("objectId", requestsUpdate.get(getAdapterPosition()).fetch().getObjectId());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    params.put("newObjectId", ParseUser.getCurrentUser().getObjectId());
                    ParseCloud.callFunctionInBackground("addFriend", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e != null){
                                Log.i("Friends", e.toString());
                            }
                        }
                    });

                    List<ParseObject> friendsUpdate = ParseUser.getCurrentUser().getList("friends");
                    friendsUpdate.add(request);
                    ParseUser newUser = ParseUser.getCurrentUser();
                    requestsUpdate.remove(getAdapterPosition());
                    newUser.put("requests", requestsUpdate);
                    newUser.put("friends", friendsUpdate);
                    newUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null){
                                Toast.makeText(context.getApplicationContext(), "Error saving new friend: " + e, Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(context.getApplicationContext(), "New friend added", Toast.LENGTH_SHORT).show();
                                updateRequestUI("Now " + request.getString("username") + " and you are friends");
                            }
                        }
                    });
                }
            });
        }

        public void updateRequestUI(String text){
            btnRejectRequest.setVisibility(View.GONE);
            btnAcceptRequest.setVisibility(View.GONE);
            tvRequestChanged.setVisibility(View.VISIBLE);
            tvRequestChanged.setText(text);
        }
    }
}
