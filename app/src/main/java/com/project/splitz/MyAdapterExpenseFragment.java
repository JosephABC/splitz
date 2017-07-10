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

public class MyAdapterExpenseFragment extends ArrayAdapter<ExpenseFragmentItems> {

    private final Context context;
    private final ArrayList<ExpenseFragmentItems> itemsArrayList;

    public MyAdapterExpenseFragment(Context context, ArrayList<ExpenseFragmentItems> itemsArrayList) {

        super(context, R.layout.row_expense_fragment, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.row_expense_fragment, parent, false);

        // 3. Get the two text view from the rowView
        TextView TitleView = (TextView) rowView.findViewById(R.id.title_expenses);
        TextView GroupView = (TextView) rowView.findViewById(R.id.Group_expenses);
        TextView OwnerView = (TextView) rowView.findViewById(R.id.Owner_expenses);
        TextView AmountView = (TextView) rowView.findViewById(R.id.Amount_expenses);

        // 4. Set the text for textView
        TitleView.setText(itemsArrayList.get(position).getTitle()+ " | " + itemsArrayList.get(position).getGroupCurrencyID() + " $ " + String.format("%.2f",itemsArrayList.get(position).getTotalAmount()));
        GroupView.setText("Group: " + itemsArrayList.get(position).getGroupName());
        OwnerView.setText("Owner: " + itemsArrayList.get(position).getOwnerName());
        AmountView.setText(String.format("%.2f",itemsArrayList.get(position).getAmount())+ " " + itemsArrayList.get(position).getGroupCurrencyID());

        // 5. return rowView
        return rowView;
    }
}

