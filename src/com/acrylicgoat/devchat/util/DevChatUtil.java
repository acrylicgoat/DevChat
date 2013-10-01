/**
 * Copyright (c) 2013 Acrylic Goat Software
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 3 (LGPL).  See LICENSE.txt for details.
 */
package com.acrylicgoat.devchat.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        String today = dateFormat.format(date);
        
        return today;
    }
    
    public static String getFileDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);
        Date date = new Date();
        String today = dateFormat.format(date);
        
        return today;
    }

}
