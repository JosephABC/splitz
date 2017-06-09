package com.project.splitz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class FriendsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        findViewById(R.id.AddFriendBtn).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.AddFriendBtn) {
            Intent aIntent = new Intent(FriendsActivity.this, AddFriendActivity.class);
            startActivity(aIntent);

        }
    }

}
