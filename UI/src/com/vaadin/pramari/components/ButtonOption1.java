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
public class ButtonOption1 extends Button{

    public ButtonOption1( final Application a )
    {
        addStyleName( "btnAssociation btn1Pos" );
        setWidth( "451px" );
        setHeight( "207px" );
        addListener( new Button.ClickListener() {
            //Handle clicks on option 1
            public void buttonClick( ClickEvent event ) {
                Page page = new Page( a );
                a.getMainWindow().setContent( page );
            }
        });
    }
}
