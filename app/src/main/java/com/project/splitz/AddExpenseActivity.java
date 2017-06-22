package com.project.splitz;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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

    private ListView listViewMembers;
    private String GroupId;
    private String GroupName;
    private String currentUid;
    public ArrayList<String> MembersUidList = new ArrayList<String>();
    private MyAdapterMembers adapter;

    public FirebaseAuth mAuth;
    private DatabaseReference gDatabase = FirebaseDatabase.getInstance().getReference("groups");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        findViewById(R.id.AddExpenseBtn).setOnClickListener(this);
        listViewMembers = (ListView) findViewById(R.id.listViewMembers);

        //Handle bundle extras
        Bundle b = getIntent().getExtras();
        GroupId = b.getCharSequence("GroupId").toString();
        GroupName = b.getCharSequence("GroupName").toString();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Add Expense to " + GroupName);


        // Generate payer list options
        GenerateMembers();

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent parentIntent = NavUtils.getParentActivityIntent(this);
            if(parentIntent == null) {
                finish();
                return true;
            } else {
                parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(parentIntent);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        addExpense();

        Intent myIntent = new Intent(this, GroupActivity.class);
        Bundle b = new Bundle();
        b.putString("GroupId", GroupId);
        b.putString("GroupName", GroupName);
        myIntent.putExtras(b);
        startActivity(myIntent);
    }

    protected void addExpense() {
        EditText titleField = (EditText) findViewById(R.id.ExpenseTitleET);
        EditText descriptionField = (EditText) findViewById(R.id.ExpenseDescriptionET);
        EditText amountField = (EditText) findViewById(R.id.ExpenseAmountET);

        String title = titleField.getText().toString();
        String description = descriptionField.getText().toString();
        float amount = Float.valueOf(amountField.getText().toString());

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUid = currentUser.getUid();

        // Get emails of selected members
        ArrayList<String> selectedMembers = GeneratePayers();


        // Add expense to expense database
        DatabaseReference eDatabase = FirebaseDatabase.getInstance().getReference("expenses");
        String expenseId = eDatabase.push().getKey();
        Expenses expense = new Expenses(title, description, amount, currentUid, selectedMembers);
        eDatabase.child(GroupId).child(expenseId).setValue(expense);

        //Add expense to group database
        gDatabase.child(GroupId).child("expenses").child(expenseId).setValue(expense);

    }

    protected ArrayList<String> GeneratePayers() {
        //Check Which Friends are selected
        SparseBooleanArray checked = listViewMembers.getCheckedItemPositions();

        ArrayList<String> selectedMembers = new ArrayList<String>();
        for (int c = 0; c < checked.size(); c++) {

            int position = checked.keyAt(c);
            if (checked.valueAt(c)) {

                selectedMembers.add(adapter.getItem(position).getDescription());

                // either somehow only extract the id, or make selectedMembers an arraylist of "items" and retrieve the id later,.
            }
        }
        return selectedMembers;
    }

    // Generate list of payers from group
    protected void GenerateMembers() {

        Query UserQuery = gDatabase.child(GroupId).child("participants");
        UserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String Entry = child.getValue().toString();
                    MembersUidList.add(Entry);
                }

                // Retrieve email data
                final ArrayList<Items> MembersDataList = new ArrayList<Items>();
                for (final String uid: MembersUidList){
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
                    Query UserQuery2 = mDatabase.orderByKey().equalTo(uid);
                    UserQuery2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                String Email = child.child("Email").getValue().toString();
                                MembersDataList.add(new Items(Email,uid));
                            }
                            generate(MembersDataList);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    public void generate(ArrayList<Items> MembersDataList) {
        adapter = new MyAdapterMembers(this, MembersDataList);
        listViewMembers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listViewMembers.setAdapter(adapter);

    }
}


