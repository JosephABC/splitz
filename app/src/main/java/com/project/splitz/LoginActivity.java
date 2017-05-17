package com.project.splitz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
// hello
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private EditText emailField;
    private EditText passwordField;

    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        emailField = (EditText) findViewById(R.id.emailET);
        passwordField = (EditText) findViewById(R.id.passwordET);

        // Buttons
        findViewById(R.id.signInBtn).setOnClickListener(this);
        findViewById(R.id.signUpBtn).setOnClickListener(this);
        findViewById(R.id.verifyEmailBtn).setOnClickListener(this);

        // initialize auth
        mAuth = FirebaseAuth.getInstance();
    }

    //[START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        //Check if user is signed in (non-null)
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(myIntent);
        }
    }


    protected void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if(!validateForm()) {
            return;
        }

        // [START create_user_with_email]

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // Signin success,updateUI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:Success");
                    FirebaseUser user = mAuth.getCurrentUser();
                } else {
                    // If signin fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure",task.getException());
                    Toast.makeText(LoginActivity.this, "Authenticationfailed.", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    protected void signIn(String email, String password) {
        Log.d(TAG,"signIn:" + email);
        if(!validateForm()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(myIntent);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



    protected void sendEmailVerification() {
        // Disable button
        findViewById(R.id.verifyEmailBtn).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        findViewById(R.id.verifyEmailBtn).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(LoginActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }
    protected boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signUpBtn) {
            createAccount(emailField.getText().toString(), passwordField.getText().toString());
        } else if (i == R.id.signInBtn) {
            signIn(emailField.getText().toString(), passwordField.getText().toString());
        } else if (i == R.id.verifyEmailBtn) {
            sendEmailVerification();
        }
    }
}


















//public class LoginActivity extends AppCompatActivity {
//    EditText userNameET;
//    EditText passwordET;
//    Button loginBtn;
//    Button signUpBtn;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        userNameET = (EditText) findViewById(R.id.userNameET);
//        passwordET = (EditText) findViewById(R.id.passwordET);
//        loginBtn = (Button) findViewById(R.id.signInBtn);
//        signUpBtn = (Button) findViewById(R.id.signUpBtn);
//
//        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
//
//        loginBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                String userName = userNameET.getText().toString();
//                String password = passwordET.getText().toString();
//                if (TextUtils.isEmpty(userName)) {
//                    Toast.makeText(LoginActivity.this, "Please enter your username.", Toast.LENGTH_SHORT).show();
//                } else if(TextUtils.isEmpty(password)) {
//                    Toast.makeText(LoginActivity.this, "Please enter your password. ", Toast.LENGTH_SHORT).show();
//                }
//                else {
//
//
//                }
//            }
//        });
//        signUpBtn.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View view) {
//                Intent myIntent = new Intent(LoginActivity.this, SignUpActivity.class);
//                startActivity(myIntent);
//            }
//        });
//    }
//}
