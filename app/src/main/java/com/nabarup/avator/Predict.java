package com.nabarup.avator;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class Predict extends AppCompatActivity {

    Button activationBtn, predictBtn;
    ImageView logo;
    TextView Loading_txt;
    TextView appName, generatedNum, telegram, Predict_btn_txt;
    boolean isFirstTime = true;
    boolean isActivated = false;
    EditText activationCodeInput;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict);

        activationBtn = findViewById(R.id.activation_btn);
        Loading_txt = findViewById(R.id.loading_txt);
        predictBtn = findViewById(R.id.get_pd_btn);
        logo = findViewById(R.id.logo);
        appName = findViewById(R.id.app_name);
        telegram = findViewById(R.id.teg_txt);
        generatedNum = findViewById(R.id.number_gn); // Initialize generatedNum TextView
        activationCodeInput = findViewById(R.id.dialogMessage);

        activationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });

        predictBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isActivated) {
                    predictBtn.setVisibility(View.INVISIBLE);
                    Animation slideInLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left);
                    Loading_txt.startAnimation(slideInLeft);
                    Loading_txt.setVisibility(View.VISIBLE);
                    simulateBackgroundTask();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Animation slideOutRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
                            Loading_txt.startAnimation(slideOutRight);
                            Loading_txt.setVisibility(View.INVISIBLE);
                            predictBtn.setVisibility(View.VISIBLE);
                            generateNumber();
                        }
                    }, 2000);
                } else {
                    showContactDialog();
                    // Toast.makeText(Predict.this, "Please activate the app first", Toast.LENGTH_SHORT).show();
                }
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


    }
    private void simulateBackgroundTask() {
        try {
            // Simulate some delay
            Thread.sleep(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void showContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Aviator Predictor v4.0 is not activated!")
                .setMessage("You need to activate Aviator Predictor v4.0. For this, you need to contact Aviator Predictor Admin.\n" +
                        "\n" +
                        "P.S. THE ACTIVATION IS PAID.")
                .setPositiveButton("Contact admin", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = "https://t.me/nr_devlope";
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                })
                .show();
    }

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.active_bg, null);
        activationCodeInput = dialogView.findViewById(R.id.dialogMessage);
        builder.setView(dialogView);
        TextView okButton = dialogView.findViewById(R.id.okButton);
        AlertDialog dialog = builder.create();
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredCode = activationCodeInput.getText().toString().trim();
                if (enteredCode.equals("D8CZZGQ2")) {
                    isActivated = true;
                    dialog.dismiss();
                } else {
                    Toast.makeText(Predict.this, "Incorrect activation code", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void generateNumber() {
        Random random = new Random();
        double randomNumber;

        // 70% chance
        if (random.nextDouble() <= 0.7) {
            randomNumber = random.nextDouble() * 10;
        } else {
            // 30% chance
            randomNumber = random.nextDouble() * 90 + 10;
        }

        String formattedNumber = String.format("%.1f", randomNumber);

        ValueAnimator animator = ValueAnimator.ofFloat(0, (float) randomNumber);
        animator.setDuration(1000); // 1 second animation duration
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                generatedNum.setText(String.format("%.1f", currentValue) + "x");
            }
        });
        animator.start();
    }
}

