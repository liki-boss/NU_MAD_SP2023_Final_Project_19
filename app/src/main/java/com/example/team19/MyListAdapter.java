package com.example.team19;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyListAdapter extends ArrayAdapter<String> {

    public MyListAdapter(Context context, String[] values) {
        super(context, android.R.layout.simple_list_item_1, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getItem(position));
        return view;
    }
}
