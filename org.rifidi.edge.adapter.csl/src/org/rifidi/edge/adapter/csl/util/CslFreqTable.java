/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.adapter.csl.util;

/* 
 * @author Matthew Dean - matt@transcends.co
 *  
 **/


public class CslFreqTable {

//----------------------------------------------------------------------------- Frequency Table
    public int[] etsiFreqTable_NumCh = new int[] { 4 };
	public int[] etsiFreqTable = new int[]
    {
            0x003C21D1, /*865.700MHz   */
            0x003C21D7, /*866.300MHz   */
            0x003C21DD, /*866.900MHz   */
            0x003C21E3, /*867.500MHz   */
    };
    
    public int[] indiaFreqTable_NumCh = new int[] { 3 };
    public int[] indiaFreqTable = new int[]
    {
            0x003C21D1, /*865.700MHz   */
            0x003C21D7, /*866.300MHz   */
            0x003C21DD, /*866.900MHz   */
    };

    public int[] G800FreqTable_NumCh = new int[] { 4 };
    public int[] G800FreqTable = new int[]
    {
            0x003C21D1, /*865.700MHz   */
            0x003C21D7, /*866.300MHz   */
            0x003C21DD, /*866.900MHz   */
            0x003C21E3, /*867.500MHz   */
    };
    
    public int[] AusFreqTable_NumCh = new int[] { 10 };
    public int[] AusFreqTable = new int[]
    {
            0x00180E63, /* 920.75MHz   */
            0x00180E69, /* 922.25MHz   */
            0x00180E6F, /* 923.75MHz   */
            0x00180E73, /* 924.75MHz   */
            0x00180E65, /* 921.25MHz   */
            0x00180E6B, /* 922.75MHz   */
            0x00180E71, /* 924.25MHz   */
            0x00180E75, /* 925.25MHz   */
            0x00180E67, /* 921.75MHz   */
            0x00180E6D, /* 923.25MHz   */
    };

    public int[] br1FreqTable_NumCh = new int[] { 24 };
    public int[] br1FreqTable = new int[]
    {
            0x00180E4F, /*915.75 MHz   */
            0x00180E7B, /*926.75 MHz   */
            0x00180E79, /*926.25 MHz   */
            0x00180E7D, /*927.25 MHz   */
            0x00180E61, /*920.25 MHz   */
            0x00180E5D, /*919.25 MHz   */
            0x00180E5B, /*918.75 MHz   */
            0x00180E57, /*917.75 MHz   */
            0x00180E75, /*925.25 MHz   */
            0x00180E67, /*921.75 MHz   */
            0x00180E69, /*922.25 MHz   */
            0x00180E55, /*917.25 MHz   */
            0x00180E59, /*918.25 MHz   */
            0x00180E51, /*916.25 MHz   */
            0x00180E73, /*924.75 MHz   */
            0x00180E5F, /*919.75 MHz   */
            0x00180E53, /*916.75 MHz   */
            0x00180E6F, /*923.75 MHz   */
            0x00180E77, /*925.75 MHz   */
            0x00180E71, /*924.25 MHz   */
            0x00180E65, /*921.25 MHz   */
            0x00180E63, /*920.75 MHz   */
            0x00180E6B, /*922.75 MHz   */
            0x00180E6D, /*923.25 MHz   */
    };     

    public int[] br2FreqTable_NumCh = new int[] { 33 };     
    public int[] br2FreqTable = new int[]
    {
            0x00180E4F, /*915.75 MHz   */
            0x00180E1D, /*903.25 MHz   */
            0x00180E7B, /*926.75 MHz   */
            0x00180E79, /*926.25 MHz   */
            0x00180E21, /*904.25 MHz   */
            0x00180E7D, /*927.25 MHz   */
            0x00180E61, /*920.25 MHz   */
            0x00180E5D, /*919.25 MHz   */
            0x00180E5B, /*918.75 MHz   */
            0x00180E57, /*917.75 MHz   */
            0x00180E25, /*905.25 MHz   */
            0x00180E23, /*904.75 MHz   */
            0x00180E75, /*925.25 MHz   */
            0x00180E67, /*921.75 MHz   */
            0x00180E2B, /*906.75 MHz   */
            0x00180E69, /*922.25 MHz   */
            0x00180E1F, /*903.75 MHz   */
            0x00180E27, /*905.75 MHz   */
            0x00180E29, /*906.25 MHz   */
            0x00180E55, /*917.25 MHz   */
            0x00180E59, /*918.25 MHz   */
            0x00180E51, /*916.25 MHz   */
            0x00180E73, /*924.75 MHz   */
            0x00180E5F, /*919.75 MHz   */
            0x00180E53, /*916.75 MHz   */
            0x00180E6F, /*923.75 MHz   */
            0x00180E77, /*925.75 MHz   */
            0x00180E71, /*924.25 MHz   */
            0x00180E65, /*921.25 MHz   */
            0x00180E63, /*920.75 MHz   */
            0x00180E6B, /*922.75 MHz   */
            0x00180E1B, /*902.75 MHz   */
            0x00180E6D, /*923.25 MHz   */
    };     
    
    public int[] hkFreqTable_NumCh = new int[] { 8 };    
    public int[] hkFreqTable = new int[]
    {
            0x00180E63, /*920.75MHz   */
            0x00180E69, /*922.25MHz   */
            0x00180E6F, /*923.75MHz   */
            0x00180E65, /*921.25MHz   */
            0x00180E6B, /*922.75MHz   */
            0x00180E71, /*924.25MHz   */
            0x00180E67, /*921.75MHz   */
            0x00180E6D, /*923.25MHz   */
    };     

    public int[] thFreqTable_NumCh = new int[] { 8 };    
    public int[] thFreqTable = new int[]
    {
            0x00180E63, /*920.75MHz   */
            0x00180E69, /*922.25MHz   */
            0x00180E6F, /*923.75MHz   */
            0x00180E65, /*921.25MHz   */
            0x00180E6B, /*922.75MHz   */
            0x00180E71, /*924.25MHz   */
            0x00180E67, /*921.75MHz   */
            0x00180E6D, /*923.25MHz   */
    };     
    
    public int[] sgFreqTable_NumCh = new int[] { 8 };    
    public int[] sgFreqTable = new int[]
    {
            0x00180E63, /*920.75MHz   */
            0x00180E69, /*922.25MHz   */
            0x00180E6F, /*923.75MHz   */
            0x00180E65, /*921.25MHz   */
            0x00180E6B, /*922.75MHz   */
            0x00180E71, /*924.25MHz   */
            0x00180E67, /*921.75MHz   */
            0x00180E6D, /*923.25MHz   */
    };     

    public int[] mysFreqTable_NumCh = new int[] { 8 };
    public int[] mysFreqTable = new int[]
    {
            0x00180E5F, /*919.75MHz   */
            0x00180E65, /*921.25MHz   */
            0x00180E6B, /*922.75MHz   */
            0x00180E61, /*920.25MHz   */
            0x00180E67, /*921.75MHz   */
            0x00180E6D, /*923.25MHz   */
            0x00180E63, /*920.75MHz   */
            0x00180E69, /*922.25MHz   */
    };     

    public int[] zaFreqTable_NumCh = new int[] { 16 };
    public int[] zaFreqTable = new int[]
    {
            0x003C23C5, /*915.7 MHz   */ 
            0x003C23C7, /*915.9 MHz   */
            0x003C23C9, /*916.1 MHz   */
            0x003C23CB, /*916.3 MHz   */
            0x003C23CD, /*916.5 MHz   */
            0x003C23CF, /*916.7 MHz   */
            0x003C23D1, /*916.9 MHz   */
            0x003C23D3, /*917.1 MHz   */
            0x003C23D5, /*917.3 MHz   */
            0x003C23D7, /*917.5 MHz   */
            0x003C23D9, /*917.7 MHz   */
            0x003C23DB, /*917.9 MHz   */
            0x003C23DD, /*918.1 MHz   */
            0x003C23DF, /*918.3 MHz   */
            0x003C23E1, /*918.5 MHz   */
            0x003C23E3, /*918.7 MHz   */
    };     
    
    public int[] indonesiaFreqTable_NumCh = new int[] { 4 };
    public int[] indonesiaFreqTable = new int[]
    {
            0x00180E6D, /*923.25 MHz    */
            0x00180E6F,/*923.75 MHz    */
            0x00180E71,/*924.25 MHz    */
            0x00180E73,/*924.75 MHz    */
    };     

    public int[] cnFreqTable_NumCh = new int[] { 16 };
    public int[] cnFreqTable = new int[]
    {
            0x00301CD3, /*922.375MHz   */
            0x00301CD1, /*922.125MHz   */
            0x00301CCD, /*921.625MHz   */
            0x00301CC5, /*920.625MHz   */
            0x00301CD9, /*923.125MHz   */
            0x00301CE1, /*924.125MHz   */
            0x00301CCB, /*921.375MHz   */
            0x00301CC7, /*920.875MHz   */
            0x00301CD7, /*922.875MHz   */
            0x00301CD5, /*922.625MHz   */
            0x00301CC9, /*921.125MHz   */
            0x00301CDF, /*923.875MHz   */
            0x00301CDD, /*923.625MHz   */
            0x00301CDB, /*923.375MHz   */
            0x00301CCF, /*921.875MHz   */
            0x00301CE3, /*924.375MHz   */
    };     

    public int[] cn1FreqTable_NumCh = new int[] { 4 };    
    public int[] cn1FreqTable = new int[]
    {
            0x00301CC5, /*920.625MHz   */
            0x00301CC7, /*920.875MHz   */
            0x00301CC9, /*921.125MHz   */
            0x00301CCB, /*921.375MHz   */
    };     

    public int[] cn2FreqTable_NumCh = new int[] { 4 };
    public int[] cn2FreqTable = new int[]
    {
            0x00301CCD, /*921.625MHz   */
            0x00301CCF, /*921.875MHz   */
            0x00301CD1, /*922.125MHz   */
			0x00301CD3, /*922.375MHz   */
    };     
 
    public int[] cn3FreqTable_NumCh = new int[] { 4 };        
    public int[] cn3FreqTable = new int[]
    {
            0x00301CD5, /*922.625MHz   */
            0x00301CD7, /*922.875MHz   */
            0x00301CD9, /*923.125MHz   */
            0x00301CDB, /*923.375MHz   */
    };     

    public int[] cn4FreqTable_NumCh = new int[] { 4 };      
    public int[] cn4FreqTable = new int[]
    {
            0x00301CDD, /*923.625MHz   */
            0x00301CDF, /*923.875MHz   */
            0x00301CE1, /*924.125MHz   */
            0x00301CE3, /*924.375MHz   */
    };     
 
    public int[] cn5FreqTable_NumCh = new int[] { 4 };        
    public int[] cn5FreqTable = new int[]
    {
            0x00301CC5, /*920.625MHz   */
            0x00301CCD, /*921.625MHz   */
            0x00301CD5, /*922.625MHz   */
            0x00301CDD, /*923.625MHz   */
    };     
    
    public int[] cn6FreqTable_NumCh = new int[] { 4 };        
    public int[] cn6FreqTable = new int[]
    {
            0x00301CC7, /*920.875MHz   */
            0x00301CCF, /*921.875MHz   */
            0x00301CD7, /*922.875MHz   */
            0x00301CDF, /*923.875MHz   */
    };     
 
    public int[] cn7FreqTable_NumCh = new int[] { 4 };      
    public int[] cn7FreqTable = new int[]
    {
            0x00301CC9, /*921.125MHz   */
            0x00301CD1, /*922.125MHz   */
            0x00301CD9, /*923.125MHz   */
            0x00301CE1, /*924.125MHz   */
    };
    
    public int[] cn8FreqTable_NumCh = new int[] { 4 };     
    public int[] cn8FreqTable = new int[]
    {
            0x00301CCB, /*921.375MHz   */
            0x00301CD3, /*922.375MHz   */
            0x00301CDB, /*923.375MHz   */
            0x00301CE3, /*924.375MHz   */
    };     
 
    public int[] cn9FreqTable_NumCh = new int[] { 3 };       
    public int[] cn9FreqTable = new int[]
    {
            0x00301CC5, /*920.625MHz   */
            0x00301CC7, /*920.875MHz   */
            0x00301CC9, /*921.125MHz   */
    };     

    public int[] cn10FreqTable_NumCh = new int[] { 3 };      
    public int[] cn10FreqTable = new int[]
    {
            0x00301CCD, /*921.625MHz   */
            0x00301CCF, /*921.875MHz   */
            0x00301CD1, /*922.125MHz   */
    };     
 
    public int[] cn11FreqTable_NumCh = new int[] { 3 };      
    public int[] cn11FreqTable = new int[]
    {
            0x00301CD5, /*922.625MHz   */
            0x00301CD7, /*922.875MHz   */
            0x00301CD9, /*923.125MHz   */
    };
    
    public int[] cn12FreqTable_NumCh = new int[] { 3 };        
    public int[] cn12FreqTable = new int[]
    {
            0x00301CDD, /*923.625MHz   */
            0x00301CDF, /*923.875MHz   */
            0x00301CE1, /*924.125MHz  */
    };
    
    public int[] twFreqTable_NumCh = new int[] { 12 };       
    public int[] twFreqTable = new int[]
    {
            0x00180E7D, /*927.25MHz   10*/
            0x00180E73, /*924.75MHz   5*/
            0x00180E6B, /*922.75MHz   1*/
            0x00180E75, /*925.25MHz   6*/
            0x00180E7F, /*927.75MHz   11*/
            0x00180E71, /*924.25MHz   4*/
            0x00180E79, /*926.25MHz   8*/
            0x00180E6D, /*923.25MHz   2*/
            0x00180E7B, /*926.75MHz   9*/
            0x00180E69, /*922.25MHz   0*/
            0x00180E77, /*925.75MHz   7*/
            0x00180E6F, /*923.75MHz   3*/
    };

    public int[] jpn2012FreqTable_NumCh = new int[] { 4 };    
    public int[] jpn2012FreqTable = new int[]
    {
            0x003C23D0, /*916.800MHz   Channel 1*/
            0x003C23DC, /*918.000MHz   Channel 2*/
            0x003C23E8, /*919.200MHz   Channel 3*/
            0x003C23F4, /*920.400MHz   Channel 4*/
    };
    
    public int[] fccFreqTable_NumCh = new int[] { 50 };
    public int[] fccFreqTable = new int[]
    {
            0x00180E4F, /*915.75 MHz   */
            0x00180E4D, /*915.25 MHz   */
            0x00180E1D, /*903.25 MHz   */
            0x00180E7B, /*926.75 MHz   */
            0x00180E79, /*926.25 MHz   */
            0x00180E21, /*904.25 MHz   */
            0x00180E7D, /*927.25 MHz   */
            0x00180E61, /*920.25 MHz   */
            0x00180E5D, /*919.25 MHz   */
            0x00180E35, /*909.25 MHz   */
            0x00180E5B, /*918.75 MHz   */
            0x00180E57, /*917.75 MHz   */
            0x00180E25, /*905.25 MHz   */
            0x00180E23, /*904.75 MHz   */
            0x00180E75, /*925.25 MHz   */
            0x00180E67, /*921.75 MHz   */
            0x00180E4B, /*914.75 MHz   */
            0x00180E2B, /*906.75 MHz   */
            0x00180E47, /*913.75 MHz   */
            0x00180E69, /*922.25 MHz   */
            0x00180E3D, /*911.25 MHz   */
            0x00180E3F, /*911.75 MHz   */
            0x00180E1F, /*903.75 MHz   */
            0x00180E33, /*908.75 MHz   */
            0x00180E27, /*905.75 MHz   */
            0x00180E41, /*912.25 MHz   */
            0x00180E29, /*906.25 MHz   */
            0x00180E55, /*917.25 MHz   */
            0x00180E49, /*914.25 MHz   */
            0x00180E2D, /*907.25 MHz   */
            0x00180E59, /*918.25 MHz   */
            0x00180E51, /*916.25 MHz   */
            0x00180E39, /*910.25 MHz   */
            0x00180E3B, /*910.75 MHz   */
            0x00180E2F, /*907.75 MHz   */
            0x00180E73, /*924.75 MHz   */
            0x00180E37, /*909.75 MHz   */
            0x00180E5F, /*919.75 MHz   */
            0x00180E53, /*916.75 MHz   */
            0x00180E45, /*913.25 MHz   */
            0x00180E6F, /*923.75 MHz   */
            0x00180E31, /*908.25 MHz   */
            0x00180E77, /*925.75 MHz   */
            0x00180E43, /*912.75 MHz   */
            0x00180E71, /*924.25 MHz   */
            0x00180E65, /*921.25 MHz   */
            0x00180E63, /*920.75 MHz   */
            0x00180E6B, /*922.75 MHz   */
            0x00180E1B, /*902.75 MHz   */
            0x00180E6D, /*923.25 MHz   */
    };
    
    public int NUM_COUNTRIES = 28;
    
    public int[] countryFreqTable [][] =
    	{
    		// Freq Table,  Number of Channels
    		{fccFreqTable, fccFreqTable_NumCh},
    		{etsiFreqTable, etsiFreqTable_NumCh},
    		{indiaFreqTable, indiaFreqTable_NumCh},
    		{G800FreqTable, G800FreqTable_NumCh},
    		{AusFreqTable, AusFreqTable_NumCh},
    		{br1FreqTable, br1FreqTable_NumCh},
    		{br2FreqTable, br2FreqTable_NumCh},
    		{hkFreqTable, hkFreqTable_NumCh},
    		{thFreqTable, thFreqTable_NumCh},
    		{sgFreqTable, sgFreqTable_NumCh},
    		{mysFreqTable, mysFreqTable_NumCh},
    		{zaFreqTable, zaFreqTable_NumCh},
    		{indonesiaFreqTable, indonesiaFreqTable_NumCh},
    		{cnFreqTable, cnFreqTable_NumCh},
    		{cn1FreqTable, cn1FreqTable_NumCh},
    		{cn2FreqTable, cn2FreqTable_NumCh},
    		{cn3FreqTable, cn3FreqTable_NumCh},
    		{cn4FreqTable, cn4FreqTable_NumCh},
    		{cn5FreqTable, cn5FreqTable_NumCh},
    		{cn6FreqTable, cn6FreqTable_NumCh},
    		{cn7FreqTable, cn7FreqTable_NumCh},
    		{cn8FreqTable, cn8FreqTable_NumCh},
    		{cn9FreqTable, cn9FreqTable_NumCh},
    		{cn10FreqTable, cn10FreqTable_NumCh},
    		{cn11FreqTable, cn11FreqTable_NumCh},
    		{cn12FreqTable, cn12FreqTable_NumCh},
    		{twFreqTable, twFreqTable_NumCh},
    		{jpn2012FreqTable, jpn2012FreqTable_NumCh}
    	};


//    public List<String> OEMCountryTable = Arrays.asList
    public String[] OEMCountryTable = 
    {
    	"FCC",
   		"ETSI",
   		"IN",
   		"G800",
   		"AU",
   		"BR1",
   		"BR2",
   		"HK",
   		"TH",
   		"SG",
   		"MY",
   		"ZA",
   		"ID",
   		"CN",
   		"CN1",
   		"CN2",
   		"CN3",
   		"CN4",
   		"CN5",
   		"CN6",
   		"CN7",
   		"CN8",
   		"CN9",
   		"CN10",
   		"CN11",
   		"CN12",
   		"TW",
   		"JP"
    };

}