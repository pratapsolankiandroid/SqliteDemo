package com.example.home.sqlitedemo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView list;

    Database datahelper;

    CustomAdapter adapter;
    List<Contact> contacts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        dataBinder();
    }

    public void dataBinder() {
        contacts = datahelper.getAllContacts();
        adapter = new CustomAdapter(MainActivity.this, contacts);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        list.setEmptyView(findViewById(R.id.empty));
    }

    public void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        list = (ListView) findViewById(R.id.main_lv_list);
        datahelper = new Database(getApplicationContext());

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent in = new Intent(MainActivity.this, DetailsActivity.class);
                in.putExtra("id", contacts.get(pos).getID());
                in.putExtra("name", contacts.get(pos).getName());
                in.putExtra("mobile", contacts.get(pos).getPhoneNumber());
                in.putExtra("image", contacts.get(pos).get_image());
                startActivity(in);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            dataBinder();
        } catch (Exception e) {
            Log.d("error", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_add) {
            Intent in = new Intent(MainActivity.this, AddActivity.class);
            startActivity(in);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
