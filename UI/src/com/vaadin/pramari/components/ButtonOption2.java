/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaadin.pramari.components;

import com.vaadin.Application;
import com.vaadin.ui.Button;

/**
 *
 * @author agomez
 */
public class ButtonOption2 extends Button{

    public ButtonOption2( final Application a )
    {
        addStyleName( "btnGrouping btn2Pos" );
        setWidth( "451px" );
        setHeight( "207px" );
        addListener( new Button.ClickListener() {
            //Handle clicks on option 2
            public void buttonClick( ClickEvent event ) {
                System.out.println( "==== Click on option grouping ====" );
            }
        });
    }
}
