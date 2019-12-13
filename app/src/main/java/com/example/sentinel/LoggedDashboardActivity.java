package com.example.sentinel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.net.Uri;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.ImageButton;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sentinel.model.User;
import com.example.sentinel.model.UserManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.sql.RowSetListener;

public class LoggedDashboardActivity extends AppCompatActivity {

    private int hum;
    private int temp;
    private String localization;
    private String globalEvaluation;
    private TextView temperaturaField,humidadeField, globalField,dateField,dataRefresh;
    private Button btnTweet,btnLogout,btnListFavourites;
    private Button btnSend;
    private ImageButton btnFavorite, btnRefresh;
    private String email;
    public static final int btn_star_big_off = 17301515;
    public static final int btn_star_big_on = 17301516;
    private User utilizador;
    private boolean hasFavoritos = false;
    private int aux = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_dashboard);

        btnSend = findViewById(R.id.buttonSendData);

        btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentReceived = getIntent();
                    String email = intentReceived.getStringExtra("email");
                    Intent intent = new Intent(LoggedDashboardActivity.this, RegisterSensorActivity.class);
                    intent.putExtra("email",email);
                    startActivity(intent);
                }
            });


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
        btnFavorite = findViewById(R.id.btnFavorite);
        btnListFavourites = findViewById(R.id.buttonFavorite);
        dataRefresh = findViewById(R.id.textViewDatelRefresh);
        btnRefresh = findViewById(R.id.btnRefresh1);
        final Spinner spin = (Spinner) findViewById(R.id.spinner);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dataRefresh.setText("Data de ultima atualização :"+ sdf.format(GregorianCalendar.getInstance().getTime()));

        btnFavorite.setImageResource(btn_star_big_off);
        final DatabaseReference databasereference = FirebaseDatabase.getInstance().getReference();

        databasereference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hasFavoritos = false;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if(child.child("email").getValue().toString().equals(email)){
                        utilizador = new User(child.child("email").getValue().toString(),child.child("password").getValue().toString());
                        if(child.child("favoritos").getChildren().iterator().hasNext()){
                            for (DataSnapshot favoritos : child.child("favoritos").getChildren()) {
                                utilizador.addFavorito(favoritos.getValue().toString());
                                hasFavoritos = true;
                            }
                        }
                        UserManager.INSTANCE.addUser(utilizador);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databasereference.child("Sensores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                List<String> ids = new LinkedList<String>();

                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String id = areaSnapshot.child("localizacao").getValue(String.class);
                    ids.add(id);
                }

                ArrayAdapter<String> aa = new ArrayAdapter<>(LoggedDashboardActivity.this, android.R.layout.simple_spinner_dropdown_item, ids);
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Setting the ArrayAdapter data on the Spinner
                spin.setAdapter(aa);
                spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {


                        btnFavorite.setImageResource(btn_star_big_off);
                        if(hasFavoritos){
                            for (String favorito : utilizador.getFavoritos()) {
                                if(favorito.equals(spin.getSelectedItem().toString())){
                                    btnFavorite.setImageResource(btn_star_big_on);
                                }
                            }
                        }

                        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                            if (Objects.equals(areaSnapshot.child("localizacao").getValue(String.class), spin.getItemAtPosition(position).toString())){
                                hum=0;
                                temp=0;
                                localization="";
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                Date dateLast = null;
                                try {
                                    dateLast = sdf.parse("00/00/0000 00:00:00");
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Date dateReceived = null;
                                for ( DataSnapshot valores : areaSnapshot.child("valores").getChildren()) {
                                    try {
                                        dateReceived = sdf.parse(valores.child("data").getValue().toString());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if(dateLast.before(dateReceived)){
                                        dateLast = dateReceived;
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
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                aux = 0;
                databasereference.child("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int i = 0;
                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            if (user.child("email").getValue().toString().equals(email)) {
                                if (aux == 0) {
                                    if (utilizador.getFavoritos().contains(spin.getSelectedItem().toString())) {
                                        utilizador.removeFavorito(spin.getSelectedItem().toString());
                                        btnFavorite.setImageResource(btn_star_big_off);
                                        user.getRef().setValue(utilizador).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getApplicationContext(), "Sensor on " + spin.getSelectedItem().toString()
                                                        + " removed from the favorites", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        aux = 1;
                                    } else {
                                        utilizador.addFavorito(spin.getSelectedItem().toString());
                                        btnFavorite.setImageResource(btn_star_big_on);
                                        user.getRef().setValue(utilizador).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getApplicationContext(), "Sensor on " + spin.getSelectedItem().toString()
                                                        + " added to the favorites", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        aux = 1;
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


        btnListFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoggedDashboardActivity.this, FavoritesActivity.class);
                intent.putExtra("email",email);
                startActivity(intent);
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
                finish();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                dataRefresh.setText("Data de ultima atualização :"+ sdf.format(GregorianCalendar.getInstance().getTime()));
            }
        });
        }
}


