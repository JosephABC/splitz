package com.project.splitz;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyAdapterGroupInfo extends ArrayAdapter<ItemsUserInfo> {

    private final Context context;
    private final ArrayList<ItemsUserInfo> itemsArrayList;

    public MyAdapterGroupInfo(Context context, ArrayList<ItemsUserInfo> itemsArrayList) {

        super(context, R.layout.row_group_info, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.row_group_info, parent, false);

        // 3. Get the two text view from the rowView
        TextView nameView = (TextView) rowView.findViewById(R.id.nameTV);
        TextView emailView = (TextView) rowView.findViewById(R.id.emailTV);
        TextView amountView = (TextView) rowView.findViewById(R.id.amountTV);

        // 4. Set the text for textView
        nameView.setText(itemsArrayList.get(position).getName());
        emailView.setText(itemsArrayList.get(position).getEmail());

        amountView.setText(itemsArrayList.get(position).getCurrencyID() + " $ " + String.format("%.2f", itemsArrayList.get(position).getAmount() ));

        // 5. return rowView
        return rowView;
    }
}

