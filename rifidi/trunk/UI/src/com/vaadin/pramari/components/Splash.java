/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaadin.pramari.components;

import com.vaadin.Application;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class Splash extends VerticalLayout {

    public Splash( final Application application ) {
        setMargin( true );

        // Create the custom layout and set it as a component in
        // the current layout
        CustomLayout custom = new CustomLayout( "index" );

        addComponent( custom );

        // Create components and bind them to the location tags
        // in the custom layout.
        ButtonLoginSplash btnLogin= new ButtonLoginSplash( application, this );
        custom.addComponent(btnLogin, "login");

        ButtonBackSplash btnSplash = new ButtonBackSplash( application );
        custom.addComponent( btnSplash, "backButtonSplash" );

        //Format the Date as required
        SimpleDateFormat sdf = new SimpleDateFormat( "MMMM dd',' yyyy h:mm aa" );
        Label l = new Label( sdf.format( new Date() ) );
        
        custom.addComponent(l, "labelDate");
    }
}
