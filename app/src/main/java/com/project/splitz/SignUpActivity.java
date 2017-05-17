package com.project.splitz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    EditText userNameET;
    EditText passwordET;
    EditText confirmPasswordET;
    Button signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signUpBtn = (Button) findViewById(R.id.signUpBtn);
        //Firebase database reference
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userNameET = (EditText) findViewById(R.id.userNameET);
                passwordET = (EditText) findViewById(R.id.passwordET);
                confirmPasswordET = (EditText) findViewById(R.id.confirmPasswordET);
                String userName = userNameET.getText().toString();
                String password = passwordET.getText().toString();
                String confirmPassword = confirmPasswordET.getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your username.", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your password. ", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Please confirm your password. ", Toast.LENGTH_SHORT).show();
                } else {
                    if (password.equals(confirmPassword)) {
                        String userId = mDatabase.push().getKey();
                        User user = new User(userName , password);
                        mDatabase.child(userId).setValue(user);
                        Toast.makeText(SignUpActivity.this, "Sign Up Successful ", Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(myIntent);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Passwords don't match! ", Toast.LENGTH_SHORT).show();
                        confirmPasswordET.setText("");
                    }
                }
            }

        });
    }
}
