package com.project.splitz;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
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

import org.w3c.dom.Text;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Add Friend");

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

    public void updateUI(String SearchUserEmail){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        Query UserQuery = mDatabase.orderByChild("Email").equalTo(SearchUserEmail);

        UserQuery.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    Toast.makeText(AddFriendActivity.this, "No Such User Found", Toast.LENGTH_SHORT).show();
                } else {
                    User user = dataSnapshot.getValue(User.class);
                    System.out.println(user.Email);
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
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(currentUid);
        Query UserQuery = mDatabase;
        UserQuery.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                List<String> FriendList = new ArrayList<String>();
                if (currentUser.Friends != null){
                    FriendList = currentUser.Friends;
                }
                final String FriendUID = UserIDField.getText().toString();
                if (FriendList.contains(FriendUID)){
                    Toast.makeText(AddFriendActivity.this, "User is already your Friend", Toast.LENGTH_SHORT).show();
                }else if(FriendUID.equals(currentUid)){
                    Toast.makeText(AddFriendActivity.this, "You Cannot Add yourself as Friend", Toast.LENGTH_SHORT).show();
                }else{
                    //Add User to current User Friend List

                    FriendList.add(FriendUID);
                    mDatabase.child("Friends").setValue(FriendList);

                    //Add Current User to User Friend List
                    final DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("users").child(FriendUID);
                    Query FriendQuery = fDatabase;
                    FriendQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User Friend = dataSnapshot.getValue(User.class);
                            List<String> FriendFriendList = new ArrayList<String>();
                            if (Friend.Friends != null){
                                FriendFriendList = Friend.Friends;
                            }
                            FriendFriendList.add(currentUid);
                            fDatabase.child("Friends").setValue(FriendFriendList);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

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
            if (TextUtils.isEmpty(UserIDField.getText().toString())){
                Toast.makeText(AddFriendActivity.this, "Please Search for a User First", Toast.LENGTH_SHORT).show();
            }else{
                addFriend();
            }
        }

    }
}
