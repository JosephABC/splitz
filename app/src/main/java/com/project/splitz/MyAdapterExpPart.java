package com.project.splitz;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyAdapterExpPart extends ArrayAdapter<Items> {

    private final Context context;
    private final ArrayList<Items> itemsArrayList;

    public MyAdapterExpPart(Context context, ArrayList<Items> itemsArrayList) {

        super(context, R.layout.row, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.row_exppart, parent, false);

        // 3. Get the two text view from the rowView
        TextView nameView = (TextView) rowView.findViewById(R.id.name_exp);
        TextView emailView = (TextView) rowView.findViewById(R.id.email_exp);

        // 4. Set the text for textView
        nameView.setText(itemsArrayList.get(position).getTitle());
        emailView.setText(itemsArrayList.get(position).getDescription());

        // 5. return rowView
        return rowView;
    }
}
