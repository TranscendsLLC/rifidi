/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaadin.pramari.components;

import com.vaadin.ui.Button;

/**
 *
 * @author agomez
 */
public class ManualBarcodeButtonAssociation extends Button{

    public ManualBarcodeButtonAssociation()
    {
        setStyleName( "manualBarcodeButton" );
        setWidth( "923px" );
        setHeight( "75px" );

        //Code to handle click's on the help button
        addListener(new Button.ClickListener() 
        {
            public void buttonClick( ClickEvent event ) {
            	//Code to handle the click
            }
        });
    }
}
