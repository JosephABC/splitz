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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.project.splitz.R.id.text;
import static com.project.splitz.R.id.textView;

public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "Activity";
    private static final int Google_SIGN_IN = 9001;
    private static final int FB_SIGN_IN = 3001;

    private EditText emailField;
    private EditText passwordField;

    private GoogleApiClient mGoogleApiClient;

    public FirebaseAuth mAuth;

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize auth
        mAuth = FirebaseAuth.getInstance();

        // Views
        emailField = (EditText) findViewById(R.id.emailET);
        passwordField = (EditText) findViewById(R.id.passwordET);

        // Buttons
        findViewById(R.id.signInBtn).setOnClickListener(this);
        findViewById(R.id.signUpBtn).setOnClickListener(this);
        findViewById(R.id.GoogleSignInBtn).setOnClickListener(this);

        //Create Google Sign in option
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //Initialise facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.FBsignInBtn);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
        // [END initialize_fblogin]




    }

    //[START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        //Check if user is signed in (non-null)
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent myIntent = new Intent(LoginActivity.this, TabActivity.class);
            startActivity(myIntent);
        }
    }
    //[Start] FB Sign in
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Insert New User into Firebase Database
                            NewUser(user);

                            Intent main = new Intent(LoginActivity.this, TabActivity.class);
                            startActivity(main);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    //[End] FB Sign in

    //[Start] Google Sign in
    private void GoogleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, Google_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Google_SIGN_IN) {
            GoogleSignInResult results = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (results.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = results.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                //If Google Sign in fails
            }
        } else {
            // Result Returned from launching intent from FB
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
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
                            //Insert New User into Firebase Database
                            NewUser(user);
                            Intent main = new Intent(LoginActivity.this, TabActivity.class);
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
    //Insert New User into FireBase database
    public void NewUser(FirebaseUser User){
        final String userID = User.getUid();
        final String Email = User.getEmail();
        final String Name = User.getDisplayName();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(userID)) {

                } else{
                    User User = new User(Email, Name, null);
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
                    mDatabase.child(userID).setValue(User);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //[End] Google Sign in

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
                    Intent myIntent = new Intent(LoginActivity.this, TabActivity.class);
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
