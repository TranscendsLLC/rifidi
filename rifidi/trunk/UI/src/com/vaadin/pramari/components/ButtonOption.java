package com.vaadin.pramari.components;

import com.vaadin.Application;
import com.vaadin.pramari.PramariApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class ButtonOption extends Button
{
	public static String STYLE_ASSOCIATION = "btnAssociation";
	public static String STYLE_GROUPING = "btnGrouping";
	public static String STYLE_RECEIVING = "btnReceiving";
	public static String STYLE_SETUP = "btnSetup";
	public static String STYLE_POS1 = "btn1Pos";
	public static String STYLE_POS2 = "btn2Pos";
	public static String STYLE_POS3 = "btn3Pos";
	public static String STYLE_POS4 = "btn4Pos";
	
	public ButtonOption( final Application application, String style, String posStyle, final String destiny, final GenericMenu menu )
	{
		//assign the styles
		addStyleName( style + " " + posStyle );
        setWidth( "451px" );
        setHeight( "207px" );
        addListener( new Button.ClickListener() {
            //Handle clicks on the button
            public void buttonClick( ClickEvent event ) {
                if( destiny.equals("association") )
                {
                	Association association = new Association( application );
                	((PramariApplication)application).getNav().queue( menu );
                	application.getMainWindow().setContent( association );
                }
                if( destiny.equals("menu") )
                {
                	GenericMenu genericMenu = new GenericMenu( application, "2" );
                	((PramariApplication)application).getNav().queue( menu );
                	application.getMainWindow().setContent( genericMenu );
                }
            }
        });
	}

}
