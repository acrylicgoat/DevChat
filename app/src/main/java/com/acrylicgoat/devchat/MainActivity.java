/**
 * Copyright (c) 2013 Acrylic Goat Software
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 3 (LGPL).  See LICENSE.txt for details.
 */
package com.acrylicgoat.devchat;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Calendar;
import java.util.Collections;
//import java.util.GregorianCalendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.*;
import com.acrylicgoat.devchat.beans.Developer;
import com.acrylicgoat.devchat.provider.DBUtils;
import com.acrylicgoat.devchat.provider.DatabaseHelper;
import com.acrylicgoat.devchat.provider.Developers;
import com.acrylicgoat.devchat.provider.Notes;
//import com.acrylicgoat.devchat.R;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;


/**
 * @author ed woodward
 *
 * Opening activity for app
 *
 */
public class MainActivity extends Activity
{
    private Cursor cursor;
    private EditText today;
    private String currentOwner;
    private ImageButton yesterday;
    ActionBar aBar;
    ArrayList<Developer> devs;
    private TextView devName;
    private static final int MENUITEM = Menu.FIRST;
    SharedPreferences sharedPref;
    private List<HashMap<String,String>> navTitles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    String[] from = { "nav_icon","nav_item" };
    int[] to = { R.id.nav_icon , R.id.nav_item};



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.activity_main);

        sharedPref = getSharedPreferences("com.acrylicgoat.devchat",MODE_PRIVATE);
        if(savedInstanceState != null)
        {
            currentOwner = savedInstanceState.getString("currentOwner");
        }
        if(currentOwner == null || currentOwner.equals(""))
        {
            currentOwner = sharedPref.getString("currentOwner", "");
        }
        aBar = this.getActionBar();

        today = (EditText) findViewById(R.id.editToday);
        today.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Linkify.addLinks(today, Linkify.ALL);

            }
        });
        devName = (TextView) findViewById(R.id.devName);
        yesterday = (ImageButton)findViewById(R.id.calendarButton);
        yesterday.setOnClickListener(new OnClickListener()
        {

            public void onClick(View v)
            {

                displayPopup();

            }
        });

        String[] items = getResources().getStringArray(R.array.nav_list);
        setDrawer(items);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);
        SimpleAdapter sAdapter = new SimpleAdapter(this,navTitles, R.layout.nav_drawer,from,to);

        // Set the adapter for the list view
        //drawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, navTitles));
        // Set the list's click listener
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(getString(R.string.app_name));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(getString(R.string.app_name));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);
        aBar.setTitle(getString(R.string.app_name));
        //aBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        aBar.setDisplayHomeAsUpEnabled(true);
        aBar.setHomeButtonEnabled(true);
        drawerList.setAdapter(sAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        readDB();
        if(devs != null && devs.size() > 0)
        {
            for (int i = 0; i < devs.size(); i++)
            {
                Developer dev = devs.get(i);
                if(i == 0 && (currentOwner == null || currentOwner.equals("")))
                {
                    currentOwner = dev.getName();
                }
                menu.add(0, MENUITEM, 0, dev.getName()).setIcon(R.drawable.dev);

            }
        }
        getOwner(currentOwner);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();
        getMenuInflater().inflate(R.menu.activity_main, menu);
        readDB();
        if(devs != null && devs.size() > 0)
        {
            for (int i = 0; i < devs.size(); i++)
            {
                Developer dev = devs.get(i);
                menu.add(0, MENUITEM, 0, dev.getName());

            }
        }
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.save).setVisible(!drawerOpen);
        return true;
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (drawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        String title = (String) item.getTitle();
        if(item.getItemId() == R.id.save)
        {
            saveNote();
            return true;
        }

        else
        {
            currentOwner=title;
            //Log.d("MainActivity", "currentOwner = " + currentOwner);
            getOwner(currentOwner);
        }

        return true;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putString("currentOwner", currentOwner);
        ed.commit();
        saveNote();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        readDB();
        if(Build.VERSION.SDK_INT > 10 && devs != null && devs.size() > 0)
        {
            invalidateOptionsMenu();
        }
    }

    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //Log.d("ViewLenses.onSaveInstanceState()", "saving data");
        outState.putString("currentOwner", currentOwner);

    }

    private void displayPopup()
    {
        //retrieve yesterday's data
        String yData = getYesterday(currentOwner);
        if(yData.equals(""))
        {
            yData = getString(R.string.no_yesterday);
        }
        //display in popup
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Last Chat");
        alertDialog.setMessage(yData);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Done", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                //do nothing

            } });
        alertDialog.show();
    }

    private void getOwner(String owner)
    {
        //Log.d("MainActivity", "getOwner() called: " + owner);
        getToday(owner);
        devName.setText(currentOwner);

    }

    private String getYesterday(String owner)
    {
        //Log.d("MainActivity", "getYesterday() called: " + owner);
        StringBuilder sb = new StringBuilder();
        String results = "";
        sb.append("select notes_note, date(notes_date) as notes_date from notes where notes_owner='");
        sb.append(currentOwner);

        sb.append("' and date(notes_date)<=date('now','-1 day','localtime') order by date(notes_date) desc");

        DatabaseHelper dbHelper = new DatabaseHelper(this.getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery(sb.toString(), null);
        if(cursor.getCount()>0)
        {
            cursor.moveToNext();
            int notesColumn = cursor.getColumnIndex(Notes.NOTE);
            int dateColumn = cursor.getColumnIndex(Notes.DATE);
            //Log.d("MainActivity.getYesterday()", "notesColumn: " + notesColumn);
            results = cursor.getString(dateColumn) + " - " + cursor.getString(notesColumn);
        }


        cursor.close();
        db.close();
        return results;
    }

    private void getToday(String owner)
    {
        Log.d("MainActivity", "getToday() called: " + owner);

        DatabaseHelper dbHelper = new DatabaseHelper(this.getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery(getTodaySQL(), null);
        if(cursor.getCount()>0)
        {
            cursor.moveToNext();
            int notesColumn = cursor.getColumnIndex(Notes.NOTE);
            today.setText(cursor.getString(notesColumn));

        }
        else
        {
            if(devs.size() > 0)
            {
                today.setText("");
            }
            else
            {
                today.setText(getString(R.string.no_devs));

            }

        }
        Linkify.addLinks(today, Linkify.ALL);
        cursor.close();
        db.close();
    }

    private void saveNote()
    {
        ContentValues values = new ContentValues();

        String text = today.getText().toString() + " ";
        //Log.d("NoteEditorActivity", "note: " + text);
        int length = text.length();

        if (length == 0 || text.contains("To get started, select Tools") || text.equals("") || text.equals(" "))
        {
            //Toast.makeText(this, "Nothing to save.", Toast.LENGTH_SHORT).show();
            return;
        }

        values.put(Notes.NOTE, text);
        values.put(Notes.OWNER, currentOwner);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        values.put(Notes.DATE, dateFormat.format(date));

        //check if a note already exists for today
        DatabaseHelper dbHelper = new DatabaseHelper(this.getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery(getTodaySQL(), null);
        if(cursor.getCount()>0)
        {
            //Log.d("MainActivity", "saveNote(): doing update ");
            StringBuilder sb = new StringBuilder();
            sb.append("update notes set notes_note = '");
            sb.append(escape(text));
            sb.append("' where notes_owner='");
            sb.append(currentOwner);
            sb.append("' and date(notes_date) = strftime('%Y-%m-%d', 'now','localtime')");
            dbHelper.getReadableDatabase().execSQL(sb.toString());
        }
        else
        {
            getContentResolver().insert(Notes.CONTENT_URI, values);
        }
        cursor.close();
        db.close();

    }

    private String getTodaySQL()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("select notes_note from notes where notes_owner='");
        sb.append(currentOwner);
        sb.append("' and date(notes_date) = strftime('%Y-%m-%d', 'now','localtime')");
        Log.d("SQL", sb.toString());
        return sb.toString();
    }

    private String escape(String text)
    {
        String returnVal = "";

        returnVal = text.replace("'", "''");

        return returnVal;
    }

    private void readDB()
    {
        devs = DBUtils.readCursorIntoList(getContentResolver().query(Developers.CONTENT_URI, null, null, null, null));

        Collections.sort(devs);

    }

    private void selectItem(int position)
    {
        switch (position)
        {
            case 0:
                drawerLayout.closeDrawers();
                break;
            case 1:
                Intent devIntent = new Intent(getApplicationContext(), DevActivity.class);
                startActivity(devIntent);
                break;

            case 2:
                Intent reportIntent = new Intent(getApplicationContext(), DataTableActivity.class);
                startActivity(reportIntent);
                break;
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id)
        {
            selectItem(position);
        }
    }

    private void setDrawer(String[] items)
    {
        HashMap hm1 = new HashMap();
        hm1.put("nav_icon",Integer.toString(R.drawable.home));
        hm1.put("nav_item",items[0]);

        HashMap hm2 = new HashMap();
        hm2.put("nav_icon",Integer.toString(R.drawable.dev));
        hm2.put("nav_item",items[1]);

        HashMap hm3 = new HashMap();
        hm3.put("nav_icon",Integer.toString(R.drawable.dev));
        hm3.put("nav_item",items[2]);

        navTitles = new ArrayList();

        navTitles.add(hm1);
        navTitles.add(hm2);
        navTitles.add(hm3);
    }


}
