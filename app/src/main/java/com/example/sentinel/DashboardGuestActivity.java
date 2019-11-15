package com.example.sentinel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardGuestActivity extends AppCompatActivity {

    private TextView temperaturaField, humidadeField, globalField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperaturaField = findViewById(R.id.textViewTemp);
        humidadeField = findViewById(R.id.textViewHumidade);
        globalField = findViewById(R.id.textViewGlobal);
        View btnLogin = findViewById(R.id.button_login);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                view.getContext().startActivity(intent);}
        });

        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int hum = Integer.parseInt(dataSnapshot.child("Humidade").getValue().toString());
                int temp = Integer.parseInt(dataSnapshot.child("Temperatura").getValue().toString());
                humidadeField.setText(dataSnapshot.child("Humidade").getValue().toString() + "%");
                temperaturaField.setText(dataSnapshot.child("Temperatura").getValue().toString() + " ºC");
                if(temp>=35 && hum>=75){
                    humidadeField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_red, null));
                    temperaturaField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_red, null));
                    globalField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_red, null));
                    globalField.setText("MAU");
                }else if(temp<=19 && hum<=50){
                    humidadeField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_green, null));
                    temperaturaField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_green, null));
                    globalField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_green, null));
                    globalField.setText("BOM");
                }else{
                    globalField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_yellow, null));
                    globalField.setText("MÉDIO");
                    if(temp<=19){
                        temperaturaField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_green, null));
                    }else if(temp>=35){
                        temperaturaField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_red, null));
                    }else{
                        temperaturaField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_yellow, null));
                    }

                    if(hum<=50){
                        humidadeField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_green, null));
                    }else if(hum>=75){
                        humidadeField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_red, null));
                    }else{
                        humidadeField.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_textview_yellow, null));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void login(View view) {
    }
}