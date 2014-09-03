/**
 * Copyright (c) 2013 Acrylic Goat Software
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 3 (LGPL).  See LICENSE.txt for details.
 */
package com.acrylicgoat.devchat.provider;

import java.util.HashMap;



import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * @author ed woodward
 * 
 * Provider to save daily note for each developer
 *
 */
public class NotesProvider extends ContentProvider
{
    public static final String AUTHORITY = "com.acrylicgoat.scrumnotes.providers.NotesProvider";
    /** notes table name */
    protected static final String NOTES_TABLE = "notes";
    /** Map of Notes table columns */
    private static HashMap<String, String> NotesProjectionMap;
    
    /** static section to initialize notes table map */
    static
    {
        NotesProjectionMap = new HashMap<String,String>();
        NotesProjectionMap.put(Notes.ID, Notes.ID);
        NotesProjectionMap.put(Notes.OWNER, Notes.OWNER);
        NotesProjectionMap.put(Notes.DATE, Notes.DATE);
        NotesProjectionMap.put(Notes.NOTE, Notes.NOTE);
    }
    
    /** Variable for database helper */
    private DatabaseHelper dbHelper;
    
    /**  Called when class created. initializes database helper*/
    @Override
    public boolean onCreate() 
    {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }
    

    /* (non-Javadoc)
     * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
     */
    @Override
    public int delete(Uri arg0, String arg1, String[] arg2)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see android.content.ContentProvider#getType(android.net.Uri)
     */
    @Override
    public String getType(Uri uri)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
     */
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(NOTES_TABLE, null, new ContentValues(values));
        db.close();
        return null;
    }


    /* (non-Javadoc)
     * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(NOTES_TABLE);
        qb.setProjectionMap(NotesProjectionMap);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }
    
    

    /* (non-Javadoc)
     * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.update(NOTES_TABLE, values, selection, selectionArgs);
    }

}