/**
 * Copyright (c) 2013 Acrylic Goat Software
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 3 (LGPL).  See LICENSE.txt for details.
 */
package com.acrylicgoat.devchat.beans;

import java.io.Serializable;


/**
 * @author ed woodward
 * 
 * Bean to hold developer info from database
 *
 */
public class Developer implements Serializable, Comparable<Developer>
{
    /** id for serialization */
    public static final long serialVersionUID = 1L;
    
    private String name;
    
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     * Required method for Comparable interface
     */
    //@Override
    public int compareTo(Developer another)
    {
        return name.toUpperCase().trim().compareTo(another.name.toUpperCase().trim());
        
    }
    

}
