package com.project.splitz;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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

import org.w3c.dom.Text;

import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView toolbarText;
    private Toolbar toolbar;
    private EditText groupField;
    private ListView listViewFriends;
    private Spinner CurrencySpinner;

    public FirebaseAuth mAuth;
    private ArrayAdapter<Items> adapter;
    public ArrayList<Items> FriendDataList = new ArrayList<Items>();
    public ArrayList<String> FriendUidList = new ArrayList<String>();
    public static final int RequestCode = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        findViewById(R.id.submitBtn).setOnClickListener(this);
        groupField = (EditText) findViewById(R.id.groupnameET);
        listViewFriends = (ListView) findViewById(R.id.listViewFriends);

        CurrencySpinner = (Spinner) findViewById(R.id.currencySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_arrays, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CurrencySpinner.setAdapter(adapter);

        //Generate Friend List
        GenerateFriendList();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Create Group");
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
        }
        return super.onOptionsItemSelected(item);
    }

    public void GenerateFriendList(){
        mAuth = FirebaseAuth.getInstance();

        // Display friend list
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String currentUid = currentUser.getUid();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

        Query UserQuery = mDatabase.child(currentUid).child("Friends");
        UserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Generate list of friends details
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String Entry = child.getValue().toString();
                    FriendUidList.add(Entry);
                }

                // Retrieve name and email data
                for (String uid: FriendUidList){
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
                    Query UserQuery = mDatabase.orderByKey().equalTo(uid);
                    UserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                String Email = child.child("Email").getValue().toString();
                                String Uid = child.getKey();
                                FriendDataList.add(new Items(Email, Uid, null));
                            }
                            generate(FriendDataList);

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
    public void generate(ArrayList<Items> FriendDataList){
        adapter = new MyAdapterMembers(this, FriendDataList);
        listViewFriends.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listViewFriends.setAdapter(adapter);
    }
    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.submitBtn) {
            if (!validateForm()) {
                return;
            }
            groupSubmit(groupField.getText().toString());
        }

    }
    protected void groupSubmit(final String GroupName){
        //Initialise ArrayLists
        final Map<String, Float> GroupUidList = new HashMap<String, Float>();


        //Add Current user by default to the group
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        GroupUidList.put(currentUser.getUid(), 0.0f);

        //Group Database
        DatabaseReference gDatabase = FirebaseDatabase.getInstance().getReference("groups");
        final String groupId = gDatabase.push().getKey();
        String CurrencyID = String.valueOf(CurrencySpinner.getSelectedItem());
        Groups group = new Groups(GroupName, GroupUidList, null, CurrencyID);
        gDatabase.child(groupId).setValue(group);
        //User Database
        final DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users");
        //Add GroupId to current User
        addGroup(currentUser.getUid(), groupId);

        //Check Which Friends are selected
        SparseBooleanArray checked = listViewFriends.getCheckedItemPositions();
        Map<String, Float> selectedFriends = new HashMap<String, Float>();
        for (int c = 0; c < checked.size(); c++) {
            int position = checked.keyAt(c);
            if (checked.valueAt(c)) {
                selectedFriends.put(adapter.getItem(position).getDescription(), 0.0f);
            }
        }
        selectedFriends.put(currentUser.getUid(), 0.0f);

        //Add Users to new group database and add group to user database
        addParticipant(groupId, selectedFriends);
        List<String> selectedFriendsArray = new ArrayList<>(selectedFriends.keySet());
        for (String Uid: selectedFriendsArray){
            addGroup(Uid,groupId);
        }

        Intent parentIntent = NavUtils.getParentActivityIntent(this);
        if(parentIntent == null) {
            finish();
        } else {
            parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(parentIntent);
            finish();

        }

    }
    //Add selected friend to Group Database
    public void addParticipant(String groupId, Map<String, Float> GroupUidList){
        DatabaseReference gDatabase = FirebaseDatabase.getInstance().getReference("groups").child(groupId).child("participants");
        gDatabase.setValue(GroupUidList);
    }
    public void addGroup(final String UserUid, final String groupId){
        final ArrayList<String> UserGroupList = new ArrayList<String>();
        final DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users");
        Query GroupQuery = uDatabase.child(UserUid).child("groups");
        GroupQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    UserGroupList.add(child.getValue().toString());
                }
                UserGroupList.add(groupId);
                uDatabase.child(UserUid).child("groups").setValue(UserGroupList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    protected boolean validateForm() {
        boolean valid = true;
        String GroupName = groupField.getText().toString();
        if (TextUtils.isEmpty(GroupName)) {
            groupField.setError("Required.");
            valid = false;
        } else {
            groupField.setError(null);
        }
        return valid;
    }


}
