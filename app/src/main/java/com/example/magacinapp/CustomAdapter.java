package com.example.magacinapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;

    ArrayList<String> naziv;
    ArrayList<String> opis;
    ArrayList<String> vlasnik;

    public CustomAdapter(Context context, ArrayList<String> naziv, ArrayList<String> opis, ArrayList<String> vlasnik) {
        this.context = context;
        this.naziv = naziv;
        this.opis = opis;
        this.vlasnik = vlasnik;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return naziv.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.custom_list_data, null);
        TextView tvID = view.findViewById(R.id.tv_lv_ID);
        TextView tvName = view.findViewById(R.id.tv_lv_name);
        TextView tvCGPA = view.findViewById(R.id.tv_lv_cgpa);

        tvID.setText(naziv.get(i));
        tvName.setText(opis.get(i));
        tvCGPA.setText(vlasnik.get(i));
        return view;
    }
}

