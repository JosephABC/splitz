package com.project.splitz;

import java.util.ArrayList;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.TextView;

public class MyAdapterMembers extends ArrayAdapter<Items> {

    private final Context context;
    private final ArrayList<Items> itemsArrayList;

    public MyAdapterMembers(Context context, ArrayList<Items> itemsArrayList) {

        super(context, android.R.layout.simple_list_item_multiple_choice, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);

        // 3. Get the two text view from the rowView
        TextView labelView = (TextView) rowView.findViewById(android.R.id.text1);

        // 4. Set the text for textView
        labelView.setText(itemsArrayList.get(position).getTitle());



        // 5. return rowView
        return rowView;
    }
}
