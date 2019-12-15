package com.example.sentinel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sentinel.model.Valor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

public class RegisterSensorActivity extends AppCompatActivity implements SensorEventListener {

    private Button btnSend,btnCancel;
    private EditText inputHumidade ,inputTemperatura,inputLocalizacao;

    private SensorManager sMgr ;
    private Sensor andTemp,andHum;
    String temperatura;
    String humidade;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE){
            inputTemperatura.setText(String.valueOf(Math.round(event.values[0])));
        }
        if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY){
            inputHumidade.setText(String.valueOf(Math.round(event.values[0])));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_sensor);

        inputHumidade = findViewById(R.id.editTextHumidade);
        inputTemperatura = findViewById(R.id.editTextTemperatura);

        sMgr = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        PackageManager PM = this.getPackageManager();
        boolean check = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_AMBIENT_TEMPERATURE);
        boolean check1 = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_RELATIVE_HUMIDITY);
        if (!check && !check1) {
            Toast.makeText(getApplicationContext(), "Android Device doesn´t have sensors for sending data", Toast.LENGTH_SHORT).show();
            try {
                wait(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final Intent intent = new Intent(RegisterSensorActivity.this, LoggedDashboardActivity.class);
            goback(intent);
        }

        andTemp = sMgr.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        andHum =sMgr.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        sMgr.registerListener(this,andTemp,SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener(this,andHum,SensorManager.SENSOR_DELAY_NORMAL);

        btnSend = findViewById(R.id.buttonSend);
        btnCancel = findViewById(R.id.buttonCancelarSend);

        inputLocalizacao = findViewById(R.id.editTextLocalizacao);
        Intent intentReceived = getIntent();
        final String local = intentReceived.getStringExtra("localization");
        inputLocalizacao.setText(local);

        final Intent intent = new Intent(RegisterSensorActivity.this, LoggedDashboardActivity.class);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String temperatura = inputTemperatura.getText().toString().trim();
                final String humidade = inputHumidade.getText().toString().trim();
                final String localizacao = inputLocalizacao.getText().toString().trim();

                Date data = GregorianCalendar.getInstance().getTime();

                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                final String formattedDate = df.format(data);

                DatabaseReference databasereference = FirebaseDatabase.getInstance().getReference("Sensores");
                databasereference.addValueEventListener(new ValueEventListener() {
                    int def = 0;

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                            if (def == 0) {
                                if (Objects.equals(areaSnapshot.child("localizacao").getValue(String.class), local)) {

                                    int hum = Integer.parseInt(areaSnapshot.child("humidade").getValue().toString());
                                    int temp = Integer.parseInt(areaSnapshot.child("temperatura").getValue().toString());
                                    String date = areaSnapshot.child("data").getValue().toString();

                                    Valor valor = new Valor(date, temp, hum);
                                    String key = areaSnapshot.getRef().push().getKey().replace("-", "").replace("_", "");
                                    areaSnapshot.child("valores").getRef().child(Objects.requireNonNull(key)).setValue(valor);

                                    areaSnapshot.child("humidade").getRef().setValue(humidade);

                                    areaSnapshot.child("temperatura").getRef().setValue(temperatura);
                                    areaSnapshot.child("data").getRef().setValue(formattedDate);

                                    Toast.makeText(getApplicationContext(), "Sucessfully sent SensorUp Data.", Toast.LENGTH_SHORT).show();

                                    def = 1;
                                    goback(intent);
                                    return;
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

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goback(intent);
            }
        });

    }

        private void goback(Intent intent) {
        Intent intentReceived = getIntent();
        String email = intentReceived.getStringExtra("email");
        intent.putExtra("email", email);
        startActivity(intent);
    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

