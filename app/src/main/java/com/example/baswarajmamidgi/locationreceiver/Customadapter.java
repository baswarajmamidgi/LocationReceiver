package com.example.baswarajmamidgi.locationreceiver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by baswarajmamidgi on 29/10/16.
 */

public class Customadapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    private Mydatabase mydatabase;



    public Customadapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return pos;
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }


    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View view = convertView;
        mydatabase=new Mydatabase(context);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.favourites, null);
        }
        TextView listItemText = (TextView)view.findViewById(R.id.textView5);
        listItemText.setText(list.get(position));
        listItemText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context,MapsActivity.class);
                i.putExtra("result",list.get(position));
                i.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                context.startActivity(i);
                //((Activity)context).finish();
            }
        });
        final ImageView button = (ImageView)view.findViewById(R.id.imageView);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
                    button.setImageDrawable(context.getDrawable(R.drawable.favouritered));
                }else
                {
                    button.setImageResource(R.drawable.favouritered);
                }
                final ArrayList<String> favlist=mydatabase.getFavourites();

                if(favlist.contains(list.get(position)))
                {
                    return;
                }
                Log.i("log","add to favourites "+list.get(position));
                mydatabase.addfavourite(list.get(position));
                Toast.makeText(context, list.get(position) +" added to favourites", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }
}
