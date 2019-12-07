package com.example.sentinel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sentinel.model.UserManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class LoggedDashboardActivity extends AppCompatActivity {

    private int hum;
    private int temp;
    private String localization;
    private String globalEvaluation;
    private TextView temperaturaField,humidadeField, globalField,dateField;
    private Button btnTweet,btnLogout;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_dashboard);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            email = extras.getString("email");
        }

        temperaturaField = findViewById(R.id.textViewTemperature);
        humidadeField = findViewById(R.id.textViewHumidadel);
        globalField = findViewById(R.id.textViewGloball);
        btnTweet = findViewById(R.id.buttonShare);
        dateField = findViewById(R.id.textViewDatel);
        btnLogout = findViewById(R.id.buttonLogOut);

        DatabaseReference databasereference = FirebaseDatabase.getInstance().getReference();
        databasereference.child("Sensores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                List<String> ids = new LinkedList<String>();

                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String id = areaSnapshot.child("localizacao").getValue(String.class);
                    ids.add(id);
                }

                final Spinner spin = (Spinner) findViewById(R.id.spinner);


                ArrayAdapter<String> aa = new ArrayAdapter<>(LoggedDashboardActivity.this, android.R.layout.simple_spinner_dropdown_item, ids);
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Setting the ArrayAdapter data on the Spinner
                spin.setAdapter(aa);
                spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                            if (Objects.equals(areaSnapshot.child("localizacao").getValue(String.class), spin.getItemAtPosition(position).toString())){

                                for ( DataSnapshot valores : areaSnapshot.child("valores").getChildren()) {
                                    hum = Integer.parseInt(valores.child("humidade").getValue().toString());
                                    temp = Integer.parseInt(valores.child("temperatura").getValue().toString());
                                    dateField.setText("Data do registo: " + valores.child("data").getValue().toString());
                                    localization = areaSnapshot.child("localizacao").getValue(String.class);
                                    humidadeField.setText(hum + "%");
                                    temperaturaField.setText(temp + " ºC");

                                    if((temp>35 || temp<19) && (hum>75 || hum<50)){
                                        humidadeField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_red, null));
                                        temperaturaField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_red, null));
                                        globalField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_red, null));
                                        globalField.setText("MAU");
                                        globalEvaluation = "MAU";
                                    }else if((temp<=35 && temp>=19) && (hum>=50 && hum<=75)){
                                        humidadeField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_green, null));
                                        temperaturaField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_green, null));
                                        globalField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_green, null));
                                        globalField.setText("BOM");
                                        globalEvaluation = "BOM";
                                    }else{
                                        globalField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_yellow, null));
                                        globalField.setText("MÉDIO");
                                        globalEvaluation = "MÉDIO";
                                        if(temp<19 || temp>35){
                                            temperaturaField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_red, null));

                                        }else{
                                            temperaturaField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_green, null));
                                        }

                                        if(hum<50 || hum>75){
                                            humidadeField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_red, null));

                                        }else{
                                            humidadeField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_green, null));
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                    }

                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ShareMessage = "Edificio: A\nLocalização:"+ localization
                        + "\nTemperatura: "+ temp
                        + "ºC\nHumidade: "+ hum
                        + "%\nAvaliação geral: " + globalEvaluation
                        + "\n\n Sentinel Application";
                String tweetUrl = "https://twitter.com/intent/tweet?text=" + Uri.encode(ShareMessage);
                Uri uri = Uri.parse(tweetUrl);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                UserManager.INSTANCE.setFirebaseUser(email,null);
                finish();
            }
        });
        }
    }


