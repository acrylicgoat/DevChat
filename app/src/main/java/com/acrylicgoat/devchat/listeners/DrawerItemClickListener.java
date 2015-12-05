/**
 * Copyright (c) 2015 Acrylic Goat Software
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 3 (LGPL).  See LICENSE.txt for details.
 */
package com.acrylicgoat.devchat.listeners;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.acrylicgoat.devchat.DataTableActivity;
import com.acrylicgoat.devchat.DevActivity;

/**
 * Created by ed on 11/23/15.
 */
public class DrawerItemClickListener implements ListView.OnItemClickListener
{
    DrawerLayout drawerLayout;
    Context context;

    public DrawerItemClickListener(Context c, DrawerLayout layout)
    {
        context = c;
        drawerLayout = layout;

    }
    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id)
    {
        selectItem(position);
    }

    private void selectItem(int position)
    {
        switch (position)
        {
            case 0:
                drawerLayout.closeDrawers();
                break;
            case 1:
                Intent devIntent = new Intent(context, DevActivity.class);
                context.startActivity(devIntent);
                drawerLayout.closeDrawers();
                break;

            case 2:
                Intent reportIntent = new Intent(context, DataTableActivity.class);
                context.startActivity(reportIntent);
                drawerLayout.closeDrawers();
                break;
        }
    }
}
