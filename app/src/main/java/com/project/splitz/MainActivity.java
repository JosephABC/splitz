package com.project.splitz;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.project.splitz.R.id.textView;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    TextView userView;
    public ListView ListViewGroups;
    private static final String TAG = "Activity";

    public FirebaseAuth mAuth;

    private GoogleApiClient mGoogleApiClient;

    //Drawer stuff
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

//    private ArrayAdapter<String> adapter;

    final String[] fragments = {
            "com.project.splitz.FriendsFragment"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        // Setup drawer view
        NavigationView nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);
        userView = (TextView) nvDrawer.getHeaderView(0).findViewById(R.id.userViewTV);

        // Group view
        ListViewGroups = (ListView) findViewById(R.id.listViewGroups);

        findViewById(R.id.NewGrpBtn).setOnClickListener(this);

        //Create Google Sign in option
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //Initialize Auth
        mAuth = FirebaseAuth.getInstance();

        //Find Current User
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        ListViewGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView GroupIdTV = (TextView) view.findViewById(R.id.value_groups);
                String GroupId= GroupIdTV.getText().toString();
                TextView GroupNameTV = (TextView) view.findViewById(R.id.label_groups);
                String GroupName= GroupNameTV.getText().toString();

                Intent myIntent = new Intent(MainActivity.this, GroupActivity.class);

                Bundle b = new Bundle();
                b.putString("GroupId", GroupId);
                b.putString("GroupName", GroupName);
                myIntent.putExtras(b);

                startActivity(myIntent);

            }
        });
    }


    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_groups:
                fragmentClass = Fragment.class;
                Intent myIntent = new Intent(this, CreateGroupActivity.class);
                startActivity(myIntent);
                break;
            case R.id.nav_friends:
                fragmentClass = Fragment.class;
                Intent Intent = new Intent(this, FriendsActivity.class);
                startActivity(Intent);
                break;
            case R.id.nav_settings:
                fragmentClass = Fragment.class;
                Intent aIntent = new Intent(this, Main2Activity.class);
                startActivity(aIntent);
                break;
            case R.id.nav_about:
                fragmentClass = Fragment.class;
                Intent bIntent = new Intent(this, TabActivity.class);
                startActivity(bIntent);
                break;
            case R.id.nav_signout:
                fragmentClass = Fragment.class;
                signOut();
                break;
            default:
                fragmentClass = Fragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();




        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

/*        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }*/


        return super.onOptionsItemSelected(item);
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE 1: Make sure to override the method with only a single `Bundle` argument
    // Note 2: Make sure you implement the correct `onPostCreate(Bundle savedInstanceState)` method.
    // There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger icon.
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            userView.setText(name);
        }
        final ArrayList<String> GroupIdList = new ArrayList<String>();
        DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users");
        Query GroupQuery = uDatabase.child(user.getUid()).child("groups");
        GroupQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    GroupIdList.add(child.getValue().toString());
                }
                GenerateGroupName(GroupIdList);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void GenerateGroupName(ArrayList<String> GroupIdList){
        final ArrayList<Items> GroupNameList = new ArrayList<Items>();
        for (final String groupId: GroupIdList){
            DatabaseReference gDatabase = FirebaseDatabase.getInstance().getReference("groups");
            Query GroupQuery = gDatabase.orderByKey().equalTo(groupId);
            GroupQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String GroupName = child.child("groupName").getValue().toString();
                        GroupNameList.add(new Items(GroupName, groupId));
                    }
                    generate(GroupNameList);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    public void generate(ArrayList<Items> GroupNameList){
/*        String[] string = new String[GroupNameList.size()];
        GroupNameList.toArray(string);*/
        MyAdapterGroups adapter1 = new MyAdapterGroups(MainActivity.this, GroupNameList);
/*        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, string);*/
        ListViewGroups.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ListViewGroups.setAdapter(adapter1);
    }

    public void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                    }
                });
        // FB sign out
        LoginManager.getInstance().logOut();

        //Redirect back to LoginActivity
        Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(myIntent);
        finish();
    }

    //On Click Listeners
    @Override
    public void onClick(View v) {
        Intent myIntent = new Intent(MainActivity.this, CreateGroupActivity.class);
        startActivity(myIntent);
    }

    //On Connection Failed Listeners
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}