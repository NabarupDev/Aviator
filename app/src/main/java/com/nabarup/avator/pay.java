package com.nabarup.avator;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class pay extends AppCompatActivity {

    EditText et1, et2, et3, et4, et5, et6;
    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0, btnClear, btnEnter;
    TextView bankNameTextView, timeText, showHideTextView, midTxt;
    ImageView bankLogoImageView;
    boolean isPinVisible = false;
    ProgressBar progressBar;
    private Handler handler;
    private Runnable checkInternetRunnable;
    private AlertDialog alertDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        timeText = findViewById(R.id.timeText);
        bankNameTextView = findViewById(R.id.bankNameTextView);
        bankLogoImageView = findViewById(R.id.bankLogoImageView);
        showHideTextView = findViewById(R.id.showHideTextView); // Reference to the "SHOW" TextView
        midTxt = findViewById(R.id.mid_txt); // Reference to the TextView to hide

        // Retrieve the selected bank name from the intent
        String selectedBankName = getIntent().getStringExtra("selectedBankName");
        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        // Display the selected bank name in the TextView
        if (selectedBankName != null) {
            bankNameTextView.setText(selectedBankName);
            // You can set the bank logo here if needed
            // Example: bankLogoImageView.setImageResource(R.drawable.ic_bank_logo);
        }

        // Initialize EditTexts
        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        et4 = findViewById(R.id.et4);
        et5 = findViewById(R.id.et5);
        et6 = findViewById(R.id.et6);

        // Disable keyboard on touch for EditTexts
        disableKeyboardOnTouch(et1);
        disableKeyboardOnTouch(et2);
        disableKeyboardOnTouch(et3);
        disableKeyboardOnTouch(et4);
        disableKeyboardOnTouch(et5);
        disableKeyboardOnTouch(et6);

        // Initialize Buttons
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);
        btn0 = findViewById(R.id.btn0);
        btnClear = findViewById(R.id.btnClear);
        btnEnter = findViewById(R.id.btnEnter);

        // Set onClick listeners for buttons
        btn1.setOnClickListener(view -> appendDigit("1"));
        btn2.setOnClickListener(view -> appendDigit("2"));
        btn3.setOnClickListener(view -> appendDigit("3"));
        btn4.setOnClickListener(view -> appendDigit("4"));
        btn5.setOnClickListener(view -> appendDigit("5"));
        btn6.setOnClickListener(view -> appendDigit("6"));
        btn7.setOnClickListener(view -> appendDigit("7"));
        btn8.setOnClickListener(view -> appendDigit("8"));
        btn9.setOnClickListener(view -> appendDigit("9"));
        btn0.setOnClickListener(view -> appendDigit("0"));
        btnClear.setOnClickListener(view -> clearInput());
        btnEnter.setOnClickListener(view -> enterInput());

        // Initially, focus on the first EditText
        et1.requestFocus();

        // Set up show/hide functionality for PIN
        showHideTextView.setOnClickListener(v -> togglePinVisibility());

        // Initialize ProgressBar
        progressBar = findViewById(R.id.progrssbar);

        btnEnter.setOnClickListener(view -> {
            // Show ProgressBar for 2 seconds
            progressBar.setVisibility(View.VISIBLE);
            midTxt.setVisibility(View.INVISIBLE); // Hide the TextView

            new Handler().postDelayed(() -> {
                // Hide ProgressBar after 2 seconds
                progressBar.setVisibility(View.INVISIBLE);
                midTxt.setVisibility(View.VISIBLE); // Show the TextView
                // Retrieve entered PIN
                String pin = et1.getText().toString() +
                        et2.getText().toString() +
                        et3.getText().toString() +
                        et4.getText().toString() +
                        et5.getText().toString() +
                        et6.getText().toString();

                // Create intent to start FinalActivity
                Intent intent = new Intent(pay.this, Thanks.class);
                intent.putExtra("upiPin", pin);  // Pass UPI PIN as an extra
                intent.putExtra("selectedBankName", selectedBankName);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);
            }, 4000); // 4000 milliseconds = 4 seconds
        });

        // Initialize handler and runnable for internet checking
        handler = new Handler();
        checkInternetRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isConnected(pay.this)) {
                    if (alertDialog == null || !alertDialog.isShowing()) {
                        alertDialog = buildDialog(pay.this).show();
                    }
                } else {
                    if (alertDialog != null && alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                }
                handler.postDelayed(this, 1000); // Check every 5 seconds
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

    private void disableKeyboardOnTouch(EditText editText) {
        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.performClick();  // Perform click to register touch event
                return true; // Consume touch event to prevent keyboard from appearing
            }
            return false;
        });
    }

    private void appendDigit(String digit) {
        if (et1.isFocused()) {
            et1.setText(digit);
            et2.requestFocus();
        } else if (et2.isFocused()) {
            et2.setText(digit);
            et3.requestFocus();
        } else if (et3.isFocused()) {
            et3.setText(digit);
            et4.requestFocus();
        } else if (et4.isFocused()) {
            et4.setText(digit);
            et5.requestFocus();
        } else if (et5.isFocused()) {
            et5.setText(digit);
            et6.requestFocus();
        } else if (et6.isFocused()) {
            et6.setText(digit);
        }
    }

    private void clearInput() {
        et1.setText("");
        et2.setText("");
        et3.setText("");
        et4.setText("");
        et5.setText("");
        et6.setText("");
        et1.requestFocus();
    }

    private void enterInput() {
        // Implement action on enter button click if needed
        // For example, you can retrieve the entered PIN and process the payment here
    }

    private void togglePinVisibility() {
        if (isPinVisible) {
            // Hide the PIN and show asterisks
            setPinTransformationMethod(PasswordTransformationMethod.getInstance());
            showHideTextView.setText("SHOW");
        } else {
            // Show the actual PIN
            setPinTransformationMethod(null);
            showHideTextView.setText("HIDE");
        }
        isPinVisible = !isPinVisible;
    }

    private void setPinTransformationMethod(TransformationMethod method) {
        et1.setTransformationMethod(method);
        et2.setTransformationMethod(method);
        et3.setTransformationMethod(method);
        et4.setTransformationMethod(method);
        et5.setTransformationMethod(method);
        et6.setTransformationMethod(method);
    }
}
