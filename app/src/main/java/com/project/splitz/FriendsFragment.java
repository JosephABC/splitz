package com.project.splitz;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FriendsFragment extends Fragment implements View.OnClickListener {

    public FirebaseAuth mAuth;
    public Boolean AllowRefresh1 = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        mAuth = FirebaseAuth.getInstance();

        //Button
        rootView.findViewById(R.id.NewFriendBtn).setOnClickListener(this);
        //rootView.findViewById(R.id.RefreshBtn).setOnClickListener(this);

        // Display friend list
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String currentUid = currentUser.getUid();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

        Query UserQuery = mDatabase.child(currentUid).child("Friends");
        UserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Generate list of friends details
                ArrayList<String> FriendUidList = new ArrayList<String>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String Entry = child.getValue().toString();
                    FriendUidList.add(Entry);
                }

                // Retrieve name and email data
                final ArrayList<Items> FriendDataList = new ArrayList<Items>();
                for (String uid: FriendUidList){
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
                    Query UserQuery = mDatabase.orderByKey().equalTo(uid);
                    UserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                String Name = child.child("Name").getValue().toString();
                                String Email = child.child("Email").getValue().toString();
                                FriendDataList.add(new Items(Name, Email, null));
                            }

                            //Friend List Adapter
                            MyAdapterFriends adapter = new MyAdapterFriends(getActivity(), FriendDataList);
                            ListView friendList = (ListView) rootView.findViewById(R.id.listViewFriends);
                            friendList.setAdapter(adapter);
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



        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        if (AllowRefresh1){
            AllowRefresh1 = false;
            new Timer().schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.detach(FriendsFragment.this).attach(FriendsFragment.this).commit();
                        }
                    },
                    1000
            );
        }
    }



    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.NewFriendBtn) {
            Intent aIntent = new Intent(getActivity(), AddFriendActivity.class);
            startActivity(aIntent);
            AllowRefresh1 = true;
/*        }else if (i == R.id.RefreshBtn) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();*/
        }
    }
}