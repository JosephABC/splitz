package com.project.splitz;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.TimeUtils;
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
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;

public class GroupsFragment extends Fragment implements View.OnClickListener{

    public FirebaseAuth mAuth;
    public ListView ListViewGroups;
    MyAdapterGroups adapter1;
    Boolean AllowRefresh = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);


        // Group view
        ListViewGroups = (ListView) rootView.findViewById(R.id.listViewGroups);

        // Button
        rootView.findViewById(R.id.NewGroupBtn).setOnClickListener(this);
        //rootView.findViewById(R.id.RefreshBtn).setOnClickListener(this);
        //Initialize Auth
        mAuth = FirebaseAuth.getInstance();
        //Find Current User
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);


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
            Query GroupQuery = gDatabase.child(groupId);
            GroupQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Groups group = dataSnapshot.getValue(Groups.class);
                    String GroupName = group.groupName;
                    String GroupCurrencyID = group.CurrencyID;
                    GroupNameList.add(new Items(GroupName, groupId, GroupCurrencyID));
                    generate(GroupNameList);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        if (AllowRefresh){
            AllowRefresh = false;
            new Timer().schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.detach(GroupsFragment.this).attach(GroupsFragment.this).commit();
                        }
                    },
                    1000
            );
        }
    }


    public void generate(ArrayList<Items> GroupNameList){
        adapter1 = new MyAdapterGroups(getActivity(), GroupNameList);
        ListViewGroups.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ListViewGroups.setAdapter(adapter1);
        ListViewGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent myIntent = new Intent(getActivity(), GroupActivity.class);

                Bundle b = new Bundle();
                b.putString("GroupId", adapter1.getItem(position).getDescription());
                b.putString("GroupName", adapter1.getItem(position).getTitle());
                b.putString("GroupCurrencyID", adapter1.getItem(position).getGroupCurrencyID());
                myIntent.putExtras(b);

                startActivity(myIntent);
                AllowRefresh = true;

            }
        });
    }


    @Override
    public void onClick(View v) {
        int i= v.getId();
        if (i == R.id.NewGroupBtn) {
            Intent myIntent = new Intent(getActivity(), CreateGroupActivity.class);
            startActivity(myIntent);
            AllowRefresh = true;
/*        }else if (i == R.id.RefreshBtn) {

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();*/
        }
    }
}