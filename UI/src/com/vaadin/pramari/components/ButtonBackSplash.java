/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaadin.pramari.components;

import com.vaadin.Application;
import com.vaadin.ui.Button;

/**
 *
 * @author agomez
 */
public class ButtonBackSplash extends Button{

    public ButtonBackSplash( final Application a )
    {
        setStyleName( "btnBackSplash" );
        setWidth( "136px" );
        setHeight( "60px" );
        //Code to catch the event of clicking the back button in the splash screen
//        addListener( new Button.ClickListener() {
//
//            public void buttonClick( ClickEvent event ) {
//            }
//        });
    }
}
