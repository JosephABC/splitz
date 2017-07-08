package com.project.splitz;


import android.content.Intent;
import android.icu.text.MessagePattern;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.security.acl.Group;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class GroupActivity extends AppCompatActivity implements View.OnClickListener {

    private String GroupId;
    private String GroupName;
    private String GroupCurrencyID;
    public ListView ListViewExpenses;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        findViewById(R.id.NewExpenseBtn).setOnClickListener(this);
        ListViewExpenses = (ListView) findViewById(R.id.listViewExpenses);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Handle bundle extras
        Bundle b = getIntent().getExtras();
        GroupId = b.getCharSequence("GroupId").toString();
        GroupName = b.getCharSequence("GroupName").toString();
        GroupCurrencyID = b.getCharSequence("GroupCurrencyID").toString();

        GenerateExpenseList();
        setTitle(GroupName);




    }
    private void GenerateExpenseList(){
        DatabaseReference gDatabase = FirebaseDatabase.getInstance().getReference("groups").child(GroupId);
        Query GroupQuery = gDatabase;
        GroupQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Groups group = dataSnapshot.getValue(Groups.class);
                if (group.Expenses != null){
                    List<String> ExpensesIDList = group.Expenses;
                    GroupCurrencyID = group.CurrencyID;
                    GenerateListView(ExpensesIDList, GroupCurrencyID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void GenerateListView(List<String> ExpensesIDList, final String GroupCurrencyID) {
        final ArrayList<Items5> ExpenseDetailsList = new ArrayList<Items5>();
        for (final String ExpenseID : ExpensesIDList){
            DatabaseReference eDatabase = FirebaseDatabase.getInstance().getReference("expenses");
            Query ExpenseQuery = eDatabase.child(ExpenseID);
            ExpenseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Expenses expense = dataSnapshot.getValue(Expenses.class);
                    String ExpenseTitle = expense.title;
                    Float TotalAmount = expense.totalAmount;
                    String OwnerUID = expense.ownerUID;
                    String OwnerName = expense.ownerName;
                    String ExpenseDescription = expense.description;
                    ExpenseDetailsList.add(new Items5(ExpenseTitle, ExpenseDescription, OwnerUID, OwnerName, ExpenseID, TotalAmount, GroupCurrencyID));
                    generateAdapter(ExpenseDetailsList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    public void generateAdapter(ArrayList<Items5> ExpenseDetailsList){
        final MyAdapterExpenses adapter = new MyAdapterExpenses(this, ExpenseDetailsList);
        ListViewExpenses.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ListViewExpenses.setAdapter(adapter);
        ListViewExpenses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent myIntent = new Intent(GroupActivity.this, ExpenseActivity.class);

                Bundle b = new Bundle();
                b.putString("Title",adapter.getItem(position).getTitle());
                b.putString("Description", adapter.getItem(position).getDescription());
                b.putString("OwnerUID", adapter.getItem(position).getOwnerUID());
                b.putString("OwnerName", adapter.getItem(position).getOwnerName());
                b.putString("ExpenseID", adapter.getItem(position).getID());
                b.putFloat("TotalAmount", adapter.getItem(position).getTotalAmount());
                b.putString("GroupCurrencyID", adapter.getItem(position).getGroupCurrencyID());
                b.putString("GroupID",GroupId);
                myIntent.putExtras(b);

                startActivity(myIntent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return true;
    }

    @Override
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
        }else if (id == R.id.GroupInfo){
            Intent GroupInfoIntent = new Intent(this, GroupInfoTabActivity.class);
            Bundle b = new Bundle();
            b.putString("GroupId", GroupId);
            b.putString("GroupName", GroupName);
            GroupInfoIntent.putExtras(b);
            startActivity(GroupInfoIntent);
        }else if (id == R.id.Delete){
            final DatabaseReference gDatabase = FirebaseDatabase.getInstance().getReference("groups").child(GroupId);
            Query GroupQuery = gDatabase;
            GroupQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Groups group = dataSnapshot.getValue(Groups.class);
                    List<String> ExpenseIDList = null;
                    Map<String, Float> ParticipantsData = group.participants;
                    List<String> ParticipantsIDList = new ArrayList<String>(ParticipantsData.keySet());
                    if (group.Expenses != null){
                        ExpenseIDList = group.Expenses;
                    }
                    RemoveParticipants(ParticipantsIDList, ExpenseIDList);
                    gDatabase.removeValue();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    public void RemoveParticipants(List<String> ParticipantsIDLIst, final List<String> ExpensesIDList){
        final DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users");
        for (final String ParticipantsID : ParticipantsIDLIst){
            Query UserQuery = uDatabase.child(ParticipantsID);
            UserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    List<String> GroupIDList = new ArrayList<String>(user.groups);
                    GroupIDList.remove(GroupId);
                    uDatabase.child(ParticipantsID).child("groups").setValue(GroupIDList);
                    if (ExpensesIDList != null && user.Expenses != null){
                        List<String> UserExpensesIDList = new ArrayList<String>(user.Expenses);
                        UserExpensesIDList.removeAll(ExpensesIDList);
                        uDatabase.child(ParticipantsID).child("Expenses").setValue(UserExpensesIDList);


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        if (ExpensesIDList != null){
            DatabaseReference eDatabase = FirebaseDatabase.getInstance().getReference("expenses");
            for (String ExpensesID : ExpensesIDList){
                eDatabase.child(ExpensesID).removeValue();
            }
        }
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        Intent parentIntent = NavUtils.getParentActivityIntent(GroupActivity.this);
                        if(parentIntent == null) {
                            finish();
                        } else {
                            parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(parentIntent);
                            finish();
                        }
                    }
                },
                1000
        );

    }

    public void RemovePayer(List<String> PayerIDList, final String ExpenseID){
        final DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users");
        for (final String PayerID : PayerIDList){
            Query UserQuery = uDatabase.child(PayerID);
            UserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    List<String> ExpensesIDList = new ArrayList<String>(user.Expenses);
                    ExpensesIDList.remove(ExpenseID);
                    uDatabase.child(PayerID).child("Expenses").setValue(ExpensesIDList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        int i= v.getId();

        if (i == R.id.NewExpenseBtn) {
            Intent myIntent = new Intent(GroupActivity.this, AddExpenseActivity.class);
            Bundle b = new Bundle();
            b.putString("GroupId", GroupId);
            b.putString("GroupName", GroupName);
            b.putString("GroupCurrencyID", GroupCurrencyID);
            myIntent.putExtras(b);

            startActivity(myIntent);

        }
    }
}
