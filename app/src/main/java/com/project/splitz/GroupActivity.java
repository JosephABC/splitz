package com.project.splitz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class GroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Bundle b = getIntent().getExtras();
        TextView name = (TextView) findViewById(R.id.testtext);

        name.setText(b.getCharSequence("name"));

    }
}
