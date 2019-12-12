package com.example.sentinel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sentinel.model.Favoritos;
import com.example.sentinel.model.Sensor;
import com.example.sentinel.model.SensorManager;
import com.example.sentinel.model.Valor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class RegisterSensorActivity extends AppCompatActivity {

    private Button btnSend,btnCancel;
    private EditText inputTemperatura,inputHumidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_sensor);

        btnSend = findViewById(R.id.buttonSend);
        btnCancel = findViewById(R.id.buttonCancelarSend);
        inputHumidade = findViewById(R.id.editTextHumidade);

        inputTemperatura = findViewById(R.id.editTextTemperatura);
        final Spinner spin = (Spinner) findViewById(R.id.spinner);
        fillSpinner(spin);

        final Intent intent = new Intent(RegisterSensorActivity.this, LoggedDashboardActivity.class);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                final String temperatura = inputTemperatura.getText().toString().trim();
                final String humidade = inputHumidade.getText().toString().trim();

                if (TextUtils.isEmpty(temperatura)) {
                    Toast.makeText(getApplicationContext(), "Insira Temperatura!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(humidade)) {
                    Toast.makeText(getApplicationContext(), "Insira Humidade!", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (Integer.parseInt(temperatura) > 80 && Integer.parseInt(temperatura) < 0) {
                    Toast.makeText(getApplicationContext(), "Temperatura tem que ser entre 0 e 80", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Integer.parseInt(humidade) > 100 && Integer.parseInt(humidade) < 0) {
                    Toast.makeText(getApplicationContext(), "Humidade tem que ser entre 0 e 100", Toast.LENGTH_SHORT).show();
                    return;
                }


                Date data = Calendar.getInstance().getTime();

                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                final String formattedDate = df.format(data);

                DatabaseReference databasereference = FirebaseDatabase.getInstance().getReference("Sensores");

                databasereference.addValueEventListener(new ValueEventListener() {
                    int def= 0;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                            if (def == 0) {
                                if (Objects.equals(areaSnapshot.child("localizacao").getValue(String.class), spin.getSelectedItem().toString())) {

                                    int hum = Integer.parseInt(areaSnapshot.child("humidade").getValue().toString());
                                    int temp = Integer.parseInt(areaSnapshot.child("temperatura").getValue().toString());
                                    String date = areaSnapshot.child("data").getValue().toString();

                                    Valor valor = new Valor(date, temp, hum);
                                    String key = areaSnapshot.getRef().push().getKey().replace("-","").replace("_","");
                                    areaSnapshot.child("valores").getRef().child(Objects.requireNonNull(key)).setValue(valor);

                                    areaSnapshot.child("humidade").getRef().setValue(humidade);

                                    areaSnapshot.child("temperatura").getRef().setValue(temperatura);
                                    areaSnapshot.child("data").getRef().setValue(formattedDate);

                                    Toast.makeText(getApplicationContext(), "Sucessfully sent Sensor Data.", Toast.LENGTH_SHORT).show();

                                    def = 1;
                                    Intent intentReceived = getIntent();
                                    String email = intentReceived.getStringExtra("email");
                                    intent.putExtra("email",email);
                                    startActivity(intent);
                                    return;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            };








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

    private void fillSpinner(final Spinner spin) {
        DatabaseReference databasereference = FirebaseDatabase.getInstance().getReference();
        databasereference.child("Sensores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                List<String> ids = new LinkedList<String>();

                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String id = areaSnapshot.child("localizacao").getValue(String.class);
                    ids.add(id);
                }

                ArrayAdapter<String> aa = new ArrayAdapter<>(RegisterSensorActivity.this, android.R.layout.simple_spinner_dropdown_item, ids);
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Setting the ArrayAdapter data on the Spinner
                spin.setAdapter(aa);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
