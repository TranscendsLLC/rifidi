/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaadin.pramari.components;

import com.vaadin.pramari.PramariApplication;
import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 *
 * @author agomez
 */
public class ButtonBack extends Button{

    public ButtonBack( final Application application )
    {
        setStyleName( "btnBack" );
        setWidth( "136px" );
        setHeight( "60px" );
        //Code to catch the event of clicking the back button
        addListener( new Button.ClickListener() 
        {
            public void buttonClick( ClickEvent event ) 
            {
            	//Use the navigation utility class to get the last queued page
            	((PramariApplication)application).getNav().back();
            }
        });
    }
}
