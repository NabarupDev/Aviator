package com.nabarup.avator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class Home extends AppCompatActivity {

    GridView grid;
    ArrayList<Integer> arr_img = new ArrayList<>();
    TextView timerText;
    CountDownTimer countDownTimer;
    long endTimeMillis;
    public static boolean isTimerRunningInHome = false; // static variable to indicate timer state

    private Handler handler;
    private Runnable checkInternetRunnable;
    private AlertDialog alertDialog;

    private boolean doubleBackToExitPressedOnce = false; // Flag to track back button presses
    private Handler backPressHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        grid = findViewById(R.id.grid);
        timerText = findViewById(R.id.timer_text);

        arr_img.add(R.drawable.win);
        arr_img.add(R.drawable.bets);
        arr_img.add(R.drawable.casino);
        arr_img.add(R.drawable.premiur);
        arr_img.add(R.drawable.pin_up);
        arr_img.add(R.drawable.pari);
        arr_img.add(R.drawable.spribe);

        gridadapter adapter = new gridadapter();
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int imageResource = arr_img.get(position);
                Intent intent = new Intent(Home.this, Predict.class);
                startActivity(intent);
            }
        });

        // Load saved end time from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("com.nabarup.avator.PREFS", MODE_PRIVATE);
        endTimeMillis = sharedPreferences.getLong("END_TIME_KEY", 0);

        // Check if timer is running
        isTimerRunningInHome = (endTimeMillis > System.currentTimeMillis());

        // Start or resume countdown timer if there's remaining time
        if (isTimerRunningInHome) {
            startCountdown();
        }

        // Check internet connection in the background every 5 seconds
        handler = new Handler();
        checkInternetRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isConnected(Home.this)) {
                    if (alertDialog == null || !alertDialog.isShowing()) {
                        alertDialog = buildDialog(Home.this).show();
                    }
                } else {
                    if (alertDialog != null && alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                }
                handler.postDelayed(this, 3000);
            }
        };
        handler.post(checkInternetRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (handler != null && checkInternetRunnable != null) {
            handler.removeCallbacks(checkInternetRunnable);
        }
        if (backPressHandler != null) {
            backPressHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity(); // Close all activities and exit the app
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show();

        backPressHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000); // 2 seconds to reset the flag
    }

    private void startCountdown() {
        long currentTimeMillis = System.currentTimeMillis();
        long timeLeftInMillis = endTimeMillis - currentTimeMillis;

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Convert milliseconds to hours, minutes, and seconds
                int hours = (int) (millisUntilFinished / 3600000);
                int minutes = (int) ((millisUntilFinished % 3600000) / 60000);
                int seconds = (int) ((millisUntilFinished % 60000) / 1000);

                // Format the remaining time into "HH:MM:SS" or "MM:SS" if less than an hour
                String timeLeftFormatted;
                if (hours > 0) {
                    timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                } else {
                    timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                }

                // Update the TextView with remaining time
                timerText.setText(timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                // Optionally handle timer finish event
                timerText.setText("00:00");
                isTimerRunningInHome = false;
            }
        };

        countDownTimer.start();
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
                // You can perform any action upon clicking "Ok" here
                // For example, if you want to dismiss the dialog, you can do:
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

    class gridadapter extends BaseAdapter {

        @Override
        public int getCount() {
            return arr_img.size();
        }

        @Override
        public Object getItem(int i) {
            return arr_img.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = getLayoutInflater().inflate(R.layout.grid_view, viewGroup, false);
            ImageView imageView = view1.findViewById(R.id.images);
            imageView.setImageResource(arr_img.get(i));
            return view1;
        }
    }
}
