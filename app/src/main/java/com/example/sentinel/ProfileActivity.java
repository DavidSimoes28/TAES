package com.example.sentinel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.sentinel.model.User;
import com.example.sentinel.model.UserManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private User utilizador;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            email = extras.getString("email");
        }

        final DatabaseReference databasereference = FirebaseDatabase.getInstance().getReference();
        databasereference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if(child.child("email").getValue().toString().equals(email)){
                        utilizador = new User(child.child("email").getValue().toString(),child.child("password").getValue().toString());
                        if(child.child("favoritos").getChildren().iterator().hasNext()){
                            for (DataSnapshot favoritos : child.child("favoritos").getChildren()) {
                                utilizador.addFavorito(favoritos.getValue().toString());
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
}
