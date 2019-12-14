package com.example.sentinel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sentinel.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class FavoritesActivity extends AppCompatActivity {

    private ListView listView;
    private User utilizador;
    private String email;
    private boolean hasFavoritos = false;
    private ArrayList<String> arrayList;
    private int aux = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        listView = findViewById(R.id.listViewFavorites);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            email = extras.getString("email");
        }

        final DatabaseReference databasereference = FirebaseDatabase.getInstance().getReference();

        databasereference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hasFavoritos = false;
                arrayList = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (Objects.requireNonNull(child.child("email").getValue()).toString().equals(email)) {
                        utilizador = new User(child.child("name").getValue().toString(),child.child("email").getValue().toString(), child.child("password").getValue().toString());
                        if (child.child("favoritos").getChildren().iterator().hasNext()) {
                            for (DataSnapshot favoritos : child.child("favoritos").getChildren()) {
                                utilizador.addFavorito(favoritos.getValue().toString());
                                arrayList.add(favoritos.getValue().toString());
                                hasFavoritos = true;
                            }
                        }
                    }
                }
                if (hasFavoritos) {
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(FavoritesActivity.this, android.R.layout.simple_list_item_1, arrayList);
                    listView.setAdapter(arrayAdapter);
                }else {
                    Toast.makeText(getApplicationContext(), "You don't have favorite sensors", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String localizacao = arrayList.get(position);


                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(DialogInterface.BUTTON_POSITIVE == which) {
                            databasereference.child("Users").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                                        if (user.child("email").getValue().toString().equals(email)) {
                                            if (aux == 0) {
                                                if (utilizador.getFavoritos().contains(localizacao)) {
                                                    utilizador.removeFavorito(localizacao);
                                                    user.getRef().setValue(utilizador).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(getApplicationContext(), "Sensor on " + localizacao
                                                                    + " removed from the favorites", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                    arrayList.remove(localizacao);
                                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(FavoritesActivity.this, android.R.layout.simple_list_item_1, arrayList);
                                                    listView.setAdapter(arrayAdapter);
                                                    aux = 1;
                                                    setResult(RESULT_OK);
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }else if(DialogInterface.BUTTON_NEGATIVE == which) {
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(FavoritesActivity.this);
                builder.setMessage("Do you want to remove the sensor on the localization " + localizacao + " from your favorites").setPositiveButton("Sim", dialogClickListener)
                        .setNegativeButton("Não", dialogClickListener).show();
            }
        });
    }
}
