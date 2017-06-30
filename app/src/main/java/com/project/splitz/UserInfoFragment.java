package com.project.splitz;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class UserInfoFragment extends Fragment {
    public FirebaseAuth mAuth;
    public ListView ListViewExpenses;
    public TextView TextViewAmountMsg;
    public String GroupId;
    public String GroupName;
    public String currentUID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_info, container, false);

        // Group view
        ListViewExpenses = (ListView) rootView.findViewById(R.id.listViewExpenses);
        TextViewAmountMsg = (TextView) rootView.findViewById(R.id.TextViewAmountMsg);

        //Initialize Auth
        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();

        //Handle Bundle Extras
        Bundle b = getActivity().getIntent().getExtras();
        GroupId = b.getCharSequence("GroupId").toString();
        GroupName = b.getCharSequence("GroupName").toString();

        updateUI();

        return rootView;
    }

    public void updateUI(){
        DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users").child(currentUID);
        Query UserQuery = uDatabase;
        UserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                List<String> ExpenseIDList = new ArrayList<String>();
                if (currentUser.Expenses != null){
                    ExpenseIDList = currentUser.Expenses;
                    GenerateGroupExpenses(ExpenseIDList);
                }else{
                    return;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void GenerateGroupExpenses(final List<String> ExpenseIDList){
        DatabaseReference gDatabase = FirebaseDatabase.getInstance().getReference("groups").child(GroupId);
        Query GroupQuery = gDatabase;
        GroupQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Groups group = dataSnapshot.getValue(Groups.class);
                if (group.Expenses != null){
                    List<String> GroupExpensesIDList = group.Expenses;
                    List<String> List1 = new ArrayList<String>(ExpenseIDList);
                    List1.removeAll(GroupExpensesIDList);
                    ExpenseIDList.removeAll(List1);
                    GenerateExpenseData(ExpenseIDList);
                }else{
                    String msg1 = "You Owe Nothing";
                    TextViewAmountMsg.setText(msg1);
                }
                Map<String, Float> Participants = group.participants;
                Float Amount = Participants.get(currentUID);
                String msg;
                if (Amount > 0){
                    msg = "You Should Receive $";
                    TextViewAmountMsg.setText(msg + String.format("%.2f", Amount));
                }else if(Amount < 0){
                    msg = "You Owe $";
                    Amount = Math.abs(Amount);
                    TextViewAmountMsg.setText(msg + String.format("%.2f", Amount));
                }else{
                    msg = "You Owe Nothing";
                    TextViewAmountMsg.setText(msg);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void GenerateExpenseData(List<String> ExpenseIDList){
        final ArrayList<ExpenseFragmentItems> ExpenseDataList = new ArrayList<ExpenseFragmentItems>();
        for (final String ExpenseID : ExpenseIDList){
            DatabaseReference eDatabase = FirebaseDatabase.getInstance().getReference("expenses").child(ExpenseID);
            Query ExpenseQuery = eDatabase;
            ExpenseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Expenses expense = dataSnapshot.getValue(Expenses.class);
                    String title = expense.title;
                    String ownerID = expense.ownerUID;
                    String ownerName = expense.ownerName;
                    String description = expense.description;
                    Float totalAmount = expense.totalAmount;
                    Map<String, Float> payers = expense.payers;
                    Float Amount;
                    if (payers.get(currentUID) != null){
                        Amount = 0f - payers.get(currentUID);
                    }else {
                        Amount = 0f;
                    }

                    if (ownerID.equals(currentUID)) {
                        Amount += totalAmount;
                    }
                    ExpenseDataList.add(new ExpenseFragmentItems(title, description, ownerID, ownerName, ExpenseID, totalAmount, GroupId, GroupName, Amount));
                    generate(ExpenseDataList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    public void generate(ArrayList<ExpenseFragmentItems> ExpenseDataList){
        final MyAdapterExpenseFragment eAdapter = new MyAdapterExpenseFragment(getActivity(), ExpenseDataList);
        ListViewExpenses.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ListViewExpenses.setAdapter(eAdapter);
        ListViewExpenses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent myIntent = new Intent(getActivity(), ExpenseActivity.class);

                Bundle b = new Bundle();
                b.putString("Title",eAdapter.getItem(position).getTitle());
                b.putString("Description", eAdapter.getItem(position).getDescription());
                b.putString("OwnerUID", eAdapter.getItem(position).getOwnerUID());
                b.putString("OwnerName", eAdapter.getItem(position).getOwnerName());
                b.putString("ExpenseID", eAdapter.getItem(position).getID());
                b.putFloat("TotalAmount", eAdapter.getItem(position).getTotalAmount());
                b.putString("GroupID",eAdapter.getItem(position).getGroupID());
                myIntent.putExtras(b);

                startActivity(myIntent);

            }
        });
    }
}






