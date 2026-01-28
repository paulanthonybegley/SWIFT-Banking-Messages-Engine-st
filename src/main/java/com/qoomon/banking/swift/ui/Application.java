package com.qoomon.banking.swift.ui;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * Main application configuration and entry point.
 */
@Theme(themeClass = Lumo.class, variant = Lumo.DARK)
@PWA(name = "SWIFT Banking Messages", shortName = "SWIFT Messages")
public class Application implements AppShellConfigurator {
    
    public static void main(String[] args) {
        // Simple standalone mode for testing
        if (args.length > 0 && "standalone".equals(args[0])) {
            System.out.println("SWIFT Banking Messages application starting in standalone mode...");
            return;
        }
        
        // For servlet container deployment
        System.out.println("SWIFT Banking Messages application configured successfully");
        System.out.println("Application should be deployed to a servlet container or embedded server");
        System.out.println("To test with embedded development server, use: java -jar target/banking-swift-messages-0.0.0-SNAPSHOT.jar");
    }
    
    public String getPageTitle() {
        return "SWIFT Banking Messages";
    }
}