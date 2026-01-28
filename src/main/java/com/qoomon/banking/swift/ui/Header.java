package com.qoomon.banking.swift.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 * Application header component.
 */
public class Header extends FlexLayout {

    public Header() {
        addClassName("header");
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.BETWEEN);
        setAlignItems(Alignment.CENTER);
        
        // Title
        H1 title = new H1("SWIFT Banking Messages");
        title.addClassName("header-title");
        
        // Status indicator
        Div status = new Div();
        status.setText("Professional Banking Interface");
        status.addClassName("header-status");
        
        add(title, status);
    }
}