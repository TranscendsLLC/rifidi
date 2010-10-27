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
public class Association extends VerticalLayout {

    public Association( final Application application )  
    {
        setMargin( true );

        // Create the custom layout and set it as a component in
        // the current layout
        CustomLayout custom = new CustomLayout( "association" );
        
        addComponent( custom );

        // Create components and bind them to the location tags
        // in the custom layout.
        final TextField barcode = new TextField();
        custom.addComponent( barcode, "barcode" );

        final TextField rfid = new TextField();
        rfid.setEnabled(false);
        custom.addComponent( rfid, "rfid" );
        
        ButtonBack back = new ButtonBack( application );
        custom.addComponent(back , "backButton");
        
        TextButton textButton = new TextButton( application );
        custom.addComponent( textButton , "textButton" );

        ManualBarcodeButtonAssociation btnManualBarcode = new ManualBarcodeButtonAssociation();
        custom.addComponent( btnManualBarcode, "manualBarcode" );
        
        HelpButtonLogin btnHelp = new HelpButtonLogin();
        custom.addComponent(btnHelp, "helpButtonAssociation");
        
        Label numbers = new Label( "33 / 48" );
        custom.addComponent( numbers, "numbers" );

        //Format the Date as required
        SimpleDateFormat sdf = new SimpleDateFormat( "MMMM dd',' yyyy        h:mm aa" );
        Label l = new Label( sdf.format( new Date() ) );

        custom.addComponent(l, "labelDate");
    }
}
