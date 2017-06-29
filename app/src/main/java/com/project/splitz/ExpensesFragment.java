package com.project.splitz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ExpensesFragment extends Fragment {

    public FirebaseAuth mAuth;
    public ListView ListViewExpenses;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expenses, container, false);

        //Expense View
        ListViewExpenses = (ListView) rootView.findViewById(R.id.listViewExpenses);

        //Initialize Auth
        mAuth = FirebaseAuth.getInstance();

        //Find Current User
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);


        return rootView;

    }

    private void updateUI(final FirebaseUser currentUser) {
        DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users");
        Query UserQuery = uDatabase.child(currentUser.getUid());
        UserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.Expenses != null){
                    Map<String, String> ExpenseList = user.Expenses;
                    GenerateExpenseData(ExpenseList, currentUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void GenerateExpenseData(Map<String, String> ExpenseList, final FirebaseUser currentUser){
        final ArrayList<ExpenseFragmentItems> ExpenseDataList = new ArrayList<ExpenseFragmentItems>();

        for (Map.Entry<String, String> Expense:  ExpenseList.entrySet()){
            final String GroupID = Expense.getValue();
            final String ExpenseID = Expense.getKey();
            DatabaseReference eDatabase = FirebaseDatabase.getInstance().getReference("expenses");
            Query ExpenseQuery = eDatabase.child(GroupID).child(ExpenseID);
            ExpenseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Expenses expense = dataSnapshot.getValue(Expenses.class);
                    String title = expense.title;
                    String OwnerUID = expense.ownerUID;
                    String OwnerName = expense.ownerName;
                    Float TotalAmount = expense.totalAmount;
                    String Description = expense.description;
                    String GroupName = expense.GroupName;
                    String currentUid = currentUser.getUid();
                    Float Amount = expense.payers.get(currentUid);
                    ExpenseDataList.add(new ExpenseFragmentItems(title, Description, OwnerUID, OwnerName, ExpenseID, TotalAmount, GroupID, GroupName, Amount));
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