package com.vaadin.pramari.components;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.Application;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class Page extends VerticalLayout
{
	
    public Page( Application application )
    {
        setMargin( true );

        // Create the custom layout and set it as a component in
        // the current layout
        CustomLayout custom = new CustomLayout( "page" );

        addComponent( custom );

        // Create components and bind them to the location tags
        // in the custom layout.
//        EmbeddedPage embedded = new EmbeddedPage( "http://localhost:8080/LabTrackUI" );
//        custom.addComponent(embedded, "embedded");

        ButtonBack btnBack = new ButtonBack( application );
        custom.addComponent( btnBack, "btnBackPage" );

        //Format the Date as required
        SimpleDateFormat sdf = new SimpleDateFormat( "MMMM dd',' yyyy h:mm aa" );
        Label l = new Label( sdf.format( new Date() ) );

        custom.addComponent( l, "labelDate" );
    }

}
