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
public class Menu extends VerticalLayout {

    public Menu( final Application application ) {
        setMargin( true );

        // Create the custom layout and set it as a component in
        // the current layout
        CustomLayout custom = new CustomLayout( "menu" );

        addComponent( custom );

        // Create components and bind them to the location tags
        // in the custom layout.
        ButtonOption1 btnOption1 = new ButtonOption1( application );
        custom.addComponent(btnOption1, "btnOption1");

        ButtonOption2 btnOption2 = new ButtonOption2( application );
        custom.addComponent(btnOption2, "btnOption2");

        ButtonOption3 btnOption3 = new ButtonOption3( application );
        custom.addComponent(btnOption3, "btnOption3");

        ButtonOption4 btnOption4 = new ButtonOption4( application );
        custom.addComponent(btnOption4, "btnOption4");

        HelpButtonMenu btnHelpMenu = new HelpButtonMenu();
        custom.addComponent( btnHelpMenu, "btnHelpMenu");
        
        ButtonBack back = new ButtonBack( application );
        custom.addComponent( back, "btnBackMenu" );

        //Format the Date as required
        SimpleDateFormat sdf = new SimpleDateFormat( "MMMM dd',' yyyy h:mm aa" );
        Label l = new Label( sdf.format( new Date() ) );
        
        custom.addComponent(l, "labelDate");
    }
}
