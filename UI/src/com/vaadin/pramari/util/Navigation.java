package com.vaadin.pramari.util;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

import com.vaadin.Application;
import com.vaadin.pramari.components.GenericMenu;
import com.vaadin.pramari.components.LoginForm;
import com.vaadin.pramari.components.Page;
import com.vaadin.pramari.components.Splash;

public class Navigation implements Serializable{
	
	private Deque<Object> nav;
	
	private Application application;
	
	public Navigation( Application application )
	{
		this.application = application;
		nav = new ArrayDeque<Object>();
	}
	
    /**
     * Method to queue a "page" into the stack
     **/	
    public void queue( Object o )
    {
    	nav.addFirst( o );
    }
    
    public Object retrieve()
    {
    	return nav.pop();
    }
    
    public boolean hasPages()
    {
    	return !nav.isEmpty();
    }
    
    /**
     * Method to go back to the previous "page" taking it out of the stack
     **/
    public void back(){
    	if( !nav.isEmpty() )
    	{
	    	Object o = nav.pop();
	    	
	    	if( o instanceof Splash )
	    	{
	    		Splash splash = new Splash( application );
	    		application.getMainWindow().setContent( splash );
	    	}
	    	else if( o instanceof LoginForm )
	    	{
	    		LoginForm loginForm = ( LoginForm )o;
	    		application.getMainWindow().setContent( loginForm );
	    	}
			else if( o instanceof GenericMenu )
			{
				GenericMenu menu = ( GenericMenu )o;
				application.getMainWindow().setContent( menu );
			}
			else if( o instanceof Page )
			{
				Page page = (Page)o;
				application.getMainWindow().setContent( page );    		
			}
    	}
    }
    /**
     * Method to return the stack of visited "pages"
     **/    
    public Deque<Object> getNav()
    {
    	return nav;
    }
    
}
