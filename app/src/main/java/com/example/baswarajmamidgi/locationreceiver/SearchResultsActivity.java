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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity{
    MapsActivity activity;
    ArrayList<String> searchresults = null;
    Mydatabase mydatabase;
    ArrayList<String> busstops;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.black));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle("Search results");
        setSupportActionBar(toolbar);

        activity = new MapsActivity();
        Intent searchintent = getIntent();
        mydatabase=new Mydatabase(this);
        busstops=new ArrayList<>();
       busstops= MapsActivity.arrayList;
        searchresults = new ArrayList<>();
        String query = searchintent.getStringExtra("data");
        Log.i("log query",query);
        for(String result:busstops)
        {
            if(result.toLowerCase().contains(query.toLowerCase()))
            {
                String resultset[]=result.split(",");
                searchresults.add(resultset[0]);
                Log.i("log",resultset[0]);

            }
        }
        if(searchresults.isEmpty())
        {
            TextView textView= (TextView) findViewById(R.id.searchresult);
            textView.setVisibility(View.VISIBLE);
            textView.setText("No such stop available");
        }
        final ListView listView = (ListView) findViewById(R.id.listview);
        Customadapter adapter=new Customadapter(searchresults,this);
        listView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.showallbuses,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.showbuses)
        {
            if(searchresults!=null)
            {
                Intent i=new Intent(this,MapsActivity.class);
                i.putStringArrayListExtra("searchresults",searchresults);
                startActivity(i);

            }
        }
        return super.onOptionsItemSelected(item);
    }


}





