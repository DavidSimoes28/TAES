package com.example.sentinel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class DashboardGuestActivity extends AppCompatActivity {

    private TextView temperaturaField, humidadeField, globalField, textViewData,textViewDataRefresh;
    private Button btnRegister;
    private ImageButton btnRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperaturaField = findViewById(R.id.textViewTemp);
        humidadeField = findViewById(R.id.textViewHumidade);
        globalField = findViewById(R.id.textViewGlobal);
        View btnLogin = findViewById(R.id.buttonLogin);
        textViewData = findViewById(R.id.textViewData);
        btnRegister = findViewById(R.id.buttonRegister);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh = findViewById(R.id.btnRefresh);
        textViewDataRefresh = findViewById(R.id.textViewDataRefresh);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        textViewDataRefresh.setText("Data de ultima atualização :"+ sdf.format(GregorianCalendar.getInstance().getTime()));


        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                textViewDataRefresh.setText("Data de ultima atualização :"+ sdf.format(GregorianCalendar.getInstance().getTime()));

            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardGuestActivity.this, LoginActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardGuestActivity.this, RegisterActivity.class));
            }
        });

        DatabaseReference databasereference = FirebaseDatabase.getInstance().getReference();
        databasereference.child("Sensores").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                int hum = 0;
                int temp = 0;
                int medHum= 0;
                int medTemp =0,i=0;
                Date dateReceived = null;
                Date dateLast = null;
                try {
                    dateLast = sdf.parse("00/00/0000 00:00:00");
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    for ( DataSnapshot valores : areaSnapshot.child("valores").getChildren()) {
                        try {
                            dateReceived = sdf.parse(valores.child("data").getValue().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if(dateLast.before(dateReceived)){
                                textViewData.setText("Data do sensor: " + valores.child("data").getValue().toString());
                                dateLast = dateReceived;
                        }
                        medHum += Integer.parseInt(valores.child("humidade").getValue().toString());
                        medTemp += Integer.parseInt(valores.child("temperatura").getValue().toString());
                        i++;
                    }
                }

                hum = medHum / i;
                temp = medTemp / i;

                humidadeField.setText(hum + "%");
                temperaturaField.setText(temp + " ºC");
                if((temp>35 || temp<19) && (hum>75 || hum<50)){
                    humidadeField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_red, null));
                    temperaturaField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_red, null));
                    globalField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_red, null));
                    globalField.setText("MAU");
                }else if((temp<=35 && temp>=19) && (hum>=50 && hum<=75)){
                    humidadeField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_green, null));
                    temperaturaField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_green, null));
                    globalField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_green, null));
                    globalField.setText("BOM");
                }else{
                    globalField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_yellow, null));
                    globalField.setText("MÉDIO");
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    public void refresh(){

    }

}