package com.example.sentinel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    private TextView textViewName,textViewEmail;
    private Button buttonAlterPassword,buttonCancel;
    private EditText editTextOldPassword,editTextNewPassword,editTextConfirmationPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            email = extras.getString("email");
        }

        textViewName = findViewById(R.id.textViewNomeProfile);
        textViewEmail = findViewById(R.id.textViewEmailProfile);
        buttonAlterPassword = findViewById(R.id.buttonAlterPasswordProfile);
        buttonCancel = findViewById(R.id.buttonCancelProfile);
        editTextOldPassword = findViewById(R.id.editTextOldPasswordProfile);
        editTextNewPassword = findViewById(R.id.editTextNewPasswordProfile);
        editTextConfirmationPassword = findViewById(R.id.editTextConfirmationPasswordProfile);


        final DatabaseReference databasereference = FirebaseDatabase.getInstance().getReference();
        databasereference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if(child.child("email").getValue().toString().equals(email)){
                        utilizador = new User(child.child("name").getValue().toString(),child.child("email").getValue().toString(),child.child("password").getValue().toString());
                        textViewName.setText(utilizador.getName());
                        textViewEmail.setText(utilizador.getEmail());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
