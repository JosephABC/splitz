package com.project.splitz;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.google.firebase.auth.FirebaseAuth.*;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "EmailPassword";
    //initialize EditText fields
    private EditText nameField;
    private EditText emailField;
    private EditText passwordField;
    private EditText confirmPasswordField;

    public FirebaseAuth mAuth;
    public FirebaseAuth mAuthListener;

    public String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Views
        nameField = (EditText) findViewById(R.id.nameET);
        emailField = (EditText) findViewById(R.id.emailET);
        passwordField = (EditText) findViewById(R.id.passwordET);
        confirmPasswordField = (EditText) findViewById(R.id.confirmPasswordET);

        // Button
        findViewById(R.id.signUpBtn).setOnClickListener(this);
        findViewById(R.id.backBtn).setOnClickListener(this);

        // Initialize Auth
        mAuth = getInstance();
    }

    protected void createAccount(final String email, String password, final String name) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // Signin success,updateUI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:Success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                    }
                                }
                            });
                    //Insert User into Firebase database
                    NewUser(user);

                    mAuth.signOut();
                    Toast.makeText(SignUpActivity.this, "Account Sign Up Successful! Please Sign in!", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(myIntent);

                } else {
                    // If signin fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure",task.getException());
                    Toast.makeText(SignUpActivity.this, "Authenticationfailed.", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    //NewUser into Firebase Database
    public void NewUser(FirebaseUser user){
        String userID = user.getUid();
        String Email = user.getEmail();
        String Name = user.getDisplayName();
        User User = new User(Email, Name, null);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(userID).setValue(User);
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

        String confirmPassword = confirmPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            confirmPasswordField.setError("Required.");
            valid = false;
        } else {
            confirmPasswordField.setError(null);
        }

        String name = nameField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            nameField.setError("Required.");
            valid = false;
        } else {
            nameField.setError(null);
        }

        return valid;
    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signUpBtn) {
            if(!validateForm()) {
                return;
            }
            String password = passwordField.getText().toString();
            String confirmPassword = confirmPasswordField.getText().toString();
            if (password.equals(confirmPassword)) {
                createAccount(emailField.getText().toString(), passwordField.getText().toString(), nameField.getText().toString());
            } else {
                Toast.makeText(SignUpActivity.this, "Passwords don't match! ", Toast.LENGTH_SHORT).show();
                confirmPasswordField.setText("");
            }
        } else if (i == R.id.backBtn){
            Intent myIntent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(myIntent);
        }
    }
}

























//public class SignUpActivity extends AppCompatActivity {
//    EditText userNameET;
//    EditText passwordET;
//    EditText confirmPasswordET;
//    Button signUpBtn;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sign_up);
//        signUpBtn = (Button) findViewById(R.id.signUpBtn);
//        //Firebase database reference
//        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
//
//        signUpBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                userNameET = (EditText) findViewById(R.id.userNameET);
//                passwordET = (EditText) findViewById(R.id.passwordET);
//                confirmPasswordET = (EditText) findViewById(R.id.confirmPasswordET);
//                String userName = userNameET.getText().toString();
//                String password = passwordET.getText().toString();
//                String confirmPassword = confirmPasswordET.getText().toString();
//                if (TextUtils.isEmpty(userName)) {
//                    Toast.makeText(SignUpActivity.this, "Please enter your username.", Toast.LENGTH_SHORT).show();
//                } else if(TextUtils.isEmpty(password)) {
//                    Toast.makeText(SignUpActivity.this, "Please enter your password. ", Toast.LENGTH_SHORT).show();
//                } else if(TextUtils.isEmpty(confirmPassword)) {
//                    Toast.makeText(SignUpActivity.this, "Please confirm your password. ", Toast.LENGTH_SHORT).show();
//                } else {
//                    if (password.equals(confirmPassword)) {
//                        String userId = mDatabase.push().getKey();
//                        User user = new User(userName , password);
//                        mDatabase.child(userId).setValue(user);
//                        Toast.makeText(SignUpActivity.this, "Sign Up Successful ", Toast.LENGTH_SHORT).show();
//                        Intent myIntent = new Intent(SignUpActivity.this, LoginActivity.class);
//                        startActivity(myIntent);
//                    } else {
//                        Toast.makeText(SignUpActivity.this, "Passwords don't match! ", Toast.LENGTH_SHORT).show();
//                        confirmPasswordET.setText("");
//                    }
//                }
//            }
//
//        });
//    }
//}
