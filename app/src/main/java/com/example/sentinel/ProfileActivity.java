package com.example.sentinel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sentinel.model.User;
import com.example.sentinel.model.UserManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private int aux = 0;
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

        buttonAlterPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String old_password = editTextOldPassword.getText().toString().trim();
                final String new_password = editTextNewPassword.getText().toString().trim();
                final String confirmation_password = editTextConfirmationPassword.getText().toString().trim();

                if (TextUtils.isEmpty(old_password)) {
                    Toast.makeText(getApplicationContext(), "Enter old password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(new_password)) {
                    Toast.makeText(getApplicationContext(), "Enter new password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(confirmation_password)) {
                    Toast.makeText(getApplicationContext(), "Enter password confirmation!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (new_password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "New Password to small!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!old_password.equals(utilizador.getPassword())) {
                    Toast.makeText(getApplicationContext(), "Old Password is not correct!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!new_password.equals(confirmation_password)) {
                    Toast.makeText(getApplicationContext(), "Password and Confirm password are not the same!", Toast.LENGTH_SHORT).show();
                    return;
                }

                aux=0;
                databasereference.child("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if(child.child("email").getValue().toString().equals(email)){
                                if (aux == 0) {
                                    utilizador.setPassword(new_password);
                                    child.getRef().setValue(utilizador).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(), "Password updated", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    aux = 1;
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

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
