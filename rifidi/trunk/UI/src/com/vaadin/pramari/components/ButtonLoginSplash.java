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
public class ButtonLoginSplash extends Button{

    public ButtonLoginSplash( final Application application, final Splash splash )
    {
        setStyleName( "btnLoginSplash" );
        setWidth( "599px" );
        setHeight( "333px" );
        addListener( new Button.ClickListener() {
            public void buttonClick( ClickEvent event ) {
            	LoginForm loginForm = new LoginForm( application );
            	((PramariApplication)application).getNav().queue( splash );
                application.getMainWindow().setContent( loginForm );
            }
        });
    }
}
