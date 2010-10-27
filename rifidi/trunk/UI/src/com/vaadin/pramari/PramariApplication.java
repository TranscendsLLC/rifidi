/*
 * PramariApplication.java
 *
 * Created on 7 de octubre de 2010, 03:15 PM
 */
 
package com.vaadin.pramari;

import java.util.Deque;

import com.vaadin.pramari.components.Splash;
import com.vaadin.pramari.util.Navigation;
import com.vaadin.Application;
import com.vaadin.ui.*;

/** 
 *
 * @author agomez
 * @version 
 */

public class PramariApplication extends Application 
{
	private Navigation nav;
    @Override
    public void init() 
    {
        setTheme( "Pramari" );
		Window mainWindow = new Window( "Ambient" );
		Splash splash = new Splash( this );
		nav = new Navigation( this );
		mainWindow.addComponent( splash );
		setMainWindow( mainWindow );
    }
    
    public Navigation getNav()
    {
    	return nav;
    }
}
