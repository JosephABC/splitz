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

public class MyAdapterExpenses extends ArrayAdapter<Items3> {

    private final Context context;
    private final ArrayList<Items3> itemsArrayList;

    public MyAdapterExpenses(Context context, ArrayList<Items3> itemsArrayList) {

        super(context, R.layout.row_expenses, itemsArrayList);

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
        TextView labelView = (TextView) rowView.findViewById(R.id.label_groups);
        TextView valueView = (TextView) rowView.findViewById(R.id.value_groups);

        // 4. Set the text for textView
        labelView.setText(itemsArrayList.get(position).getTitle());
        valueView.setText(itemsArrayList.get(position).getDescription());

        // 5. return rowView
        return rowView;
    }
}
