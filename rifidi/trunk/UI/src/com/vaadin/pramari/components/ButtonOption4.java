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
public class ButtonOption4 extends Button{

    public ButtonOption4( final Application a )
    {
        addStyleName( "btnSetup btn4Pos" );
        setWidth( "451px" );
        setHeight( "207px" );
        addListener( new Button.ClickListener() {
            //Handle clicks on option 4
            public void buttonClick( ClickEvent event ) {
                System.out.println( "==== Click on setup ====" );
            }
        });
    }
}
