/**
 * Copyright (c) 2013 Acrylic Goat Software
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 3 (LGPL).  See LICENSE.txt for details.
 */
package com.acrylicgoat.devchat.util;

import android.content.Context;

import com.acrylicgoat.devchat.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * @author ed woodward
 * 
 * Utility class
 *
 */
public class DevChatUtil
{
    public static String getTodaysDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }
    
    public static String getFileDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static List<HashMap<String,String>> setNavDrawer(Context context)
    {
        String[] items = context.getResources().getStringArray(R.array.nav_list);
        HashMap hm1 = new HashMap<>();
        hm1.put("nav_icon",Integer.toString(R.drawable.home));
        hm1.put("nav_item",items[0]);

        HashMap hm2 = new HashMap<>();
        hm2.put("nav_icon",Integer.toString(R.drawable.dev));
        hm2.put("nav_item",items[1]);

        HashMap hm3 = new HashMap<>();
        hm3.put("nav_icon",Integer.toString(R.drawable.dev));
        hm3.put("nav_item",items[2]);

        ArrayList navTitles = new ArrayList();

        navTitles.add(hm1);
        navTitles.add(hm2);
        navTitles.add(hm3);
        return navTitles;

    }

}
