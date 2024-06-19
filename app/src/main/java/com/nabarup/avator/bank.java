package com.nabarup.avator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class bank extends AppCompatActivity {

    private int[] bankLogos = {
            R.drawable.white,
            R.drawable.axis,
            R.drawable.bank_of_borda,
            R.drawable.bank__of_ndia,
            R.drawable.canara_bank,
            R.drawable.central,
            R.drawable.dena_ank,
            R.drawable.hdfc_ank,
            R.drawable.icic,
            R.drawable.idbi,
            R.drawable.indian_ank,
            R.drawable.indian_verseas_ank,
            R.drawable.jammu_ashmir_bank,
            R.drawable.punjab_and_sind_ank,
            R.drawable.punjub_national_bank,
            R.drawable.state_bank_of_ndia,
            R.drawable.uco_bank,
            R.drawable.union_bank_of_ndia,
            R.drawable.vijaya_ank,
            R.drawable.others_bank,
    };

    private String[] bankNames = {
            "Select your bank ▼ ",
            "Axis Bank",
            "Bank of Baroda",
            "Bank of India",
            "Canara Bank",
            "Central Bank of India",
            "Dena Bank",
            "HDFC Bank",
            "ICICI Bank",
            "IDBI Bank",
            "Indian Bank",
            "Indian Overseas Bank",
            "Jammu & Kashmir Bank",
            "Punjab and Sind Bank",
            "Punjab National Bank",
            "State Bank of India",
            "UCO Bank",
            "Union Bank of India",
            "Vijaya Bank",
            "Others Bank"
    };

    private String selectedBankName;

    private Spinner spinnerBanks;
    private EditText phoneEditText;
    private EditText bankNameEditText;
    private TextView bankTextView;
    private RadioGroup checkedBox;
    private RadioButton payUsingApps;

    private Handler handler;
    private Runnable checkInternetRunnable;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);

        spinnerBanks = findViewById(R.id.spinner_banks);
        phoneEditText = findViewById(R.id.phno_edit); // Reference to phone number EditText
        bankNameEditText = findViewById(R.id.bank_name);
        bankTextView = findViewById(R.id.bn_txt);
        checkedBox = findViewById(R.id.checked_box);
        payUsingApps = findViewById(R.id.pay_using_apps);

        // Set the Spinner background color to white
        spinnerBanks.setBackgroundColor(getResources().getColor(R.color.white));

        // Create a custom adapter for the Spinner
        CustomSpinnerAdapter customAdapter = new CustomSpinnerAdapter(this, bankLogos, bankNames);
        spinnerBanks.setAdapter(customAdapter);

        // Handle item selection in the Spinner
        spinnerBanks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBankName = bankNames[position]; // Capture the selected bank name
                if (selectedBankName.equals("Others Bank")) {
                    bankNameEditText.setVisibility(View.VISIBLE);
                    bankNameEditText.requestFocus(); // Focus on EditText
                    bankTextView.setVisibility(View.GONE); // Hide bank text view

                    // Adjust margin bottom of Spinner
                    adjustSpinnerMarginBottom(10); // Set margin to 10sp
                } else {
                    bankNameEditText.setVisibility(View.GONE);
                    bankTextView.setVisibility(View.VISIBLE);

                    // Reset margin bottom of Spinner
                    adjustSpinnerMarginBottom(150); // Set default margin to 150sp
                }

                // Always show bn_txt TextView
                bankTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedBankName = null;
            }
        });

        // Handle button click to proceed to the payment activity
        Button payButton = findViewById(R.id.pay_btn);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered phone number
                String phoneNumber = phoneEditText.getText().toString().trim();

                // Check if phone number is empty
                if (phoneNumber.isEmpty()) {
                    Toast.makeText(bank.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if phone number length is exactly 10 digits
                if (phoneNumber.length() != 10) {
                    Toast.makeText(bank.this, "Phone number must be exactly 10 digits", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if a bank is selected
                if (selectedBankName.equals("Select your bank ▼ ")) {
                    Toast.makeText(bank.this, "Please select your bank", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if "Others Bank" is selected and the EditText is visible
                if (selectedBankName.equals("Others Bank")) {
                    String enteredBankName = bankNameEditText.getText().toString().trim();
                    if (enteredBankName.isEmpty()) {
                        Toast.makeText(bank.this, "Please enter the name of your bank", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        selectedBankName = enteredBankName;
                    }
                }

                // Check internet connectivity before proceeding
                if (!isConnected(bank.this)) {
                    showNoInternetDialog();
                    return;
                }

                // Create an intent to start the pay activity
                Intent intent = new Intent(bank.this, pay.class);
                // Pass the selected bank name to the pay activity
                intent.putExtra("selectedBankName", selectedBankName);
                // Pass the phone number to the pay activity
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);
            }
        });

        // Handle RadioGroup checked change
        checkedBox.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.pay_using_apps) {
                    payUsingApps.setEnabled(false); // Disable the RadioButton when selected
                }
            }
        });

        // Initialize handler and runnable for internet checking
        handler = new Handler();
        checkInternetRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isConnected(bank.this)) {
                    if (alertDialog == null || !alertDialog.isShowing()) {
                        alertDialog = buildDialog(bank.this).show();
                    }
                } else {
                    if (alertDialog != null && alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                }
                handler.postDelayed(this, 2000);
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

    private void showNoInternetDialog() {
        if (alertDialog == null || !alertDialog.isShowing()) {
            alertDialog = buildDialog(bank.this).show();
        }
    }

    // Method to adjust margin bottom of Spinner dynamically
    private void adjustSpinnerMarginBottom(int marginBottom) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) spinnerBanks.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, marginBottom);
        spinnerBanks.setLayoutParams(params);
    }
}
