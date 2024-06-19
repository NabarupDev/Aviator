package com.nabarup.avator.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nabarup.avator.Home;
import com.nabarup.avator.R;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    Button loginButton;
    ProgressBar progressBar;
    TextView signupRedirectText, Forgot_password;
    ImageView passwordVisibilityToggle;
    String gmailPattern = "[a-zA-Z0-9._%+-]+@gmail\\.[a-z]+";
    private SharedPreferences sharedPref;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        boolean loggedInBefore = sharedPref.getBoolean("loggedInBefore", false);

        if (loggedInBefore) {
            Intent intent = new Intent(Login.this, Home.class);
            startActivity(intent);
            finish();
        } else {
            FirebaseApp.initializeApp(this);
            setContentView(R.layout.activity_login);

            loginEmail = findViewById(R.id.login_email);
            loginPassword = findViewById(R.id.login_pass);
            loginButton = findViewById(R.id.login_button);
            progressBar = findViewById(R.id.progrssbar);
            passwordVisibilityToggle = findViewById(R.id.pass_view_img);
            signupRedirectText = findViewById(R.id.signupRedirectText);
            Forgot_password = findViewById(R.id.forgot_pass);

            Forgot_password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Login.this, Forgot_pass.class);
                    startActivity(intent);
                }
            });

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validateInput()) {
                        String email = loginEmail.getText().toString().trim();
                        String password = loginPassword.getText().toString().trim();
                        progressBar.setVisibility(View.VISIBLE);
                        loginButton.setVisibility(View.INVISIBLE);
                        checkUser(email, password);
                    }
                }
            });

            signupRedirectText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Login.this, Sign_up.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private boolean validateInput() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            loginEmail.setError("Email cannot be empty");
            return false;
        }else if (!email.matches(gmailPattern)) {
            loginEmail.setError("Invalid email address");
            return false;
        }else if (TextUtils.isEmpty(password)) {
            loginPassword.setError("Password cannot be empty");
            return false;
        }
        return true;
    }

    public void checkUser(String email, String password) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query query = reference.orderByChild("email").equalTo(email);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.INVISIBLE);
                loginButton.setVisibility(View.VISIBLE);
                if (dataSnapshot.exists()) {
                    // User already login
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String passwordFromDB = snapshot.child("password").getValue(String.class);

                        if (passwordFromDB.equals(hashPassword(password))) {
                            Intent intent = new Intent(Login.this, Home.class);
                            startActivity(intent);
                            finish();

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("loggedInBefore", true);
                            editor.apply();

                            return;
                        }
                    }

                    Toast.makeText(Login.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    // loginPassword.setError("Incorrect password");
                } else {
                    loginEmail.setError("No user with this email exists");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("LoginActivity", "DatabaseError: " + databaseError.getMessage());
                Toast.makeText(Login.this, "DatabaseError", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                loginButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}