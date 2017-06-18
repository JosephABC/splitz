package com.project.splitz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class GroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        //Handle bundle extras
        Bundle b = getIntent().getExtras();
        String GroupId = b.getCharSequence("GroupId").toString();
        String GroupName = b.getCharSequence("GroupName").toString();

        //Display
        TextView GroupNameTV = (TextView) findViewById(R.id.GroupNameTV);
        GroupNameTV.setText(GroupName);
        TextView GroupIdTV = (TextView) findViewById(R.id.GroupIdTV);
        GroupIdTV.setText(GroupId);



    }
}
