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
public class ButtonBackLogin extends Button{

    public ButtonBackLogin( final Application a )
    {
        setStyleName( "btnBack" );
        setWidth( "136px" );
        setHeight( "60px" );
        addListener( new Button.ClickListener() {

            public void buttonClick( ClickEvent event ) {
                Splash splash = new Splash( a );
                a.getMainWindow().setContent(splash);
            }
        });
    }
}
