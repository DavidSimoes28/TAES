package com.example.sentinel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;




/**
 * Created by jonas on 10.09.16.
 */
public class StatisticsActivity extends AppCompatActivity {
    private int hum;
    private int temp;
    private String localization;
    private LineGraphSeries<DataPoint> series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);


        //final DatabaseReference databasereference = FirebaseDatabase.getInstance().getReference();

        GraphView graph = findViewById(R.id.graph);
        initGraph(graph);
    }


    public void initGraph(final GraphView graph) {
        final Spinner spin = (Spinner) findViewById(R.id.spinner);
        final DatabaseReference databasereference = FirebaseDatabase.getInstance().getReference();
        databasereference.child("Sensores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                List<String> ids = new LinkedList<String>();

                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String id = areaSnapshot.child("localizacao").getValue(String.class);
                    ids.add(id);
                }

                ArrayAdapter<String> aa = new ArrayAdapter<>(StatisticsActivity.this, android.R.layout.simple_spinner_dropdown_item, ids);
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Setting the ArrayAdapter data on the Spinner
                spin.setAdapter(aa);
                spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {


                        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                            if (Objects.equals(areaSnapshot.child("localizacao").getValue(String.class), spin.getItemAtPosition(position).toString())) {
                                hum = 0;
                                temp = 0;
                                localization = "";

                                for (DataSnapshot valores : areaSnapshot.child("valores").getChildren()) {
                                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                                    new DataPoint(1, 3)
                                    });

                                }
                                graph.addSeries(series);

                                /*LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                                        new DataPoint(0, 1),
                                        new DataPoint(1, 5),
                                        new DataPoint(2, 3),
                                        new DataPoint(3, 2),
                                        new DataPoint(4, 6)
                                });
                                graph.addSeries(series);*/


                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }
}