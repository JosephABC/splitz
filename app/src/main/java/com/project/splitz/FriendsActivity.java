package com.project.splitz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends Activity implements View.OnClickListener {

    public FirebaseAuth mAuth;
    private ArrayAdapter<String> adapter;
    public ArrayList<Items> FriendDataList = new ArrayList<Items>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mAuth = FirebaseAuth.getInstance();



        // Retrieve friend list
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String currentUid = currentUser.getUid();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

        Query UserQuery = mDatabase.child(currentUid).child("Friends");
        UserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Generate list of friends details
                ArrayList<String> arrayList = new ArrayList<String>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String Entry = child.getValue().toString();
                    arrayList.add(Entry);
                }
                    //Friend List Adapter
                    MyAdapter adapter = new MyAdapter(FriendsActivity.this, generateData(arrayList));
                    ListView friendList = (ListView) findViewById(R.id.listViewFriends);
                    friendList.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        findViewById(R.id.AddFriendBtn).setOnClickListener(this);
    }

    // Generate ArrayList of Friend Data
    private ArrayList<Items> generateData(List<String> FriendUidList){
//        final ArrayList<Items> FriendDataList = new ArrayList<Items>();
        for (String uid: FriendUidList){
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
            Query UserQuery = mDatabase.orderByKey().equalTo(uid);
            UserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // Generate list of friends details
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String Name = child.child("Name").getValue().toString();
                        String Email = child.child("Email").getValue().toString();
                        FriendDataList.add(new Items(Name, Email));
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
//        FriendDataList.add(new Items("hello1","Hello1"));
//        FriendDataList.add(new Items("hello2","Hello2"));
//        FriendDataList.add(new Items("hello3","Hello3"));
        System.out.println(FriendDataList);
        return FriendDataList;

    }



    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.AddFriendBtn) {
            Intent aIntent = new Intent(FriendsActivity.this, AddFriendActivity.class);
            startActivity(aIntent);

        }
    }

}
