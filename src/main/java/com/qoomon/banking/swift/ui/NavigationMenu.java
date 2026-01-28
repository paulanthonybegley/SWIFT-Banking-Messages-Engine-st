package com.qoomon.banking.swift.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;

/**
 * Navigation menu component.
 */
@CssImport("./styles/navigation.css")
public class NavigationMenu extends HorizontalLayout {

    public NavigationMenu() {
        addClassName("navigation-menu");
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setSpacing(false);
        
        // Navigation items
        add(createNavLink("Parser", ParserView.class));
        add(createNavLink("Composer", ComposerView.class));
        add(createNavLink("Validator", ValidatorView.class));
        add(createNavLink("Documentation", DocumentationView.class));
        
        // External link to GitHub
        Anchor github = new Anchor("https://github.com/qoomon/banking-swift-messages-java", "GitHub");
        github.addClassName("external-link");
        github.setTarget("_blank");
        add(github);
    }
    
    private RouterLink createNavLink(String text, Class<? extends Component> navigationTarget) {
        RouterLink link = new RouterLink(text, navigationTarget);
        link.addClassName("nav-link");
        return link;
    }
}