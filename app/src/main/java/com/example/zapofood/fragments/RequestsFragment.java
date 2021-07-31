package com.example.zapofood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.zapofood.R;
import com.example.zapofood.adapters.RequestsAdapter;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class RequestsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageButton btnBackUser;
    private RecyclerView rvRequests;
    private RequestsAdapter requestsAdapter;
    private List<ParseObject> requests;

    public static RequestsFragment newInstance(List<ParseObject> requests) {
        RequestsFragment fragment = new RequestsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("requests", (ArrayList<? extends Parcelable>) requests);
        fragment.setArguments(args);
        return fragment;
    }
    public RequestsFragment() {
        // Required empty public constructor
    }

    public static RequestsFragment newInstance(String param1, String param2) {
        RequestsFragment fragment = new RequestsFragment();
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
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnBackUser = view.findViewById(R.id.btnBackUser);
        rvRequests = view.findViewById(R.id.rvRequests);
        requests = new ArrayList<>();
        requestsAdapter = new RequestsAdapter(getContext(), requests);
        rvRequests.setAdapter(requestsAdapter);
        rvRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        btnBackUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                getRequests();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        requestsAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
    public void getRequests(){
        List<ParseObject> requestsF =  getArguments().getParcelableArrayList("requests");
        for(ParseObject parseObject : requestsF){
            try {
                requests.add(parseObject.fetch());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}