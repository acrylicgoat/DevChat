/**
 * Copyright (c) 2013 Acrylic Goat Software
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 3 (LGPL).  See LICENSE.txt for details.
 */
package com.acrylicgoat.devchat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.acrylicgoat.devchat.beans.DevNote;
import com.acrylicgoat.devchat.beans.Developer;
import com.acrylicgoat.devchat.provider.DatabaseHelper;
import com.acrylicgoat.devchat.provider.Notes;
import com.acrylicgoat.devchat.util.DevChatUtil;

/**
 * @author ed woodward
 * 
 * Activity to display notes taken.  They can be filtered by developer
 *
 */
public class DataTableActivity extends Activity
{
    ArrayList<DevNote> notes;
    ArrayList<Developer> devs;
    private static final int MENUITEM = Menu.FIRST;
    ActionBar aBar;
    Context context;
    String currentOwner;
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datatable);
        aBar = getActionBar();
        aBar.setTitle(getString(R.string.app_name) + " - Everyone");
        aBar.setDisplayHomeAsUpEnabled(true);
     // read database
        getDevelopers();
        if(devs != null)
        {
        	getDeveloperNotes(devs.get(0).getName());
        	aBar.setTitle(getString(R.string.app_name) + " - " + devs.get(0).getName());
            currentOwner = devs.get(0).getName();
        }
        else
        {
        	readDB();
        }
        context = this;
        setupTable();
    }

    private void setupTable()
    {
        //get table row objects
        TableLayout table = (TableLayout) this.findViewById(R.id.tablelayout);
        LayoutInflater inflater = (LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE);
        table.removeAllViews();

        //loop through data and create rows
        if(notes.size() > 0)
        {
            for (int i = 0; i < notes.size(); i++)
            {
                View fullRow = inflater.inflate(R.layout.table_row, null, false);
                TextView date = (TextView) fullRow.findViewById(R.id.date);
                TextView devName = (TextView) fullRow.findViewById(R.id.devName);
                TextView note = (TextView) fullRow.findViewById(R.id.description);
                note.setAutoLinkMask(Linkify.ALL);
                DevNote dev = notes.get(i);
                date.setText(dev.getDate());
                devName.setText(dev.getDevName());
                note.setText(dev.getNote());
                Linkify.addLinks(note, Linkify.ALL);
                fullRow.setOnLongClickListener(new View.OnLongClickListener() {

                    //@Override
                    public boolean onLongClick(View v) {
                        TableRow SelectedRow;


                        SelectedRow = (TableRow)v;

                        TextView date = (TextView) SelectedRow.findViewById(R.id.date);
                        TextView devName = (TextView) SelectedRow.findViewById(R.id.devName);
                        final String dateStr = date.getText().toString();
                        final String name = devName.getText().toString();

                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setTitle("Delete Row");
                        alertDialog.setMessage("Delete selected row dated " + dateStr + "?");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                deleteNote(dateStr, name);

                            }
                        });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                //do nothing

                            } });
                        alertDialog.show();
                        return false;
                    }
                });
                table.addView(fullRow);

            }

        }
        else
        {
            Toast.makeText(DataTableActivity.this, "No data to display",  Toast.LENGTH_LONG).show();
        }

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_devtable, menu);

        if(devs != null && devs.size() > 0)
        {
            for (int i = 0; i < devs.size(); i++)
            {
                Developer dev = devs.get(i);
                
                menu.add(0, MENUITEM, 0, dev.getName()).setIcon(R.drawable.dev);
                
            }
        }
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();
        getMenuInflater().inflate(R.menu.activity_devtable, menu);
        getDevelopers();
        if(devs != null && devs.size() > 0)
        {
            for (int i = 0; i < devs.size(); i++)
            {
                Developer dev = devs.get(i);
                menu.add(0, MENUITEM, 0, dev.getName());
                
            }
        }
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        String title = (String) item.getTitle();
        if(item.getItemId() == R.id.share)
        {
            String data = createTSV();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(getString(R.string.mimetype_text));

            if(!data.equals(""))
            {
                String today = DevChatUtil.getTodaysDate();
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.scrumnotes_data) + " " + today);
                intent.putExtra(Intent.EXTRA_TEXT, data + "\n\n" + getString(R.string.shared_via));

                Intent chooser = Intent.createChooser(intent, getString(R.string.tell_friend) + getString(R.string.scrumnotes_data));
                startActivity(chooser);
            }
            else
            {
                Toast.makeText(DataTableActivity.this, getString(R.string.no_data_msg),  Toast.LENGTH_LONG).show();
            }
        }
        else if(item.getItemId() == R.id.everyone)
        {
            readDB();
            setupTable();
            aBar.setTitle(getString(R.string.app_name) +" - Everyone");
        }
        else if(item.getItemId() == R.id.export)
        {
            exportReport();
        }
        else if(item.getItemId() == R.id.importFile)
        {
            importFile();
        }
        else if(item.getItemId() == android.R.id.home)
        {
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
        }
        else
        {
            getDeveloperNotes(title);
            setupTable();
            currentOwner = title;
            aBar.setTitle(getString(R.string.app_name) +" - " + title);
        }

        return true;
    }
    
    private void getDeveloperNotes(String name)
    {
        notes = new ArrayList<>();
        String sql = "select date(notes_date) as notes_date, notes_owner, notes_note from notes where notes_owner='" + name + "' order by date(notes_date) desc";
        DatabaseHelper dbHelper = new DatabaseHelper(this.getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        int noteColumn = cursor.getColumnIndex(Notes.NOTE);
        int devColumn = cursor.getColumnIndex(Notes.OWNER);
        int dateColumn = cursor.getColumnIndex(Notes.DATE);
        
        if(cursor.getCount()>0)
        {
            cursor.moveToNext();
            do
            {
                DevNote dev = new DevNote();
                dev.setDevName(cursor.getString(devColumn));
                dev.setDate(cursor.getString(dateColumn));
                dev.setNote(cursor.getString(noteColumn));
                
                notes.add(dev);
                
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        
    }
    
    private void getDevelopers()
    {
        //Log.d("DatTbleActy.getDevs()","called");
         //devs = DBUtils.readCursorIntoList(getContentResolver().query(Developers.CONTENT_URI, null, null, null, null));
        devs = new ArrayList<>();
        String sql = "select distinct notes_owner from notes";
        DatabaseHelper dbHelper = new DatabaseHelper(this.getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        int devColumn = cursor.getColumnIndex(Notes.OWNER);
        if(cursor.getCount()>0)
        {
            cursor.moveToNext();
            do
            {
                Developer dev = new Developer();
                dev.setName(cursor.getString(devColumn));


                devs.add(dev);

            }while(cursor.moveToNext());
        }
        cursor.close();
		db.close();
		
          
         Collections.sort(devs);
              
    }
    
    private void readDB()
    {
        notes = new ArrayList<>();
        String sqlString = "select date(notes_date) as notes_date, notes_owner, notes_note from notes order by date(notes_date) desc";
        DatabaseHelper dbHelper = new DatabaseHelper(this.getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlString, null);
        int noteColumn = cursor.getColumnIndex(Notes.NOTE);
        int devColumn = cursor.getColumnIndex(Notes.OWNER);
        int dateColumn = cursor.getColumnIndex(Notes.DATE);
        
        if(cursor.getCount()>0)
        {
            cursor.moveToNext();
            do
            {
                DevNote dev = new DevNote();
                dev.setDevName(cursor.getString(devColumn));
                dev.setDate(cursor.getString(dateColumn));
                dev.setNote(cursor.getString(noteColumn));
                
                notes.add(dev);
                
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

    }
    
    private void exportReport()
    {
        File snDir = new File(Environment.getExternalStorageDirectory(), "DevChat/");
        if(!snDir.exists())
        {
            snDir.mkdir();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("dcExport");
        sb.append(DevChatUtil.getFileDate());
        sb.append(".tsv");
        //String fileName = "snExport.csv";
        File file = new File(snDir, sb.toString());
        String text = createTSV();
        Log.d("DataTableActivity","file: "+ text);
        PrintWriter pw = null;
        
        try
        {
            pw = new PrintWriter(file);
            pw.write(text);
            pw.flush();
            Toast.makeText(this, sb.toString() + " saved to DevChat folder.", Toast.LENGTH_LONG).show();
        }
        catch (FileNotFoundException e)
        {
            //Log.d("DataTableActivity", "Error exporting note: " + e.toString(), e);
            Toast.makeText(this, "Error exporting note: " + e.toString(), Toast.LENGTH_LONG).show();
        }
        finally
        {
            if(pw != null)
            {
                pw.close();
            }
        }
    }
    
    private String createTSV()
    {
        StringBuilder csv = new StringBuilder();
        for (int i = 0; i < notes.size(); i++)
        {
            DevNote dev = notes.get(i);
            csv.append(dev.getDate());
            csv.append("\t");
            csv.append(dev.getDevName());
            csv.append("\t");
            csv.append(removeNewLines(dev.getNote()));
            csv.append("\n");
            
        }
        
        return csv.toString();
    }
    
    private void importFile()
    {
        //get file
        ContentValues values = new ContentValues();
        File snDir = new File(Environment.getExternalStorageDirectory(), "DevChat/");
        File file = new File(snDir, "dcExport.tsv");
        BufferedReader br = null;;
        
        try
        {
          //loop through lines
            br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) 
            {
                //Log.d("DataTableActivity.insertFile()", "line = " + line);
                String[] parts = line.split("\t");
                //Log.d("DataTableActivity.insertFile()", "parts = " + parts[0] + " " + parts[1] + " " + parts[2]);
                values.put(Notes.DATE, parts[0]);
                values.put(Notes.NOTE, addNewLines(parts[2]));
                values.put(Notes.OWNER, parts[1]);
                getContentResolver().insert(Notes.CONTENT_URI, values);
                
            }
            readDB();
            setupTable();
        }
        catch(Exception ioe)
        {
            //Log.d("DataTableActivity", "Exception: " + ioe.toString());
        }
        finally
        {
            try
            {
                if(br != null)
                {
                    br.close();
                }
            }
            catch (IOException e)
            {
                //Log.d("DataTableActivity", "Exception: " + e.toString());
            }
            
        }
        
    }
    
    private String removeNewLines(String strToEdit)
    {
        return strToEdit.replace("\n"," ");
    }
    
    private String addNewLines(String strToEdit)
    {
        StringBuilder sb = new StringBuilder();
        
        int index= strToEdit.indexOf("Today:");
        if(index > -1)
        {
            sb.append(strToEdit.substring(0,index-1));
            sb.append("\n\n");
            sb.append(strToEdit.substring(index));
        }
        else
        {
            return strToEdit;
        }
        
        return sb.toString();
    }

    private void deleteNote(String date, String name)
    {
        getContentResolver().delete(Notes.CONTENT_URI, "date(notes_date)='"+date+"' and notes_owner='"+name+"'" , null);
        getDeveloperNotes(currentOwner);
        setupTable();
        aBar.setTitle("Dev Chat -  " + currentOwner);
    }

}
