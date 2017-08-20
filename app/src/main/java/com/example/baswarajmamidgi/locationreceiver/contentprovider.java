package com.example.baswarajmamidgi.locationreceiver;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by baswarajmamidgi on 16/12/16.
 */

public class contentprovider extends ContentProvider {
    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        ArrayList<String> busstops=MapsActivity.arrayList;
        ArrayList<String> allstops=new ArrayList<>();
        for(String stops:busstops)
        {
           String[] stopslist=stops.split(",");

                for(String name:stopslist)
                {
                    if(!allstops.contains(name.toLowerCase())) {
                        allstops.add(name.toLowerCase());
                    }
                }
        }

        MatrixCursor matrixCursor=new MatrixCursor(new String[] {BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID

        });
        if(allstops!=null)
        {
            String query=uri.getLastPathSegment().toString();
            int limit= Integer.parseInt(uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT));
            int length=allstops.size();
            for(int i=0; i<length && matrixCursor.getCount()<limit;i++)
            {
                String notes=allstops.get(i);
                if(notes.toLowerCase().contains(query.toLowerCase()))
                {
                    matrixCursor.addRow(new Object[] { i,notes.toLowerCase(),i});
                }
            }
        }
        return matrixCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
