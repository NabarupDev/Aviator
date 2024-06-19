package com.nabarup.avator;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
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
    TextView appName, generatedNum;
    EditText activationCodeInput;
    boolean isActivated = false;
    private Handler handler;
    private Runnable checkInternetRunnable;
    private AlertDialog alertDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict);

        activationBtn = findViewById(R.id.activation_btn);
        predictBtn = findViewById(R.id.get_pd_btn);
        logo = findViewById(R.id.logo);
        appName = findViewById(R.id.app_name);
        generatedNum = findViewById(R.id.number_gn);
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
                if (Home.isTimerRunningInHome) {
                    predictBtn.setVisibility(View.INVISIBLE);
                    showLoadingDialog();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            predictBtn.setVisibility(View.VISIBLE);
                            generateNumber();
                        }
                    }, 3000);
                } else {
                    showContactDialog();
                }
            }
        });

        // Initialize handler and runnable for internet checking
        handler = new Handler();
        checkInternetRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isConnected(Predict.this)) {
                    if (alertDialog == null || !alertDialog.isShowing()) {
                        alertDialog = buildDialog(Predict.this).show();
                    }
                } else {
                    if (alertDialog != null && alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(checkInternetRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && checkInternetRunnable != null) {
            handler.removeCallbacks(checkInternetRunnable);
        }
    }

    public boolean isConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnectedOrConnecting()) {
            NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            return (mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting());
        } else {
            return false;
        }
    }

    public AlertDialog.Builder buildDialog(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Oops! It seems like there's no internet connection. Please check your network settings and try again");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.setCancelable(true);
            }
        });
        return builder;
    }

    private void showLoadingDialog() {
        final Dialog dialog = new Dialog(Predict.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_loading_dialog);
        TextView text = dialog.findViewById(R.id.dialog_text);
        text.setText("Please wait, your prediction is loading...");
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 3000); // Dismiss dialog after 3 seconds
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
                if (isActivated) {
                    Toast.makeText(Predict.this, "Activation is already active.", Toast.LENGTH_SHORT).show();
                } else if (enteredCode.equals("D8CZZGQ2")) {
                    isActivated = true;
                    dialog.dismiss();
                    // Start or update timer in Home activity only if not already running
                    if (!Home.isTimerRunningInHome) {
                        setTimerInHome();
                    }
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
        final String formattedNumber = String.format("%.1f", randomNumber);
        final String numberWithX = formattedNumber + "x";
        ValueAnimator animator = ValueAnimator.ofFloat(0f, Float.parseFloat(formattedNumber));
        animator.setDuration(1000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                generatedNum.setText(String.format("%.1f", currentValue) + "x");
            }
        });
        animator.start();
    }

    private void showContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_contact_admin, null);
        builder.setView(dialogView);

        TextView contactAdminButton = dialogView.findViewById(R.id.contact_admin_button);
        TextView payText = dialogView.findViewById(R.id.pay_text);

        final AlertDialog dialog = builder.create();

        contactAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://t.me/TRONX0X";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        payText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Predict.this, upi.class);
                startActivity(intent);
            }
        });

        dialog.show();
    }

    // Method to start or update timer in Home activity
    private void setTimerInHome() {
        long durationInMillis = 24 * 60 * 60 * 1000;
        // Save end time to SharedPreferences
        long endTimeMillis = System.currentTimeMillis() + durationInMillis;
        SharedPreferences sharedPreferences = getSharedPreferences("com.nabarup.avator.PREFS", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("END_TIME_KEY", endTimeMillis);
        editor.apply();

        Home.isTimerRunningInHome = true;
        Intent intent = new Intent(Predict.this, Home.class);
        startActivity(intent);
        finish();
    }
}
