/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaadin.pramari.components;

import com.vaadin.pramari.PramariApplication;
import com.vaadin.Application;
import com.vaadin.ui.Button;

/**
 *
 * @author agomez
 */
public class ButtonBackMenu extends Button{

    public ButtonBackMenu( final Application application )
    {
        setStyleName( "btnBack" );
        setWidth( "136px" );
        setHeight( "60px" );
        //Code to catch the event of clicking the back button in the menu screen
        addListener( new Button.ClickListener() {
            public void buttonClick( ClickEvent event ) {
                //change main window layout to the login form screen
            	LoginForm loginForm = new LoginForm( application );
            	
                application.getMainWindow().setContent( loginForm );
            }
        });
    }
}
