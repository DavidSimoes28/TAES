package com.example.sentinel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sentinel.model.User;
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
                        utilizador = new User(child.child("email").getValue().toString(), child.child("password").getValue().toString());
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
                    Toast.makeText(getApplicationContext(), "Sem favoritos adicionados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FavoritesActivity.this, PopUpDetailsActivity.class);
                intent.putExtra("localizacao",arrayList.get(position).toString());
                startActivity(intent);
            }
        });
    }
}
