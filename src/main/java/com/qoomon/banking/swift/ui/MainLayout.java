package com.qoomon.banking.swift.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.PWA;

/**
 * Main layout for the SWIFT Banking Messages application.
 */
@PWA(name = "SWIFT Banking Messages", shortName = "SWIFT Messages")
@CssImport("./styles/main.css")
public class MainLayout extends VerticalLayout implements RouterLayout {

    private Div contentContainer;

    public MainLayout() {
        addClassName("main-layout");
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        
        // Add header
        Header header = new Header();
        add(header);
        
        // Add navigation
        NavigationMenu navigation = new NavigationMenu();
        add(navigation);
        
        // Create container for routed content
        contentContainer = new Div();
        contentContainer.addClassName("content-container");
        contentContainer.setSizeFull();
        add(contentContainer);
        
        expand(contentContainer);
    }
    
    public void showRouterLayoutContent(Component content) {
        contentContainer.removeAll();
        contentContainer.add(content);
    }
}