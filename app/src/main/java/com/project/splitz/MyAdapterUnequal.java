package com.project.splitz;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import static com.project.splitz.R.id.Amount;
import static com.project.splitz.R.id.UserAmountET;
import static com.project.splitz.R.id.UserEndAmountTV;

public class MyAdapterUnequal extends ArrayAdapter<ItemUnequal> {

    private final Context context;
    private final ArrayList<ItemUnequal> itemsArrayList;
    boolean[] checkBoxState;
    boolean[] AllFalseState;
    boolean AllUnchecked = false;
    Float AmountUndistributed;

    public MyAdapterUnequal(Context context, ArrayList<ItemUnequal> itemsArrayList){
        super(context, R.layout.row_unequal, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
        checkBoxState= new boolean[itemsArrayList.size()];
        Arrays.fill(checkBoxState,true);
        AllFalseState = new boolean[itemsArrayList.size()];
        Arrays.fill(AllFalseState, false);
        AmountUndistributed = itemsArrayList.get(0).getOriginalAmount();
    }

    @Override
    public View getView(final int position, View covertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.row_unequal, parent, false);
        // 3. Get the two text view from the rowView

        TextView UserNameTV = (TextView) rowView.findViewById(R.id.UserNameTV);
        TextView UserEmailTV = (TextView) rowView.findViewById(R.id.UserEmailTV);
        TextView OriginalCurrencyTV = (TextView) rowView.findViewById(R.id.UserOriginalCurrencyTV);
        final EditText UserAmountET = (EditText) rowView.findViewById(R.id.UserAmountET);
        TextView EndCurrencyTV = (TextView) rowView.findViewById(R.id.UserEndCurrencyTV);
        final TextView UserEndAmountTV = (TextView) rowView.findViewById(R.id.UserEndAmountTV);

        CheckBox cb = (CheckBox) rowView.findViewById(R.id.checkbox);
        cb.setChecked(checkBoxState[position]);
        UserAmountET.setEnabled(!checkBoxState[position]);
        cb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if(((CheckBox)v).isChecked())
                {
                    checkBoxState[position]=true;
                    UserAmountET.setEnabled(false);
                    Recalculate();
                    notifyDataSetChanged();
                    AllUnchecked = false;
                }
                else
                {
                    checkBoxState[position]=false;
                    UserAmountET.setEnabled(true);
                    AllUnchecked = Arrays.equals(checkBoxState,AllFalseState);

                }
            }
        });



        // 4. Set the text for textView
        UserNameTV.setText(itemsArrayList.get(position).getUserName());
        UserEmailTV.setText(itemsArrayList.get(position).getUserEmail());
        OriginalCurrencyTV.setText(itemsArrayList.get(position).getOriginalCurrency());
        UserAmountET.setText(String.format("%.2f", itemsArrayList.get(position).getOriginalAmount()));
        UserAmountET.setSelection(UserAmountET.getText().length());
        EndCurrencyTV.setText(itemsArrayList.get(position).getEndCurrency() + ")");
        UserEndAmountTV.setText(" (" + String.format("%.2f", itemsArrayList.get(position).getEndAmount()));

        UserAmountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Float MaxAmount = ValidateEntry(position);
                if (AllUnchecked){
                    if (TextUtils.isEmpty(UserAmountET.getText().toString())) {
                        Toast.makeText(getContext(), "Please Enter an Amount", Toast.LENGTH_SHORT).show();
                    }else if (Float.valueOf(UserAmountET.getText().toString()) > itemsArrayList.get(0).getTotalAmount()){
                        UserAmountET.setText(String.format("%.2f", itemsArrayList.get(0).getTotalAmount()));
                        Toast.makeText(getContext(), "That is over the Total Amount", Toast.LENGTH_SHORT).show();
                        Float OriginalAmount = Float.valueOf(UserAmountET.getText().toString());
                        Float EndAmount = OriginalAmount * itemsArrayList.get(position).getExchangeRate();
                        itemsArrayList.get(position).setOriginalAmount(OriginalAmount);
                        itemsArrayList.get(position).setEndAmount(EndAmount);
                        UserEndAmountTV.setText(String.format("%.2f", itemsArrayList.get(position).getEndAmount()));
                    }else if(Float.valueOf(UserAmountET.getText().toString()) > MaxAmount){
                        Toast.makeText(getContext(), "Please Double Check on Amounts", Toast.LENGTH_SHORT).show();
                        Float OriginalAmount = Float.valueOf(UserAmountET.getText().toString());
                        Float EndAmount = OriginalAmount * itemsArrayList.get(position).getExchangeRate();
                        itemsArrayList.get(position).setOriginalAmount(OriginalAmount);
                        itemsArrayList.get(position).setEndAmount(EndAmount);
                        UserEndAmountTV.setText(String.format("%.2f", itemsArrayList.get(position).getEndAmount()));
                    }else {
                        Float OriginalAmount = Float.valueOf(UserAmountET.getText().toString());
                        Float EndAmount = OriginalAmount * itemsArrayList.get(position).getExchangeRate();
                        itemsArrayList.get(position).setOriginalAmount(OriginalAmount);
                        itemsArrayList.get(position).setEndAmount(EndAmount);
                        UserEndAmountTV.setText(String.format("%.2f", itemsArrayList.get(position).getEndAmount()));
                    }

                }else{
                    if (TextUtils.isEmpty(UserAmountET.getText().toString())) {
                        Toast.makeText(getContext(), "Please Enter an Amount", Toast.LENGTH_SHORT).show();
                    }else if(Float.valueOf(UserAmountET.getText().toString()) > MaxAmount){
                        UserAmountET.setText(String.format("%.2f", MaxAmount));
                        Toast.makeText(getContext(), "Please Enter a Lower Amount \n This is the Maximum Value", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Float OriginalAmount = Float.valueOf(UserAmountET.getText().toString());
                        Float EndAmount = OriginalAmount * itemsArrayList.get(position).getExchangeRate();
                        itemsArrayList.get(position).setOriginalAmount(OriginalAmount);
                        itemsArrayList.get(position).setEndAmount(EndAmount);
                        UserEndAmountTV.setText(String.format("%.2f", itemsArrayList.get(position).getEndAmount()));
                        Recalculate();
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // 5. return rowView
        return rowView;

    }

    public Float ValidateEntry(int position){
        Float AmountLeft = itemsArrayList.get(0).getTotalAmount();
        for (int p =0; p < checkBoxState.length; p++){
            if (!checkBoxState[p] && p != position){
                AmountLeft = AmountLeft - itemsArrayList.get(p).getOriginalAmount();
            }
        }
        return AmountLeft;
    }
    public void Recalculate(){
        Float TotalAmount = itemsArrayList.get(0).getTotalAmount();
        Float AmountLeft = TotalAmount;
        int UncheckedNum = 0;
        for (int position = 0; position < checkBoxState.length; position++){
            if (checkBoxState[position]==false){
                UncheckedNum++;
                AmountLeft = AmountLeft - itemsArrayList.get(position).getOriginalAmount();
            }else{

            }
        }
        Float SplitAmount = EqualSplitCalc(AmountLeft, checkBoxState.length - UncheckedNum);
        Float SplitEndAmount = SplitAmount * itemsArrayList.get(0).getExchangeRate();
        for (int position = 0; position < checkBoxState.length; position++){
            if (checkBoxState[position]==true){
                itemsArrayList.get(position).setOriginalAmount(SplitAmount);
                itemsArrayList.get(position).setEndAmount(SplitEndAmount);
            }else{

            }
        }
    }
    protected Float EqualSplitCalc(Float TotalAmount, int PayerNumbers) {
        Float EachAmount = format(TotalAmount / PayerNumbers);
        return EachAmount;
    }
    protected Float format(Float n) {
        Float fn = Math.round(n * 100.00f) / 100.00f;
        return fn;
    }

}
