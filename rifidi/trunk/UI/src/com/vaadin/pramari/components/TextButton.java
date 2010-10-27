/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaadin.pramari.components;

import com.vaadin.Application;
import com.vaadin.pramari.PramariApplication;
import com.vaadin.ui.Button;


/**
 *
 * @author agomez
 */
public class TextButton extends Button{

    public TextButton( final Application a )
    {
        addStyleName( "textButton" );
        setWidth( "322px" );
        setHeight( "245px" );
        addListener( new Button.ClickListener() {
            public void buttonClick( ClickEvent event ) {
            	//code to handle click
            }
        });
    }
}
