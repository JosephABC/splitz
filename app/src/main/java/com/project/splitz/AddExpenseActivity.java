package com.project.splitz;

import android.app.ListActivity;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.icu.text.MessagePattern;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import org.xml.sax.ext.DefaultHandler2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.acl.Group;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AddExpenseActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private ListView listViewMembers;
    private String GroupId;
    private String GroupName;
    private String GroupCurrencyID;
    private String currentUid;
    public Float OriginalAmount;
    public Float EndAmount;
    public Float ExchangeRate;


    public ArrayList<String> MembersUidList = new ArrayList<String>();
    private MyAdapterMembers adapter;
    private Spinner CurrencySpinner;
    private EditText ExpenseAmountET;
    private TextView EndAmountTV;
    private EditText ExchangeRateET;

    public FirebaseAuth mAuth;
    private TextWatcher Watcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        //Buttons
        findViewById(R.id.AddExpenseBtn).setOnClickListener(this);

        //Views
        EndAmountTV = (TextView) findViewById(R.id.EndAmountTV);
        ExchangeRateET = (EditText) findViewById(R.id.ExchangeRateET);
        ExpenseAmountET = (EditText) findViewById(R.id.ExpenseAmountET);

        //On Text Change
        Watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(ExpenseAmountET.getText().toString()) || TextUtils.isEmpty(ExchangeRateET.getText().toString())){
                    Toast.makeText(AddExpenseActivity.this, "Please Enter an Amount", Toast.LENGTH_SHORT).show();
                    EndAmount = null;
                    EndAmountTV.setText("");
                }else{
                    OriginalAmount = Float.valueOf(ExpenseAmountET.getText().toString());
                    ExchangeRate = Float.valueOf(ExchangeRateET.getText().toString());
                    EndAmount = OriginalAmount * ExchangeRate;
                    EndAmountTV.setText(GroupCurrencyID + " " + String.format("%.2f", EndAmount));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        ExpenseAmountET.addTextChangedListener(Watcher);
        ExchangeRateET.addTextChangedListener(Watcher);

        //List View
        listViewMembers = (ListView) findViewById(R.id.listViewMembers);

        //Handle bundle extras
        Bundle b = getIntent().getExtras();
        GroupId = b.getCharSequence("GroupId").toString();
        GroupName = b.getCharSequence("GroupName").toString();
        GroupCurrencyID = b.getCharSequence("GroupCurrencyID").toString();

        //Currency Spinner
        CurrencySpinner = (Spinner) findViewById(R.id.currencySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_arrays, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CurrencySpinner.setAdapter(adapter);
        CurrencySpinner.setSelection(adapter.getPosition(GroupCurrencyID));
        CurrencySpinner.setOnItemSelectedListener(this);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Add Expense to " + GroupName);


        // Generate payer list options
        GenerateMembers();

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent parentIntent = NavUtils.getParentActivityIntent(this);
            if (parentIntent == null) {
                finish();
                return true;
            } else {
                parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(parentIntent);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        addExpense();


    }

    protected void addExpense() {
        EditText titleField = (EditText) findViewById(R.id.ExpenseTitleET);
        EditText descriptionField = (EditText) findViewById(R.id.ExpenseDescriptionET);
        EditText amountField = (EditText) findViewById(R.id.ExpenseAmountET);
        TextView EndAmountTV = (TextView) findViewById(R.id.EndAmountTV);

        final String title = titleField.getText().toString();
        final String description = descriptionField.getText().toString();
//        final Float totalAmount = Float.valueOf(EndAmountTV.getText().toString());
        final Float totalAmount = EndAmount;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUid = currentUser.getUid();

        final String CurrencyID = CurrencySpinner.getSelectedItem().toString();

        //Get Owner UserName from database
        DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users");
        Query UserNameQuery = uDatabase.child(currentUid);
        UserNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String OwnerName = user.Name;

                // Get emails of selected members
                ArrayList<String> selectedMembers = GeneratePayers();

                //Equal Splitting Calculation
                Float EachAmount = EqualSplitCalc(totalAmount, selectedMembers.size());

                // Prepare Data for Expense Database
                DatabaseReference eDatabase = FirebaseDatabase.getInstance().getReference("expenses");
                String expenseId = eDatabase.push().getKey();
                Map<String, Float> selectedMembersData = new HashMap<String, Float>();
                for (String member : selectedMembers) {
                    selectedMembersData.put(member, EachAmount);
                    //Add expense ID to User
                    AddExpenseToUser(member, expenseId);
                }
                //Add ExpenseID to currentUser if not done
                AddExpenseToUser(currentUid, expenseId);
                //Add Expense to Expense Database
                Expenses expense = new Expenses(title, description, totalAmount, currentUid, OwnerName, selectedMembersData, GroupName, GroupId, CurrencyID, ExchangeRate);
                eDatabase.child(expenseId).setValue(expense);

                //Add Expense to Group Database
                AddExpenseToGroup(GroupId, expenseId);

                //update total Amount owed to the group in the group database
                updateTotalAmount(currentUid, selectedMembersData, totalAmount, GroupId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    protected void AddExpenseToGroup(String GroupID, final String ExpenseID) {
        final DatabaseReference gDatabase = FirebaseDatabase.getInstance().getReference("groups").child(GroupID);
        Query GroupQuery = gDatabase;
        GroupQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Groups group = dataSnapshot.getValue(Groups.class);
                List<String> ExpenseList = new ArrayList<String>();
                if (group.Expenses != null) {
                    ExpenseList = group.Expenses;
                }
                ExpenseList.add(ExpenseID);

                gDatabase.child("Expenses").setValue(ExpenseList);
                Intent myIntent = new Intent(AddExpenseActivity.this, GroupActivity.class);
                Bundle b = new Bundle();
                b.putString("GroupId", GroupId);
                b.putString("GroupName", GroupName);
                b.putString("GroupCurrencyID", GroupCurrencyID);
                myIntent.putExtras(b);
                startActivity(myIntent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void AddExpenseToUser(String Member, final String ExpenseID) {
        final DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users").child(Member);
        Query UserQuery = uDatabase;
        UserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                List<String> ExpenseList = new ArrayList<String>();
                if (user.Expenses != null) {
                    ExpenseList = user.Expenses;
                }
                ExpenseList.add(ExpenseID);
                uDatabase.child("Expenses").setValue(ExpenseList);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected Float EqualSplitCalc(Float TotalAmount, int PayerNumbers) {
        Float EachAmount = format(TotalAmount / PayerNumbers);
        return EachAmount;
    }

    protected Float format(Float n) {
        Float fn = Math.round(n * 100.00f) / 100.00f;
        return fn;
    }

    protected ArrayList<String> GeneratePayers() {
        //Check Which Friends are selected
        SparseBooleanArray checked = listViewMembers.getCheckedItemPositions();

        ArrayList<String> selectedMembers = new ArrayList<String>();
        for (int c = 0; c < checked.size(); c++) {

            int position = checked.keyAt(c);
            if (checked.valueAt(c)) {

                selectedMembers.add(adapter.getItem(position).getDescription());


            }
        }
        return selectedMembers;
    }

    // Generate list of payers from group
    protected void GenerateMembers() {
        DatabaseReference gDatabase = FirebaseDatabase.getInstance().getReference("groups");
        Query UserQuery = gDatabase.child(GroupId).child("participants");
        UserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String Entry = child.getKey().toString();
                    MembersUidList.add(Entry);
                }

                // Retrieve name data
                final ArrayList<Items> MembersDataList = new ArrayList<Items>();
                for (final String uid : MembersUidList) {
                    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("users");
                    Query UserQuery2 = uDatabase.orderByKey().equalTo(uid);
                    UserQuery2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                String Name = child.child("Name").getValue().toString();
                                MembersDataList.add(new Items(Name, uid, null));
                            }
                            generate(MembersDataList);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    public void generate(ArrayList<Items> MembersDataList) {
        adapter = new MyAdapterMembers(this, MembersDataList);
        listViewMembers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listViewMembers.setAdapter(adapter);

    }

    public void updateTotalAmount(final String currentUid, final Map<String, Float> selectedMembersData, final Float totalAmount, String groupId) {
        final DatabaseReference gDatabase = FirebaseDatabase.getInstance().getReference("groups").child(groupId);
        Query UserMapQuery = gDatabase;
        UserMapQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Groups group = dataSnapshot.getValue(Groups.class);
                Map<String, Float> ParticipantData = group.participants;
                for (Map.Entry<String, Float> Data : selectedMembersData.entrySet()) {
                    String UID = Data.getKey();
                    Float UpdatedAmount = ParticipantData.get(UID) - selectedMembersData.get(UID);
                    ParticipantData.put(UID, UpdatedAmount);
                }
                Float CurrentUserAmount = ParticipantData.get(currentUid) + totalAmount;
                ParticipantData.put(currentUid, CurrentUserAmount);
                gDatabase.child("participants").setValue(ParticipantData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String OriginalAmount = ExpenseAmountET.getText().toString();
        String CurrencyID = parent.getItemAtPosition(position).toString();
        if (CurrencyID.equals(GroupCurrencyID)){
            ExchangeRateET.setEnabled(false);
            ExchangeRateET.setFocusable(false);
        }else{
            ExchangeRateET.setEnabled(true);
            ExchangeRateET.setFocusable(true);
        }
        if (TextUtils.isEmpty(OriginalAmount)) {
            Toast.makeText(parent.getContext(), "Please Enter an Amount", Toast.LENGTH_SHORT).show();

            UpdateExchangeRateWithoutAmount u = new UpdateExchangeRateWithoutAmount();
            u.execute(CurrencyID);
        } else {

            UpdateExchangeRate Update = new UpdateExchangeRate();
            Update.execute(CurrencyID);

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class UpdateExchangeRate extends AsyncTask<String, Void, JSONObject> {

        public String CurrencyID;
        public EditText ExchangeRateET;
        public EditText ExpenseAmountET;
        public TextView EndAmountTV;

        public UpdateExchangeRate() {
            ExchangeRateET = (EditText) findViewById(R.id.ExchangeRateET);
            ExpenseAmountET = (EditText) findViewById(R.id.ExpenseAmountET);
            EndAmountTV = (TextView) findViewById(R.id.EndAmountTV);

        }

        public void onPreExecute() {
        }

        public JSONObject doInBackground(String... params) {
            CurrencyID = params[0];
            String url = "http://free.currencyconverterapi.com/api/v3/convert?q=" + CurrencyID + "_" + GroupCurrencyID + "&compact=y";

            JSONParser JP = new JSONParser();

            // Getting JSON from URL
            JSONObject json = JP.getJSONFromUrl(url);

            return json;

        }

        public void onProgressUpdate() {

        }

        public void onPostExecute(JSONObject json) {
            try {
                // Getting JSON Array
                JSONObject c = json.getJSONObject(CurrencyID + "_" + GroupCurrencyID);

                // Storing  JSON item in a Variable
                Float rate = Float.valueOf(c.getString("val"));
                ExchangeRateET.setText(String.format("%.3f", rate));
                Float OriginalAmount = Float.valueOf(ExpenseAmountET.getText().toString());
                Float EndAmount = OriginalAmount * rate;
                EndAmountTV.setText(GroupCurrencyID + " " + String.format("%.2f", EndAmount));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public class UpdateExchangeRateWithoutAmount extends AsyncTask<String, Void, JSONObject> {

        public String CurrencyID;
        public EditText ExchangeRateET;

        public UpdateExchangeRateWithoutAmount() {
            ExchangeRateET = (EditText) findViewById(R.id.ExchangeRateET);

        }

        public void onPreExecute() {
        }

        public JSONObject doInBackground(String... params) {
            CurrencyID = params[0];
            String url = "http://free.currencyconverterapi.com/api/v3/convert?q=" + CurrencyID + "_" + GroupCurrencyID + "&compact=y";

            JSONParser JP = new JSONParser();

            // Getting JSON from URL
            JSONObject json = JP.getJSONFromUrl(url);

            return json;

        }

        public void onProgressUpdate() {

        }

        public void onPostExecute(JSONObject json) {
            try {
                // Getting JSON Array
                JSONObject c = json.getJSONObject(CurrencyID + "_" + GroupCurrencyID);

                // Storing  JSON item in a Variable
                Float rate = Float.valueOf(c.getString("val"));
                ExchangeRateET.setText(String.format("%.3f", rate));

                if (CurrencyID.equals(GroupCurrencyID)){
                    ExchangeRateET.setEnabled(false);
                    ExchangeRateET.setFocusable(false);
                }else{
                    ExchangeRateET.setEnabled(true);
                    ExchangeRateET.setFocusable(true);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}