package com.project.splitz;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Main2Activity extends Activity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ListView listView1 = (ListView) findViewById(R.id.listView1);

        product[] items = {
                new product(1, "Milk", 21.50),
                new product(2, "Butter", 15.99),
                new product(3, "Yogurt", 14.90),
                new product(4, "Toothpaste", 7.99),
                new product(5, "Ice Cream", 10.00),
        };

        ArrayAdapter<product> adapter = new ArrayAdapter<product>(this,
                android.R.layout.simple_list_item_1, items);

        listView1.setAdapter(adapter);

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                String item = ((TextView)view).getText().toString();

                Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();

            }
        });

    }

    @Override
    public void onClick(View v) {

    }
}
