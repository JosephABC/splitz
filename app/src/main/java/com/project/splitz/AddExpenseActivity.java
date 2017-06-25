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

import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddExpenseActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listViewMembers;
    private String GroupId;
    private String GroupName;
    private String currentUid;
    public ArrayList<String> MembersUidList = new ArrayList<String>();
    private MyAdapterMembers adapter;

    public FirebaseAuth mAuth;

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

        final String title = titleField.getText().toString();
        final String description = descriptionField.getText().toString();
        final Float totalAmount = Float.valueOf(amountField.getText().toString());

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUid = currentUser.getUid();

        //Get Owner UserName from database
        DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users");
        Query UserNameQuery = uDatabase.child(currentUid);
        UserNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String OwnerName = user.Name;

                // Get emails of selected members
                ArrayList<String> selectedMembers = GeneratePayers();

                //Equal Splitting Calculation
                Float EachAmount = EqualSplitCalc(totalAmount, selectedMembers.size());

                // Add expense to expense database
                DatabaseReference eDatabase = FirebaseDatabase.getInstance().getReference("expenses");
                String expenseId = eDatabase.push().getKey();
                Map<String, Float> selectedMembersData = new HashMap<String, Float>();
                for (String member : selectedMembers){
                    selectedMembersData.put(member, EachAmount);
                }
                Expenses expense = new Expenses(title, description, totalAmount, currentUid, OwnerName, selectedMembersData);
                eDatabase.child(GroupId).child(expenseId).setValue(expense);
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                    String OwnerName = child.child("Name").getValue().toString();
//
//                    // Get emails of selected members
//                    ArrayList<String> selectedMembers = GeneratePayers();
//
//                    // Add expense to expense database
//                    DatabaseReference eDatabase = FirebaseDatabase.getInstance().getReference("expenses");
//                    String expenseId = eDatabase.push().getKey();
//                    Expenses expense = new Expenses(title, description, totalAmount, currentUid, OwnerName, selectedMembers);
//                    eDatabase.child(GroupId).child(expenseId).setValue(expense);
//
//
//                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    protected Float EqualSplitCalc(Float TotalAmount, int PayerNumbers){
        Float EachAmount = TotalAmount/PayerNumbers;
        return EachAmount;
    }
    protected ArrayList<String> GeneratePayers() {
        //Check Which Friends are selected
        SparseBooleanArray checked = listViewMembers.getCheckedItemPositions();

        ArrayList<String> selectedMembers = new ArrayList<String>();
        for (int c = 0; c < checked.size(); c++) {

            int position = checked.keyAt(c);
            if (checked.valueAt(c)) {

                selectedMembers.add(adapter.getItem(position).getDescription());


            }
        }
        return selectedMembers;
    }

    // Generate list of payers from group
    protected void GenerateMembers() {
        DatabaseReference gDatabase = FirebaseDatabase.getInstance().getReference("groups");
        Query UserQuery = gDatabase.child(GroupId).child("participants");
        UserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String Entry = child.getKey().toString();
                    MembersUidList.add(Entry);
                }

                // Retrieve name data
                final ArrayList<Items> MembersDataList = new ArrayList<Items>();
                for (final String uid: MembersUidList){
                    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users");
                    Query UserQuery2 = uDatabase.orderByKey().equalTo(uid);
                    UserQuery2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                String Name = child.child("Name").getValue().toString();
                                MembersDataList.add(new Items(Name,uid));
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


