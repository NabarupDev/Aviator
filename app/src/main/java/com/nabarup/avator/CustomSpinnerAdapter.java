package com.nabarup.avator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private Context context;
    private int[] bankLogos;
    private String[] bankNames;

    public CustomSpinnerAdapter(Context context, int[] bankLogos, String[] bankNames) {
        super(context, R.layout.spinner_item, bankNames);
        this.context = context;
        this.bankLogos = bankLogos;
        this.bankNames = bankNames;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.text);
        textView.setText(bankNames[position]);
        textView.setTextColor(context.getResources().getColor(R.color.black)); // Ensure text color is black

        ImageView imageView = convertView.findViewById(R.id.image);
        imageView.setImageResource(bankLogos[position]);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.custom_spinner_dropdown_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.textView);
        textView.setText(bankNames[position]);
        textView.setTextColor(context.getResources().getColor(R.color.black)); // Ensure text color is black

        ImageView imageView = convertView.findViewById(R.id.imageView);
        imageView.setImageResource(bankLogos[position]);

        return convertView;
    }
}

