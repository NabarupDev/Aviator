package com.nabarup.avator.login;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nabarup.avator.BuildConfig;
import com.nabarup.avator.HelperClass;
import com.nabarup.avator.R;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sign_up extends AppCompatActivity {

    TextView login_txt, telegram;
    ImageView passwordVisibilityToggle;
    EditText password_edit_box, sign_name, sign_email, sign_number;
    Button sign_up_btn;
    FirebaseDatabase database;
    Context context;
    DatabaseReference reference;
    private static final String FIREBASE_APP_NAME = "com.nabarup.avator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        context = this;
        setContentView(R.layout.activity_sign_up);

        sign_email = findViewById(R.id.sign_email);
        sign_name = findViewById(R.id.sign_name);
        sign_number = findViewById(R.id.signup_number);
        sign_up_btn = findViewById(R.id.sign_up_button);
        login_txt = findViewById(R.id.login_txt);
        telegram = findViewById(R.id.teg_txt);
        passwordVisibilityToggle = findViewById(R.id.pass_view_img);
        password_edit_box = findViewById(R.id.sign_up_pass);

        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    FirebaseOptions options = new FirebaseOptions.Builder()
                            .setApplicationId(BuildConfig.APPLICATION_ID)
                            .setApiKey("AIzaSyDcfg6H13d8ecgaxWexx3RRsJFm8Ir0qH4")
                            .setDatabaseUrl("https://avator-dd16f-default-rtdb.firebaseio.com/")
                            .setProjectId("avator-dd16f")
                            .build();

                    FirebaseApp app = FirebaseApp.initializeApp(context, options, FIREBASE_APP_NAME);
                    database = FirebaseDatabase.getInstance(app);
                    reference = database.getReference("users");

                    String name = sign_name.getText().toString();
                    String number = sign_number.getText().toString();
                    String email = sign_email.getText().toString();
                    String password = password_edit_box.getText().toString();

                    String hashedPassword = hashPassword(password);

                    String uid = reference.push().getKey();

                    HelperClass helperClass = new HelperClass(name, email, number, hashedPassword);

                    reference.child(uid).setValue(helperClass);

                    Toast.makeText(Sign_up.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Sign_up.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        });



        login_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Sign_up.this, Login.class);
                startActivity(intent);
            }
        });

        telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://t.me/nr_devlope";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        passwordVisibilityToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });
    }

    // Validate email and password input
    private boolean validateInput() {
        String name = sign_name.getText().toString().trim();
        String email = sign_email.getText().toString().trim();
        String phone_no = sign_number.getText().toString().trim();
        String password = password_edit_box.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            sign_name.setError("Name cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(email)) {
            sign_email.setError("Email cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(phone_no)) {
            sign_number.setError("Phone Number cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(password)) {
            password_edit_box.setError("Password cannot be empty");
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearEditTextFields();
    }

    private void clearEditTextFields() {
        sign_name.setText("");
        sign_email.setText("");
        sign_number.setText("");
        password_edit_box.setText("");
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void togglePasswordVisibility() {
        int inputType = password_edit_box.getInputType();
        if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            password_edit_box.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordVisibilityToggle.setImageResource(R.drawable.ic_eye); // Set eye icon
        } else {
            password_edit_box.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordVisibilityToggle.setImageResource(R.drawable.ic_eye_off);
        }
        password_edit_box.setSelection(password_edit_box.getText().length());
    }
}
