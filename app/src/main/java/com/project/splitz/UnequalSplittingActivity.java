package com.project.splitz;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.project.splitz.R.id.Amount;
import static com.project.splitz.R.id.listViewMembers;

public class UnequalSplittingActivity extends AppCompatActivity implements View.OnClickListener {
    public String GroupId;
    public String GroupName;
    public String GroupCurrencyID;
    public String CurrencyID;
    public String OwnerID;
    public String OwnerName;
    public String Title;
    public String Description;
    public Float OriginalAmount;
    public Float ExchangeRate;
    public List<String> SelectedMembers;
    public Float EndAmount;
    public MyAdapterUnequal adapter;
    public ArrayList<Integer> checkedPositions;

    public TextView OriginalAmountTV;
    public TextView EndAmountTV;
    public ListView ListViewMembers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unequal_splitting);

        //Handle Bundle Extras
        Bundle b = getIntent().getExtras();
        GroupId = b.getCharSequence("GroupId").toString();
        GroupName = b.getCharSequence("GroupName").toString();
        GroupCurrencyID = b.getCharSequence("GroupCurrencyID").toString();
        CurrencyID = b.getCharSequence("CurrencyID").toString();
        OwnerID = b.getCharSequence("OwnerID").toString();
        OwnerName = b.getCharSequence("OwnerName").toString();
        Title = b.getCharSequence("Title").toString();
        Description = b.getCharSequence("Description").toString();
        OriginalAmount = b.getFloat("OriginalAmount");
        ExchangeRate = b.getFloat("ExchangeRate");
        SelectedMembers = b.getStringArrayList("SelectedMembers");


        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Unequal Splitting for " + Title);

        //Views
        OriginalAmountTV = (TextView) findViewById(R.id.OriginalAmountTV);
        EndAmountTV = (TextView) findViewById(R.id.EndAmountTV);
        ListViewMembers = (ListView) findViewById(listViewMembers);

        //Buttons
        findViewById(R.id.RecalculateBtn).setOnClickListener(this);
        findViewById(R.id.AddExpenseBtn).setOnClickListener(this);
        //Populate Views
        EndAmount = OriginalAmount * ExchangeRate;
        OriginalAmountTV.setText(String.format("%.2f", OriginalAmount) + " " + CurrencyID
                + " (Equivalent to " + String.format("%.2f", EndAmount) + " " + GroupCurrencyID + ")");
        EndAmountTV.setText("Exchange Rate: " + String.format("%.3f", ExchangeRate) + " " + CurrencyID + "/" + GroupCurrencyID);
        UpdateUI();


    }
    public void UpdateUI(){
        DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users");
        final Float EqualAmount = EqualSplitCalc(OriginalAmount, SelectedMembers.size());
        final Float EndEqualAmount = EqualAmount * ExchangeRate;
        final ArrayList<ItemUnequal> MembersDataList = new ArrayList<ItemUnequal>();
        for (final String MemberUID : SelectedMembers){
            Query UserQuery = uDatabase.child(MemberUID);
            UserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    String UserName = user.Name;
                    String UserEmail = user.Email;
                    MembersDataList.add(new ItemUnequal(MemberUID, UserName, UserEmail, CurrencyID, EqualAmount, GroupCurrencyID, EndEqualAmount, ExchangeRate, OriginalAmount));
                    generate(MembersDataList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void generate(ArrayList<ItemUnequal> MembersDataList) {
        adapter = new MyAdapterUnequal(this, MembersDataList);
        ListViewMembers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ListViewMembers.setAdapter(adapter);

    }

    protected Float EqualSplitCalc(Float TotalAmount, int PayerNumbers) {
        Float EachAmount = format(TotalAmount / PayerNumbers);
        return EachAmount;
    }
    protected Float format(Float n) {
        Float fn = Math.round(n * 100.00f) / 100.00f;
        return fn;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent parentIntent = NavUtils.getParentActivityIntent(this);
            if (parentIntent == null) {
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
        int i = v.getId();
        if (i == R.id.AddExpenseBtn) {
            Float AmountUndistributed = OriginalAmount;
            for (int p = 0; p < adapter.getCount(); p++){
                AmountUndistributed = AmountUndistributed - adapter.getItem(p).getOriginalAmount();
            }
            if (AmountUndistributed > 0){
                Toast.makeText(this, "Amount Undistributed: " + String.format("%.2f", AmountUndistributed), Toast.LENGTH_SHORT).show();
            }else{
                AddExpense();
            }
        }else if (i == R.id.RecalculateBtn){
            Recalculate();
        }
    }

    public void AddExpense(){
        Map<String, Float> PayerDataList = new HashMap<String, Float>();
        ArrayList<String> PayerIDList = new ArrayList<String>();
        for (int position = 0; position < adapter.getCount(); position++){
            ItemUnequal item = adapter.getItem(position);
            String UserID = item.getUserID();
            Float UserEndAmount = item.getEndAmount();
            PayerDataList.put(UserID, UserEndAmount);
            PayerIDList.add(UserID);
        }
        Float TotalEndAmount = OriginalAmount * ExchangeRate;
        Expenses expense = new Expenses(Title, Description, TotalEndAmount, OwnerID, OwnerName, PayerDataList, GroupName, GroupId, CurrencyID, ExchangeRate);

        //Add Expense into Expense Database
        DatabaseReference eDatabase = FirebaseDatabase.getInstance().getReference("expenses");
        String ExpenseID = eDatabase.push().getKey();
        eDatabase.child(ExpenseID).setValue(expense);

        //Add Owner to List
        if (!(PayerIDList.contains(OwnerID))){
            PayerIDList.add(OwnerID);
        }
        //Add ExpenseID to User
        for (String PayerID: PayerIDList) {
            AddExpenseToUser(PayerID, ExpenseID);
        }

        //Add Expense to Group Database
        AddExpenseToGroup(GroupId, ExpenseID);

        //update total Amount owed to the group in the group database
        updateTotalAmount(OwnerID, PayerDataList, TotalEndAmount, GroupId);
    }

    public void updateTotalAmount(final String currentUid, final Map<String, Float> selectedMembersData, final Float totalAmount, String groupId) {
        final DatabaseReference gDatabase = FirebaseDatabase.getInstance().getReference("groups").child(groupId);
        Query UserMapQuery = gDatabase;
        UserMapQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Groups group = dataSnapshot.getValue(Groups.class);
                Map<String, Float> ParticipantData = group.participants;
                for (Map.Entry<String, Float> Data : selectedMembersData.entrySet()) {
                    String UID = Data.getKey();
                    Float UpdatedAmount = ParticipantData.get(UID) - selectedMembersData.get(UID);
                    ParticipantData.put(UID, UpdatedAmount);
                }
                Float CurrentUserAmount = ParticipantData.get(currentUid) + totalAmount;
                ParticipantData.put(currentUid, CurrentUserAmount);
                gDatabase.child("participants").setValue(ParticipantData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    protected void AddExpenseToGroup(String GroupID, final String ExpenseID) {
        final DatabaseReference gDatabase = FirebaseDatabase.getInstance().getReference("groups").child(GroupID);
        Query GroupQuery = gDatabase;
        GroupQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Groups group = dataSnapshot.getValue(Groups.class);
                List<String> ExpenseList = new ArrayList<String>();
                if (group.Expenses != null) {
                    ExpenseList = group.Expenses;
                }
                ExpenseList.add(ExpenseID);

                gDatabase.child("Expenses").setValue(ExpenseList);
                Intent myIntent = new Intent(UnequalSplittingActivity.this, GroupActivity.class);
                Bundle b = new Bundle();
                b.putString("GroupId", GroupId);
                b.putString("GroupName", GroupName);
                b.putString("GroupCurrencyID", GroupCurrencyID);
                myIntent.putExtras(b);
                startActivity(myIntent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void AddExpenseToUser(String Member, final String ExpenseID) {
        final DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users").child(Member);
        Query UserQuery = uDatabase;
        UserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                List<String> ExpenseList = new ArrayList<String>();
                if (user.Expenses != null) {
                    ExpenseList = user.Expenses;
                }
                ExpenseList.add(ExpenseID);
                uDatabase.child("Expenses").setValue(ExpenseList);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void Recalculate(){
        //Check Which Friends are selected
        boolean[] checkBoxState = adapter.checkBoxState;
        int UncheckedNum = 0;
        Float AmountLeft = OriginalAmount;
        for (int position = 0; position < adapter.checkBoxState.length; position++){
                if (checkBoxState[position]==false){
                    UncheckedNum++;
                    AmountLeft = AmountLeft - adapter.getItem(position).getOriginalAmount();
                }else{

                }
        }
        Float SplitAmount = EqualSplitCalc(AmountLeft, adapter.checkBoxState.length - UncheckedNum);
        for (int position = 0; position < adapter.checkBoxState.length; position++){
            if (checkBoxState[position]==true){
                adapter.getItem(position).setOriginalAmount(SplitAmount);
                adapter.notifyDataSetChanged();

            }else{

            }
        }
    }
}
