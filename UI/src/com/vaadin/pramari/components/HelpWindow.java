package com.vaadin.pramari.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class HelpWindow extends Window{
	
	public HelpWindow( final Window window )
	{
    	super("HELP");
    	final HelpWindow thisHelpWindow = this;
    	setWidth("957px");
    	setHeight("586px");
    	setPositionX(32);
    	setPositionY(150);
    	setClosable( false );
        // Configure the windws layout; by default a VerticalLayout
        VerticalLayout layout = (VerticalLayout)getContent();
        layout.setMargin( true );
        layout.setSpacing( true );
        // Add panel
        final Panel panel = new Panel( "Help Content" );
        panel.setHeight( "450px" );
        panel.setScrollable( true );
        //add content (help) to the panel
        for (int i = 0; i < 100; i++) {
        	Label label = new Label( "<b>Label</b> " + i );
        	label.setContentMode(Label.CONTENT_XHTML);
            panel.addComponent( label );
        }

        Button scrollUp = new Button();
        scrollUp.setWidth( "64px" );
        scrollUp.setHeight( "64px" );
        scrollUp.setStyleName("btnHelpUp");
        scrollUp.addListener(new Button.ClickListener() 
        {
			
			@Override
			public void buttonClick(ClickEvent event) 
			{
				int pixel = panel.getScrollTop() - 100;
				if( pixel < 0 )
					pixel = 0;
				panel.setScrollTop( pixel );
				
			}
		});
        Button scrollDown = new Button();
        scrollDown.setWidth( "64px" );
        scrollDown.setHeight( "64px" );
        scrollDown.setStyleName("btnHelpDown");
        scrollDown.addListener(new Button.ClickListener() 
        {
			
			@Override
			public void buttonClick( ClickEvent event ) {
				panel.setScrollTop( panel.getScrollTop() + 100 );			
			}
		});
        Button close = new Button();
        close.setWidth( "64px" );
        close.setHeight( "64px" );
        close.setStyleName("btnHelpClose");
        close.addListener(new Button.ClickListener() 
        {
			
			@Override
			public void buttonClick( ClickEvent event ) {
				window.removeWindow( thisHelpWindow );
			}
		});
        HorizontalLayout horizontal = new HorizontalLayout();
        horizontal.setSpacing( true );
        horizontal.addComponent( scrollDown );
        horizontal.addComponent( scrollUp );
        horizontal.addComponent( close );
        addComponent( panel );
        addComponent( horizontal );	
	}

}
