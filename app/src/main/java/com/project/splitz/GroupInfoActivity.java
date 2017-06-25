package com.project.splitz;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class GroupInfoActivity extends AppCompatActivity {
    public FirebaseAuth mAuth;
    public ListView ListViewParticipants;
    public String GroupId;
    public String GroupName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Group view
        ListViewParticipants = (ListView) findViewById(R.id.listViewParticipants);

        //Initialize Auth
        mAuth = FirebaseAuth.getInstance();

        //Handle Bundle Extras
        Bundle b = getIntent().getExtras();
        GroupId = b.getCharSequence("GroupId").toString();
        GroupName = b.getCharSequence("GroupName").toString();

        setTitle(GroupName + "  INFO");

        updateUI(GroupId);

    }

    public void updateUI(String GroupId){
        final ArrayList<String> UserIdList = new ArrayList<String>();
        DatabaseReference gDatabase = FirebaseDatabase.getInstance().getReference("groups");
        Query GroupQuery = gDatabase.child(GroupId);
        GroupQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Groups group = dataSnapshot.getValue(Groups.class);
                Map<String, Float> ParticipantsDataList = group.participants;
                GenerateUsers(ParticipantsDataList);
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                    UserIdList.add(child.getKey().toString());
//                }
//                GenerateUsers(UserIdList);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void GenerateUsers(final Map<String, Float> ParticipantsDataList){
        ArrayList<String> UserIdList = new ArrayList<>(ParticipantsDataList.keySet());
        final ArrayList<ItemsUserInfo> UserList = new ArrayList<>();
        for (final String userId: UserIdList){
            DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users");
            Query GroupQuery = uDatabase.orderByKey().equalTo(userId);
            GroupQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String UserEmail = child.child("Email").getValue().toString();
                        String UserName = child.child("Name").getValue().toString();
                        Float UserAmount = ParticipantsDataList.get(userId);
                        UserList.add(new ItemsUserInfo(UserName, UserEmail, UserAmount ));
                    }
                    generate(UserList);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

//    public void GenerateUsers(ArrayList<String> UserIdList){
//        final ArrayList<Items> UserList = new ArrayList<Items>();
//        for (final String userId: UserIdList){
//            DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users");
//            Query GroupQuery = uDatabase.orderByKey().equalTo(userId);
//            GroupQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for (DataSnapshot child : dataSnapshot.getChildren()) {
//                        String UserEmail = child.child("Email").getValue().toString();
//                        String UserName = child.child("Name").getValue().toString();
//                        UserList.add(new Items(UserName, UserEmail));
//                    }
//                    generate(UserList);
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                }
//            });
//        }
//    }

    public void generate(ArrayList<ItemsUserInfo> UserList){
        MyAdapterGroupInfo adapterGroupInfo = new MyAdapterGroupInfo(this, UserList);
//        ListViewParticipants.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ListViewParticipants.setAdapter(adapterGroupInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_info, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }
}
