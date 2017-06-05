package com.project.splitz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.security.acl.Group;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView toolbarText;
    private Toolbar toolbar;
    private EditText groupField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        findViewById(R.id.backBtn).setOnClickListener(this);
        findViewById(R.id.submitBtn).setOnClickListener(this);
        groupField = (EditText) findViewById(R.id.groupnameET);

/*        // Toolbar
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.includetoolbar);
        setSupportActionBar(toolbar);
        toolbarText = (TextView) toolbar.findViewById(R.id.toolbarText);
        toolbarText.setText("Create New Group");*/

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.backBtn) {
            Intent myIntent = new Intent(CreateGroupActivity.this, MainActivity.class);
            startActivity(myIntent);
        } else if (i == R.id.submitBtn) {
            String GroupName = groupField.getText().toString();
            if (!validateForm()) {
                return;
            }
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
            String userId = mDatabase.push().getKey();
            Groups group = new Groups(GroupName);
            mDatabase.child(userId).setValue(group);
            Intent myIntent = new Intent(CreateGroupActivity.this, MainActivity.class);
            startActivity(myIntent);
        }
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
