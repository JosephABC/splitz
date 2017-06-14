package com.project.splitz;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class AddFriendActivity extends AppCompatActivity implements View.OnClickListener {

    public EditText SearchUsersField;
    public TextView EmailField;
    public TextView NameField;
    public TextView UserIDField;
    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        // initialize auth
        mAuth = FirebaseAuth.getInstance();

        //Views
        SearchUsersField = (EditText) findViewById(R.id.SearchUsers);
        EmailField = (TextView) findViewById(R.id.textViewEmail);
        NameField = (TextView) findViewById(R.id.textViewName);
        UserIDField = (TextView) findViewById(R.id.textViewUserID);
        //Buttons
        findViewById(R.id.SearchBtn).setOnClickListener(this);
        findViewById(R.id.AddFriendBtn).setOnClickListener(this);

    }

    public void updateUI(String SearchUserEmail){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        Query UserQuery = mDatabase.orderByChild("Email").equalTo(SearchUserEmail);

        UserQuery.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    Toast.makeText(AddFriendActivity.this, "No Such User Found", Toast.LENGTH_SHORT).show();
                } else {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        EmailField.setText(child.child("Email").getValue().toString());
                        NameField.setText(child.child("Name").getValue().toString());
                        UserIDField.setText(child.getKey().toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void addFriend(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String currentUid = currentUser.getUid();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(currentUid).child("Friends");
        Query UserQuery = mDatabase;
        UserQuery.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> FriendList = new ArrayList<String>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String Entry = child.getValue().toString();
                    FriendList.add(Entry);
                }
                if (FriendList.contains(UserIDField.getText().toString())){
                    Toast.makeText(AddFriendActivity.this, "User is already your Friend", Toast.LENGTH_SHORT).show();
                }else{
                    FriendList.add(UserIDField.getText().toString());
                    mDatabase.setValue(FriendList);
                    Toast.makeText(AddFriendActivity.this, "User has been added as Friend", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.SearchBtn) {
            updateUI(SearchUsersField.getText().toString());
        }else if(i == R.id.AddFriendBtn){

            addFriend();
        }

    }
}
