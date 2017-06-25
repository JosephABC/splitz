package com.project.splitz;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.List;
import java.util.Map;

public class ExpenseActivity extends AppCompatActivity {
    public String Title;
    public String OwnerUID;
    public String OwnerName;
    public String ExpenseID;
    public Float TotalAmount;
    public String GroupID;

    public TextView OwnerNameTV;
    public TextView ExpenseDescriptionTV;
    public TextView TotalAmountTV;
    public ListView ParticipantsListView;

    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Views
        OwnerNameTV = (TextView) findViewById(R.id.OwnerName);
        ExpenseDescriptionTV = (TextView) findViewById(R.id.ExpenseDescription);
        TotalAmountTV = (TextView) findViewById(R.id.Amount);
        ParticipantsListView= (ListView) findViewById(R.id.listViewParticipants);

        //Handle Bundle Extras
        Bundle b = getIntent().getExtras();
        Title = b.getCharSequence("Title").toString();
        OwnerUID= b.getCharSequence("OwnerUID").toString();
        OwnerName= b.getCharSequence("OwnerName").toString();
        ExpenseID= b.getCharSequence("ExpenseID").toString();
        TotalAmount= b.getFloat("TotalAmount");
        GroupID = b.getCharSequence("GroupID").toString();

        //Populating Views
        OwnerNameTV.setText("Expense Paid By: " + OwnerName);
        TotalAmountTV.setText("Total Amount: $" + TotalAmount);
        setTitle(Title);
        updateUI(ExpenseID, GroupID);

    }

    public void updateUI(String ExpenseID, String GroupID){
        mAuth = FirebaseAuth.getInstance();

        final DatabaseReference eDatabase = FirebaseDatabase.getInstance().getReference("expenses");
        Query ExpenseQuery = eDatabase.child(GroupID).child(ExpenseID);
        ExpenseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Expenses expense = dataSnapshot.getValue(Expenses.class);
                ExpenseDescriptionTV.setText(expense.description);
                Map<String, Float> ParticipantsData = expense.payers;
                generateParticipantData(ParticipantsData);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void generateParticipantData(final Map<String, Float> ParticipantsDataList){
        final ArrayList<ItemsUserInfo> ParticipantsData = new ArrayList<ItemsUserInfo>();
        ArrayList<String> ParticipantsUIDList = new ArrayList<>(ParticipantsDataList.keySet());
        for (final String ParticipantUID : ParticipantsUIDList){
            mAuth = FirebaseAuth.getInstance();
            final DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users");
            Query UserQuery = uDatabase.child(ParticipantUID);
            UserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User participant= dataSnapshot.getValue(User.class);
                    String Email = participant.Email;
                    String Name = participant.Name;
                    ParticipantsData.add(new ItemsUserInfo(Name, Email, ParticipantsDataList.get(ParticipantUID)));
                    MyAdapterExpPart adapter = new MyAdapterExpPart(ExpenseActivity.this, ParticipantsData);
                    ParticipantsListView.setAdapter(adapter);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_expense, menu);
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
        }else if (id == R.id.Delete){
            if (checkIfOwner()){
                AlertDialog diaBox = AskOption();
                diaBox.show();
            }else {
                Toast.makeText(ExpenseActivity.this, "You are not the Owner of this Expense. Please request the Owner to delete!",
                        Toast.LENGTH_SHORT).show();
            }

        }else if (id == R.id.Edit){
            //add edit function
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean checkIfOwner(){
        boolean check = false;
        mAuth = FirebaseAuth.getInstance();
        String currentUserUid = mAuth.getCurrentUser().getUid();
        if (OwnerUID.equals(currentUserUid)){
            check = true;
        }
        return check;
    }
    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        delete();
                        dialog.dismiss();
                    }

                })

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }
    public void delete(){
        mAuth = FirebaseAuth.getInstance();

        //delete expense from expense database
        DatabaseReference eDatabase = FirebaseDatabase.getInstance().getReference("expenses");
        eDatabase.child(GroupID).child(ExpenseID).removeValue();

        //delete expense from group database
        DatabaseReference gDatabase = FirebaseDatabase.getInstance().getReference("groups");
        Query GroupQuery = gDatabase.child(GroupID);
        GroupQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Groups group = dataSnapshot.getValue(Groups.class);
                String GroupName = group.groupName;
                Intent myIntent = new Intent(ExpenseActivity.this, GroupActivity.class);
                Bundle b = new Bundle();
                b.putString("GroupId", GroupID);
                b.putString("GroupName", GroupName);
                myIntent.putExtras(b);

                startActivity(myIntent);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
