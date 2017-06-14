package com.project.splitz;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

import org.w3c.dom.Text;

import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class CreateGroupActivity extends Activity implements View.OnClickListener{
    private TextView toolbarText;
    private Toolbar toolbar;
    private EditText groupField;
    private ListView listViewFriends;

    public FirebaseAuth mAuth;
    private ArrayAdapter<String> adapter;
    public ArrayList<String> FriendDataList = new ArrayList<String>();
    public ArrayList<String> FriendUidList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        findViewById(R.id.backBtn).setOnClickListener(this);
        findViewById(R.id.submitBtn).setOnClickListener(this);
//        findViewById(R.id.listViewFriends).setOnClickListener(this);
        groupField = (EditText) findViewById(R.id.groupnameET);
        listViewFriends = (ListView) findViewById(R.id.listViewFriends);

        //Generate Friend List
        GenerateFriendList();

/*        // Toolbar
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.includetoolbar);
        setSupportActionBar(toolbar);
        toolbarText = (TextView) toolbar.findViewById(R.id.toolbarText);
        toolbarText.setText("Create New Group");*/

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
//                                String Name = child.child("Name").getValue().toString();
                                String Email = child.child("Email").getValue().toString();
//                                String StringItem = Name + "\n     " + Email;
                                FriendDataList.add(Email);
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
    public void generate(ArrayList<String> FriendDataList){
        String[] string = new String[FriendDataList.size()];
        FriendDataList.toArray(string);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, string);
        listViewFriends.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listViewFriends.setAdapter(adapter);
    }
    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.backBtn) {
            Intent myIntent = new Intent(CreateGroupActivity.this, MainActivity.class);
            startActivity(myIntent);
        } else if (i == R.id.submitBtn) {
            if (!validateForm()) {
                return;
            }
            groupSubmit(groupField.getText().toString());
        }

    }
    protected void groupSubmit(final String GroupName){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("groups");
        final String groupId = mDatabase.push().getKey();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final ArrayList<String> GroupUidList = new ArrayList<String>();
        GroupUidList.add(currentUser.getUid());
        Groups group = new Groups(GroupName, GroupUidList);
        mDatabase.child(groupId).setValue(group);
        //Check Which Friends are selected
        SparseBooleanArray checked = listViewFriends.getCheckedItemPositions();
        ArrayList<String> selectedFriends = new ArrayList<String>();
        for (int c = 0; c < checked.size(); c++) {
            int position = checked.keyAt(c);
            if (checked.valueAt(c)) {
                selectedFriends.add(adapter.getItem(position));
            }
        }
        for (String Email: selectedFriends){

            DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("users");
            Query UserQuery = fDatabase.orderByChild("Email").equalTo(Email);
            UserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        GroupUidList.add(child.getKey().toString());
                        addParticipant(groupId, GroupUidList);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        Intent myIntent = new Intent(CreateGroupActivity.this, MainActivity.class);
        startActivity(myIntent);

    }
    public void addParticipant(String groupId, List<String> GroupUidList){
        DatabaseReference gDatabase = FirebaseDatabase.getInstance().getReference("groups").child(groupId).child("participants");
        gDatabase.setValue(GroupUidList);
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
