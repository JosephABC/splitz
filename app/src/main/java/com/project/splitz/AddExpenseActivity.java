package com.project.splitz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddExpenseActivity extends AppCompatActivity implements View.OnClickListener {

    private String Title;
    private String Description;
    private float Amount;
    public FirebaseAuth mAuth;
    private String GroupId;
    private String currentUid;
    public ArrayList<String> PayerUidList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        findViewById(R.id.AddExpenseBtn).setOnClickListener(this);
        EditText titleField = (EditText) findViewById(R.id.ExpenseTitleET);
        EditText descriptionField = (EditText) findViewById(R.id.ExpenseDescriptionET);
        EditText amountField = (EditText) findViewById(R.id.ExpenseAmountET);
        Title = titleField.getText().toString();
        Description = descriptionField.getText().toString();
        Amount = Float.valueOf(amountField.getText().toString());

        //Handle bundle extras
        Bundle b = getIntent().getExtras();
        GroupId = b.getCharSequence("GroupId").toString();
    }

    @Override
    public void onClick(View v) {
        addExpense();
    }

    protected void addExpense() {

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUid = currentUser.getUid();

        DatabaseReference eDatabase = FirebaseDatabase.getInstance().getReference("expenses");
        String expenseId = eDatabase.push().getKey();

        // Error because still need the list of payers for last argument
        Expenses expense = new Expenses(Title, Description, Amount, currentUid, GroupId);





    }

    // Generate list of users from group
    protected void generatePayers() {

        DatabaseReference gDatabase = FirebaseDatabase.getInstance().getReference("groups");
        Query UserQuery = gDatabase.child(GroupId).child("participants");
        UserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String Entry = child.getValue().toString();
                    PayerUidList.add(Entry);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
}
