package com.vaadin.pramari.components;

import java.text.SimpleDateFormat;
import java.util.Date;

//import com.google.gwt.dev.util.collect.HashMap;
import com.vaadin.Application;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class GenericMenu extends VerticalLayout{
	
	//private HashMap<String, String> options;
	ButtonOption btn1, btn2, btn3, btn4;
	
	public GenericMenu( Application application, String type )
	{
        setMargin( true );

        // Create the custom layout and set it as a component in
        // the current layout
        CustomLayout custom = new CustomLayout( "menu" );

        addComponent( custom );
        
		if( type.equals("1") )
		{
			btn1 = new ButtonOption(application, ButtonOption.STYLE_ASSOCIATION, ButtonOption.STYLE_POS1, "association", this);
			custom.addComponent(btn1, "btnOption1");
			btn2 = new ButtonOption(application, ButtonOption.STYLE_GROUPING, ButtonOption.STYLE_POS2, "menu", this);
			custom.addComponent(btn2, "btnOption2");
			btn3 = new ButtonOption(application, ButtonOption.STYLE_RECEIVING, ButtonOption.STYLE_POS3, "menu", this);
			custom.addComponent(btn3, "btnOption3");
			btn4 = new ButtonOption(application, ButtonOption.STYLE_SETUP, ButtonOption.STYLE_POS4, "menu", this);
			custom.addComponent(btn4, "btnOption4");
		}
		
		if( type.equals("2") )
		{
			btn1 = new ButtonOption( application, ButtonOption.STYLE_GROUPING, ButtonOption.STYLE_POS1, "menu", this);
			custom.addComponent(btn1, "btnOption1");
			btn2 = new ButtonOption( application, ButtonOption.STYLE_RECEIVING, ButtonOption.STYLE_POS2, "menu", this);
			custom.addComponent(btn2, "btnOption2");
			btn3 = new ButtonOption( application, ButtonOption.STYLE_SETUP, ButtonOption.STYLE_POS3, "menu", this);
			custom.addComponent(btn3, "btnOption3");
		}
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
