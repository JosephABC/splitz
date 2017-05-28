package com.project.splitz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView toolbarText;
    private Toolbar toolbar;
    private EditText GroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        GroupName = (EditText) findViewById(R.id.groupnameField);
        findViewById(R.id.backBtn).setOnClickListener(this);
        findViewById(R.id.submitBtn).setOnClickListener(this);

        // Toolbar
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.includetoolbar);
        setSupportActionBar(toolbar);
        toolbarText = (TextView) toolbar.findViewById(R.id.toolbarText);
        toolbarText.setText("Create New Group");

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.backBtn) {
            Intent myIntent = new Intent(CreateGroupActivity.this, MainActivity.class);
            startActivity(myIntent);
        } else if (i == R.id.submitBtn) {
            Intent myIntent = new Intent(CreateGroupActivity.this, MainActivity.class);
            startActivity(myIntent);
        }
    }
}
