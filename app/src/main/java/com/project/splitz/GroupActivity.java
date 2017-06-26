package com.project.splitz;


import android.content.Intent;
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
import java.util.concurrent.Semaphore;

public class GroupActivity extends AppCompatActivity implements View.OnClickListener {

    private String GroupId;
    private String GroupName;
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

        GenerateListView();
        setTitle(GroupName);




    }

    private void GenerateListView() {
        final ArrayList<Items5> ExpenseDetailsList = new ArrayList<Items5>();
        DatabaseReference eDatabase = FirebaseDatabase.getInstance().getReference("expenses");
        Query GroupQuery = eDatabase.child(GroupId);
        GroupQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String ExpenseID = child.getKey();
                    Expenses expense = child.getValue(Expenses.class);
                    String ExpenseTitle = expense.title;
                    Float TotalAmount = expense.totalAmount;
                    String OwnerUID = expense.ownerUID;
                    String OwnerName = expense.ownerName;
                    String ExpenseDescription = expense.description;

                    ExpenseDetailsList.add(new Items5(ExpenseTitle, ExpenseDescription, OwnerUID, OwnerName, ExpenseID, TotalAmount));
//
                }
                generateAdapter(ExpenseDetailsList);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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
            Intent GroupInfoIntent = new Intent(this, GroupInfoActivity.class);
            Bundle b = new Bundle();
            b.putString("GroupId", GroupId);
            b.putString("GroupName", GroupName);
            GroupInfoIntent.putExtras(b);
            startActivity(GroupInfoIntent);
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onClick(View v) {
        int i= v.getId();

        if (i == R.id.NewExpenseBtn) {
            Intent myIntent = new Intent(GroupActivity.this, AddExpenseActivity.class);
            Bundle b = new Bundle();
            b.putString("GroupId", GroupId);
            b.putString("GroupName", GroupName);
            myIntent.putExtras(b);

            startActivity(myIntent);

        }
    }
}
