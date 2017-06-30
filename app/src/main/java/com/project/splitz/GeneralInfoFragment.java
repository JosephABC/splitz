package com.project.splitz;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


public class GeneralInfoFragment extends Fragment {
    public FirebaseAuth mAuth;
    public ListView ListViewParticipants;
    public String GroupId;
    public String GroupName;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_general_info, container, false);

        // Group view
        ListViewParticipants = (ListView) rootView.findViewById(R.id.listViewParticipants);

        //Initialize Auth
        mAuth = FirebaseAuth.getInstance();

        //Handle Bundle Extras
        Bundle b = getActivity().getIntent().getExtras();
        GroupId = b.getCharSequence("GroupId").toString();
        GroupName = b.getCharSequence("GroupName").toString();

        updateUI(GroupId);

        return rootView;
    }

    public void updateUI(String GroupId){
        DatabaseReference gDatabase = FirebaseDatabase.getInstance().getReference("groups");
        Query GroupQuery = gDatabase.child(GroupId);
        GroupQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Groups group = dataSnapshot.getValue(Groups.class);
                Map<String, Float> ParticipantsDataList = group.participants;
                GenerateUsers(ParticipantsDataList);
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
    public void generate(ArrayList<ItemsUserInfo> UserList){
        MyAdapterGroupInfo adapterGroupInfo = new MyAdapterGroupInfo(getActivity(), UserList);
//        ListViewParticipants.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ListViewParticipants.setAdapter(adapterGroupInfo);
    }



}
