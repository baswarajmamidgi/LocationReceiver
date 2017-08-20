package com.example.baswarajmamidgi.locationreceiver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Buseslist extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    private ArrayList<String> arraylist;
    Mydatabase mydatabase;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buseslist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.black));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);

        ListView listView = (ListView) findViewById(R.id.buslist);
        arraylist = new ArrayList<>();
        mydatabase = new Mydatabase(this);
          FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        Intent i = getIntent();
        String activity = i.getStringExtra("activity");
        Log.i("log", activity);
        toolbar.setTitle(activity);


        if (activity.equals("Favourites")) {
            arraylist = mydatabase.getFavourites();
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arraylist);
            if (arraylist.isEmpty()) {
                Log.i("log","no favourites");
                TextView text1 = (TextView) findViewById(R.id.textView7);
                TextView text2 = (TextView) findViewById(R.id.textView9);
                text1.setVisibility(View.VISIBLE);
                text2.setVisibility(View.VISIBLE);
                return;
            }

        } else {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arraylist);
            databaseReference = firebaseDatabase.getReference().child("routeno");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String message = snapshot.getValue(String.class);
                        arraylist.add(message);
                        adapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                i.putExtra("result", arraylist.get(position));
                startActivity(i);
                finishAffinity();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favourite,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.clearfav)
        {
            mydatabase.clearfavouritetable();
            Intent i=new Intent(this,MapsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
