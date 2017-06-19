package com.project.splitz;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupsFragment extends Fragment implements View.OnClickListener{

    public FirebaseAuth mAuth;
    public ListView ListViewGroups;

    private boolean shouldRefreshOnResume = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);


        // Group view
        ListViewGroups = (ListView) rootView.findViewById(R.id.listViewGroups);

        // Button
        rootView.findViewById(R.id.NewGroupBtn).setOnClickListener(this);
        rootView.findViewById(R.id.RefreshBtn).setOnClickListener(this);
        //Initialize Auth
        mAuth = FirebaseAuth.getInstance();
        //Find Current User
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        ListViewGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView GroupIdTV = (TextView) view.findViewById(R.id.value_groups);
                String GroupId= GroupIdTV.getText().toString();
                TextView GroupNameTV = (TextView) view.findViewById(R.id.label_groups);
                String GroupName= GroupNameTV.getText().toString();

                Intent myIntent = new Intent(getActivity(), GroupActivity.class);

                Bundle b = new Bundle();
                b.putString("GroupId", GroupId);
                b.putString("GroupName", GroupName);
                myIntent.putExtras(b);

                startActivity(myIntent);

            }
        });
        return rootView;
    }



    private void updateUI(FirebaseUser user) {
        final ArrayList<String> GroupIdList = new ArrayList<String>();
        DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users");
        Query GroupQuery = uDatabase.child(user.getUid()).child("groups");
        GroupQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    GroupIdList.add(child.getValue().toString());
                }
                GenerateGroupName(GroupIdList);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void GenerateGroupName(ArrayList<String> GroupIdList){
        final ArrayList<Items> GroupNameList = new ArrayList<Items>();
        for (final String groupId: GroupIdList){
            DatabaseReference gDatabase = FirebaseDatabase.getInstance().getReference("groups");
            Query GroupQuery = gDatabase.orderByKey().equalTo(groupId);
            GroupQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String GroupName = child.child("groupName").getValue().toString();
                        GroupNameList.add(new Items(GroupName, groupId));
                    }
                    generate(GroupNameList);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    public void generate(ArrayList<Items> GroupNameList){
        MyAdapterGroups adapter1 = new MyAdapterGroups(getActivity(), GroupNameList);
        ListViewGroups.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ListViewGroups.setAdapter(adapter1);
    }


    @Override
    public void onClick(View v) {
        int i= v.getId();
        if (i == R.id.NewGroupBtn) {
            Intent myIntent = new Intent(getActivity(), CreateGroupActivity.class);
            startActivity(myIntent);
        }else if (i == R.id.RefreshBtn) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
    }
}