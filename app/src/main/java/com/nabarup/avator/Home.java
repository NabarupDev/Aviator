package com.nabarup.avator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

public class Home extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver = null;
    GridView grid;
    ArrayList<Integer> arr_img = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        grid = findViewById(R.id.grid);

        arr_img.add(R.drawable.win);
        arr_img.add(R.drawable.bets);
        arr_img.add(R.drawable.casino);
        arr_img.add(R.drawable.premiur);
        arr_img.add(R.drawable.pin_up);
        arr_img.add(R.drawable.pari);
        arr_img.add(R.drawable.spribe);
        arr_img.add(R.drawable.ic_launcher_background);

        gridadapter adapter = new gridadapter();
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Check which image was clicked
                int imageResource = arr_img.get(position);
                if (imageResource == R.drawable.win) {
                    Intent intent = new Intent(Home.this, Predict.class);
                    startActivity(intent);
                } else if (imageResource == R.drawable.bets) {
                    Intent intent = new Intent(Home.this, Predict.class);
                    startActivity(intent);
                }else if (imageResource == R.drawable.casino) {
                    Intent intent = new Intent(Home.this, Predict.class);
                    startActivity(intent);
                }else if (imageResource == R.drawable.premiur) {
                    Intent intent = new Intent(Home.this, Predict.class);
                    startActivity(intent);
                }else if (imageResource == R.drawable.pari) {
                    Intent intent = new Intent(Home.this, Predict.class);
                    startActivity(intent);
                }else if (imageResource == R.drawable.spribe) {
                    Intent intent = new Intent(Home.this, Predict.class);
                    startActivity(intent);
                }else if (imageResource == R.drawable.ic_launcher_background) {
                    Intent intent = new Intent(Home.this, Predict.class);
                    startActivity(intent);
                }else if (imageResource == R.drawable.pin_up) {
                    Intent intent = new Intent(Home.this, Predict.class);
                    startActivity(intent);
                }
            }
        });
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
