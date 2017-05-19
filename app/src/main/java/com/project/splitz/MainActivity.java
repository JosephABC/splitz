package com.project.splitz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.project.splitz.R.id.textView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView userView;
    public FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userView = (TextView) findViewById(R.id.userViewTV);
        findViewById(R.id.signoutBtn).setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            userView.setText(uid);

        } else {
            userView.setText(null);
        }
    }


    public void signOut() {
        mAuth.signOut();
        Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(myIntent);

    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if  (i == R.id.signoutBtn) {
            signOut();
        }
    }
}

