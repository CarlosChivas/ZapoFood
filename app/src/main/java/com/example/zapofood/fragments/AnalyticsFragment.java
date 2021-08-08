package com.example.zapofood.fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.zapofood.R;
import com.example.zapofood.models.Reservation;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyticsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // in this example, a LineChart is initialized from xml
    private LineChart chart;
    private BarChart barChart;
    private PieChart pieChart;
    private ImageButton btnBackUser;
    private TextView tvAmountReservationsMade;
    private int amount = 0;

    public AnalyticsFragment() {
        // Required empty public constructor
    }
    public static AnalyticsFragment newInstance(String param1, String param2) {
        AnalyticsFragment fragment = new AnalyticsFragment();
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
        return inflater.inflate(R.layout.fragment_analytics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setTitle("Please, wait a moment.");
        dialog.setMessage("Logging in...");
        dialog.show();
        btnBackUser = view.findViewById(R.id.btnBackUser);
        chart = view.findViewById(R.id.chart);
        barChart = view.findViewById(R.id.barChart);
        pieChart = view.findViewById(R.id.pieChart);
        tvAmountReservationsMade = view.findViewById(R.id.tvAmountReservationsMade);

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
                amountReservations();
                pieData();
                barData();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        pieChart.animateXY(2000, 2000);
                        pieChart.invalidate();
                        barChart.animateXY(2000, 2000);
                    }
                });
            }
        }).start();
    }

    public void amountReservations(){
        try {
            tvAmountReservationsMade.setText(""+ParseUser.getCurrentUser().fetch().getNumber("reservations"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void pieData(){
        List<Integer> amountRestaurants = new ArrayList<>();
        List<ParseObject> restaurants = new ArrayList<>();
        List<ParseObject> historyRestaurants = new ArrayList<>();
        try {
            historyRestaurants = ParseUser.getCurrentUser().fetch().getList("historyRestaurant");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for(ParseObject parseObject : historyRestaurants){
            for(int i = 0; i<restaurants.size(); i++){
                if(restaurants.get(i).equals(parseObject)){
                    amountRestaurants.set(i, amountRestaurants.get(i)+1);
                    break;
                }
            }
            restaurants.add(parseObject);
            amountRestaurants.add(1);
        }
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "type";

        //initializing data
        Map<String, Integer> typeAmountMap = new HashMap<>();
        for(int i =0; i<restaurants.size(); i++){
            try {
                typeAmountMap.put(restaurants.get(i).fetch().getString("name"),amountRestaurants.get(i));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //input data and fit data into pie chart entry
        for(String type: typeAmountMap.keySet()){
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        //setting text size of the value
        pieDataSet.setValueTextSize(12f);
        //providing color list for coloring different entries
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(true);


        pieChart.setData(pieData);

    }
    public void barData(){
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("Ene");
        xAxisLabel.add("Feb");
        xAxisLabel.add("Mar");
        xAxisLabel.add("Abr");
        xAxisLabel.add("May");
        xAxisLabel.add("Jun");
        xAxisLabel.add("Jul");
        xAxisLabel.add("Ago");
        xAxisLabel.add("Sep");
        xAxisLabel.add("Oct");
        xAxisLabel.add("Nov");
        xAxisLabel.add("Dic");

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xAxisLabel.get((int) value);
            }
        };

        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);

        barChart.setDrawGridBackground(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);

        ArrayList<BarEntry> reservations = new ArrayList<>();
        List<Object> historyReservations = null;
        try {
            historyReservations = ParseUser.getCurrentUser().fetch().getList("historyReservations");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for(int i =0; i<historyReservations.size(); i++){
            reservations.add(new BarEntry(i,(int)historyReservations.get(i)));
        }

        BarDataSet barDataSet = new BarDataSet(reservations, "Reservations made by month");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);
        barDataSet.setDrawValues(false);

        BarData barData = new BarData(barDataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);

    }
}