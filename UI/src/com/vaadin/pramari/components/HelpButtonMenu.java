/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaadin.pramari.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/**
 *
 * @author agomez
 */
public class HelpButtonMenu extends Button{

    public HelpButtonMenu()
    {
        setStyleName( "btnHelpMenu" );
        setWidth( "923px" );
        setHeight( "75px" );

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
