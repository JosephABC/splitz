package com.project.splitz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import static com.project.splitz.R.id.text;
import static com.project.splitz.R.id.textView;

public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "Activity";
    private static final int RC_SIGN_IN = 9001;

    private EditText emailField;
    private EditText passwordField;
    private TextView textView;

    private GoogleApiClient mGoogleApiClient;

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
        findViewById(R.id.GoogleSignInBtn).setOnClickListener(this);
        findViewById(R.id.signOutBtn).setOnClickListener(this);

        //Create Google Sign in option
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // initialize auth
        mAuth = FirebaseAuth.getInstance();

    }

    //[START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        //Check if user is signed in (non-null)
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(myIntent);
        }
    }
    //[Start] Google Sign in
    private void GoogleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult results = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (results.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = results.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                //If Google Sign in fails
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent main = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(main);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });

    }
    //[End] Google Sign in

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                    }
                });
    }

    //[Start] EmailPassword Sign in
    protected void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
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
    //[End] EmailPassword Sign in

    //Ensure all fields are filled
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

    //Click Listeners
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signUpBtn) {
            Intent myIntent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(myIntent);

        } else if (i == R.id.signInBtn) {
            signIn(emailField.getText().toString(), passwordField.getText().toString());
        } else if (i == R.id.GoogleSignInBtn) {
            GoogleSignIn();
        } else if (i == R.id.signOutBtn) {
            signOut();

        }
    }
    //onConnectionFailed Listener
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
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
