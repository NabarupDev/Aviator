package com.nabarup.avator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class upi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upi);

        Button payButton = findViewById(R.id.pay_btn);
        ProgressBar progressBar = findViewById(R.id.progrssbar);

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make the progress bar visible and the button invisible
                progressBar.setVisibility(View.VISIBLE);
                payButton.setVisibility(View.INVISIBLE);

                // Use a Handler to introduce a delay of 4 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // After 4 seconds, start the bank activity
                        Intent intent = new Intent(upi.this, bank.class);
                        startActivity(intent);
                    }
                }, 4000); // 4000 milliseconds = 4 seconds
            }
        });
    }
}
