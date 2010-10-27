/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaadin.pramari.components;

import com.vaadin.Application;
import com.vaadin.pramari.PramariApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class LoginForm extends VerticalLayout {

    public LoginForm( final Application application )  
    {
        setMargin( true );
        
        final LoginForm esteObjeto = this;

        // Create the custom layout and set it as a component in
        // the current layout
        CustomLayout custom = new CustomLayout( "login" );
        
        addComponent( custom );

        // Create components and bind them to the location tags
        // in the custom layout.
        final TextField username = new TextField();
        username.setStyleName("userID");
        custom.addComponent( username, "userID" );

        final TextField password = new TextField();
        password.setStyleName("password");
        password.setSecret( true );
        custom.addComponent( password, "password" );
        
        ButtonBack back = new ButtonBack(application);
        custom.addComponent(back , "backButtonLogin");

        SubmitButtonLogin btnSubmit = new SubmitButtonLogin();
        //listener to answer to clicks on the submit button
        btnSubmit.addListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
            	String user = (String)username.getValue();
            	String pass = (String)password.getValue();
                System.out.println("=======USER: " + user + " PASS: " + pass + " ==========");
                if( user.equals("ambient") && pass.equals("ambient") )
                {
                	
		            //Menu menu = new Menu( application );
                	GenericMenu menu = new GenericMenu( application, "1" );
                	( ( PramariApplication )application ).getNav().queue( esteObjeto );
                	application.getMainWindow().setContent( menu );
                }
                else
                {
                	Notification n = new Notification("", "Incorrect Username/Password", 
                			Notification.TYPE_ERROR_MESSAGE);
                	application.getMainWindow().showNotification( n );
                }
            }
        });

        custom.addComponent(btnSubmit, "submitButtonLogin");

        HelpButtonLogin btnHelp = new HelpButtonLogin();
        custom.addComponent(btnHelp, "helpButtonLogin");

        //Format the Date as required
        SimpleDateFormat sdf = new SimpleDateFormat( "MMMM dd',' yyyy        h:mm aa" );
        Label l = new Label( sdf.format( new Date() ) );

        custom.addComponent(l, "labelDate");
    }
}
