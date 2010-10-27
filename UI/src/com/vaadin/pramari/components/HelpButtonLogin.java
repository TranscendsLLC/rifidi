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
public class HelpButtonLogin extends Button{

    public HelpButtonLogin()
    {
        setStyleName( "btnHelpLogin" );
        setWidth( "83px" );
        setHeight( "80px" );

        //Code to handle click's on the help button
        addListener(new Button.ClickListener() {

            public void buttonClick( ClickEvent event ) {
            	
                HelpWindow helpWindow = new HelpWindow( getWindow() );
                if ( helpWindow.getParent() != null ) {
                    // window is already showing
                    getWindow().showNotification( "Window is already open" );
                } else {
                    // Open the subwindow by adding it to the parent window
                    getWindow().addWindow( helpWindow );
                }
            }
        });
    }
}
