/**
 * Copyright (c) 2013 Acrylic Goat Software
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 3 (LGPL).  See LICENSE.txt for details.
 */
package com.acrylicgoat.devchat.adapter;

import com.acrylicgoat.devchat.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author ed woodward
 * 
 * Holder to improve performance of list.  
 *
 */
public class DevHolder
{
    ImageView iconView;
    TextView nameView;
    
    public DevHolder (View base)
    {
        iconView = (ImageView)base.findViewById(R.id.logoView);
        nameView = (TextView)base.findViewById(R.id.devName);
    }

}
