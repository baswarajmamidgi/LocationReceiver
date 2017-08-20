package com.example.baswarajmamidgi.locationreceiver;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 class Constants {


     public static final String DATABASE_NAME = "datastorage";
     public static final int DATABASE_VERSION = 1;
     public static final String Favourites = "favouritetable";
     public static final String KEY_ID2 = "_id2";
     public static final String favouritelist = "favouritelist";



 }

     public class Databasehelper extends SQLiteOpenHelper {

         private static final String CREATE_TABLE2 = "create table " + Constants.Favourites + " (" +
                 Constants.KEY_ID2 + " integer primary key autoincrement, " + Constants.favouritelist + " text not null);";


         public Databasehelper(Context context) {
             super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
         }

         @Override
         public void onCreate(SQLiteDatabase db) {

             db.execSQL(CREATE_TABLE2);


         }

         @Override
         public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

         }
     }

