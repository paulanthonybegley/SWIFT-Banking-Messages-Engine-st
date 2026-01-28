package com.qoomon.banking.swift.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;

/**
 * Main view for the SWIFT Banking Messages application.
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("SWIFT Banking Messages - Professional Banking Interface")
public class HomeView extends VerticalLayout {

    public HomeView() {
        addClassName("home-view");
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        
        // Welcome section
        Div welcome = new Div();
        welcome.addClassName("welcome-section");
        welcome.setText("Welcome to the Professional SWIFT Banking Messages Interface");
        
        // Description
        Div description = new Div();
        description.addClassName("description-section");
        description.setText("Parse, compose, and validate SWIFT banking messages with ease. Supports MT940, MT942, and MT101 message formats.");
        
        // Quick actions
        Div quickActions = new Div();
        quickActions.addClassName("quick-actions");
        
        // Navigation buttons
        com.vaadin.flow.component.button.Button parseButton = new com.vaadin.flow.component.button.Button(
            "Parse Messages", 
            event -> getUI().ifPresent(ui -> ui.navigate(ParserView.class))
        );
        parseButton.addClassName("action-button");
        
        com.vaadin.flow.component.button.Button composeButton = new com.vaadin.flow.component.button.Button(
            "Compose Messages", 
            event -> getUI().ifPresent(ui -> ui.navigate(ComposerView.class))
        );
        composeButton.addClassName("action-button");
        
        HorizontalLayout buttons = new HorizontalLayout(parseButton, composeButton);
        buttons.addClassName("action-buttons");
        quickActions.add(buttons);
        
        add(welcome, description, quickActions);
    }
}