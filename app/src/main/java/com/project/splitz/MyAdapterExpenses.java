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

public class MyAdapterExpenses extends ArrayAdapter<Items5> {

    private final Context context;
    private final ArrayList<Items5> itemsArrayList;

    public MyAdapterExpenses(Context context, ArrayList<Items5> itemsArrayList) {

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
        View rowView = inflater.inflate(R.layout.row_expenses, parent, false);

        // 3. Get the two text view from the rowView
        TextView TitleView = (TextView) rowView.findViewById(R.id.title_expenses);
        TextView OwnerView = (TextView) rowView.findViewById(R.id.Owner_expenses);

        // 4. Set the text for textView
        TitleView.setText(itemsArrayList.get(position).getTitle() + " | $ " + itemsArrayList.get(position).getTotalAmount().toString() );
        OwnerView.setText("Owner: " + itemsArrayList.get(position).getOwnerName());

        // 5. return rowView
        return rowView;
    }
}
