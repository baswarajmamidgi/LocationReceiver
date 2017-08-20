package com.example.baswarajmamidgi.locationreceiver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by baswarajmamidgi on 20/07/16.
 */
public class Mydatabase {
    private SQLiteDatabase db;
    private final Context context;
    private final Databasehelper dbhelper;
    public Mydatabase(Context c) {
        context = c;
        dbhelper = new Databasehelper(context);
    }


    public void addfavourite(String data)
    {
        db=dbhelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(Constants.favouritelist,data);
         db.insert(Constants.Favourites,null,values);
        db.close();
    }



    public void clearfavouritetable() {
        db=dbhelper.getWritableDatabase();
        db.delete(Constants.Favourites,null,null);
        db.close();
    }

    public ArrayList<String> getFavourites() {
        db = dbhelper.getWritableDatabase();
        String[] columns={Constants.KEY_ID2,Constants.favouritelist};
        Cursor cursor = db.query(Constants.Favourites,columns,null,null,null,null,null);
        cursor.moveToFirst();
        ArrayList<String> list=new ArrayList<>();
        while (!cursor.isAfterLast())
        {
            list.add(cursor.getString(cursor.getColumnIndex(Constants.favouritelist)));
            Log.i("log fav",cursor.getString(cursor.getColumnIndex(Constants.favouritelist)));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return  list;

    }

}
