package com.example.sentinel;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sentinel.model.Sensor;
import com.example.sentinel.model.SensorManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class RegisterSensorActivity extends AppCompatActivity {

    private Button btnSend,btnCancel;
    private EditText inputLocalizacao,inputTemperatura,inputHumidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_sensor);

        btnSend = findViewById(R.id.buttonSend);
        btnCancel = findViewById(R.id.buttonCancelarSend);
        inputHumidade = findViewById(R.id.editTextHumidade);
        inputLocalizacao = findViewById(R.id.editTextLocalizacao);
        inputTemperatura = findViewById(R.id.editTextTemperatura);

        final Intent intent = new Intent(RegisterSensorActivity.this, LoggedDashboardActivity.class);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final String localizacao = inputLocalizacao.getText().toString().trim();
               final String temperatura = inputTemperatura.getText().toString().trim();
               final String humidade = inputHumidade.getText().toString().trim();

               if (TextUtils.isEmpty(localizacao)){
                   Toast.makeText(getApplicationContext(),"Insira Localização!",Toast.LENGTH_SHORT).show();
                   return;
               }
                if (TextUtils.isEmpty(temperatura)){
                    Toast.makeText(getApplicationContext(),"Insira Temperatura!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(humidade)){
                    Toast.makeText(getApplicationContext(),"Insira Humidade!",Toast.LENGTH_SHORT).show();
                    return;
                }
                Date data = Calendar.getInstance().getTime();

                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy");
                String formattedDate = df.format(data);

                DatabaseReference databasereference = FirebaseDatabase.getInstance().getReference("Sensores");

                Intent intentReceived = getIntent();
                String email = intentReceived.getStringExtra("email");

                Sensor sensor = new Sensor(localizacao,humidade,formattedDate,temperatura,email);

                String key = databasereference.push().getKey();
                databasereference.child(Objects.requireNonNull(key)).setValue(sensor);

                Toast.makeText(getApplicationContext(), "Sensor Registration sucessful.", Toast.LENGTH_SHORT).show();
                SensorManager.INSTANCE.addSensor(sensor);

                intent.putExtra("email",email);
                startActivity(intent);




            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentReceived = getIntent();
                String email = intentReceived.getStringExtra("email");
                intent.putExtra("email",email);
                startActivity(intent);
            }
        });

    }
}
