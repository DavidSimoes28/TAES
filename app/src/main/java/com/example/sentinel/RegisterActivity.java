package com.example.sentinel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sentinel.model.User;
import com.example.sentinel.model.UserManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button btnRegister, btnCancel;
    private EditText inputEmail, inputPassword, inputPasswordConfirmation, inputName;
    private TextView forgetPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        btnRegister = findViewById(R.id.buttonRegister);
        inputEmail = findViewById(R.id.editTextEmail);
        inputPassword = findViewById(R.id.editTextPassword);
        inputPasswordConfirmation = findViewById(R.id.editTextConfirmPassword);
        inputName = findViewById(R.id.editTextName);
        btnCancel = findViewById(R.id.buttonCancelarLogin);
        forgetPassword = findViewById(R.id.textViewForgetPassword);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = inputName.getText().toString().trim();
                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                final String passwordConfirmation = inputPasswordConfirmation.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter your name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password to small!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(passwordConfirmation)) {
                    Toast.makeText(getApplicationContext(), "Password and Confirm password are not the same!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(getApplicationContext(), "E-mail address is not valid!", Toast.LENGTH_SHORT).show();
                }

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final User user = new User(name,email,password);
                            FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(), "Registration success.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Register failed.", Toast.LENGTH_SHORT).show();
                        }task.isSuccessful();
                    }
                });
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
