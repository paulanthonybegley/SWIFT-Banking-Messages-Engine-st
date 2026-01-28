package com.qoomon.banking.swift.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Arrays;
import java.util.List;

/**
 * View for composing SWIFT messages.
 */
@Route(value = "composer", layout = MainLayout.class)
@PageTitle("SWIFT Message Composer - Professional Banking Interface")
@CssImport("./styles/composer.css")
public class ComposerView extends VerticalLayout {

    private ComboBox<String> messageTypeCombo;
    private TextField referenceField;
    private TextField accountField;
    private TextField amountField;
    private TextArea descriptionArea;
    private TextArea outputArea;
    
    public ComposerView() {
        addClassName("composer-view");
        setSizeFull();
        setSpacing(true);
        
        // Header
        H2 header = new H2("SWIFT Message Composer");
        header.addClassName("view-header");
        add(header);
        
        // Description
        Paragraph description = new Paragraph(
            "Create SWIFT banking messages by filling in the required fields. Select message type and provide the necessary information."
        );
        description.addClassName("view-description");
        add(description);
        
        // Form section
        createFormSection();
        
        // Output section
        createOutputSection();
        
        // Action buttons
        createActionButtons();
        
        expand(outputArea);
    }
    
    private void createFormSection() {
        Div formSection = new Div();
        formSection.addClassName("form-section");
        
        // Message type
        messageTypeCombo = new ComboBox<>("Message Type");
        messageTypeCombo.addClassName("message-type-combo");
        messageTypeCombo.setItems("MT940 - Customer Statement", 
                                "MT942 - Interim Transaction Report", 
                                "MT101 - Direct Debit");
        messageTypeCombo.setValue("MT940 - Customer Statement");
        messageTypeCombo.setWidth("300px");
        
        // Reference
        referenceField = new TextField("Reference Number");
        referenceField.addClassName("reference-field");
        referenceField.setWidth("300px");
        referenceField.setPlaceholder("e.g., TEST123456");
        
        // Account
        accountField = new TextField("Account Number");
        accountField.addClassName("account-field");
        accountField.setWidth("300px");
        accountField.setPlaceholder("e.g., 1234567890/DE1234567890");
        
        // Amount
        amountField = new TextField("Amount");
        amountField.addClassName("amount-field");
        amountField.setWidth("300px");
        amountField.setPlaceholder("e.g., 1000,00");
        
        // Description
        descriptionArea = new TextArea("Description/Remittance Info");
        descriptionArea.addClassName("description-area");
        descriptionArea.setWidth("100%");
        descriptionArea.setHeight("100px");
        descriptionArea.setPlaceholder("Enter transaction description...");
        
        formSection.add(messageTypeCombo, referenceField, accountField, amountField, descriptionArea);
        add(formSection);
    }
    
    private void createOutputSection() {
        Div outputSection = new Div();
        outputSection.addClassName("output-section");
        
        H2 outputHeader = new H2("Generated SWIFT Message");
        outputHeader.addClassName("output-header");
        
        outputArea = new TextArea();
        outputArea.addClassName("output-area");
        outputArea.setWidth("100%");
        outputArea.setHeight("300px");
        outputArea.setPlaceholder("Generated SWIFT message will appear here...");
        outputArea.setReadOnly(true);
        
        outputSection.add(outputHeader, outputArea);
        add(outputSection);
    }
    
    private void createActionButtons() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        
        // Generate button
        Button generateButton = new Button("Generate Message", new Icon(VaadinIcon.CHECK));
        generateButton.addClassName("generate-button");
        generateButton.addClickListener(event -> generateMessage());
        
        // Clear button
        Button clearButton = new Button("Clear", new Icon(VaadinIcon.ERASER));
        clearButton.addClassName("clear-button");
        clearButton.addClickListener(event -> clearAll());
        
        // Copy button
        Button copyButton = new Button("Copy to Clipboard", new Icon(VaadinIcon.CLIPBOARD));
        copyButton.addClassName("copy-button");
        copyButton.addClickListener(event -> copyToClipboard());
        
        buttonLayout.add(generateButton, clearButton, copyButton);
        add(buttonLayout);
    }
    
    private void generateMessage() {
        try {
            String messageType = messageTypeCombo.getValue();
            String reference = referenceField.getValue();
            String account = accountField.getValue();
            String amount = amountField.getValue();
            String description = descriptionArea.getValue();
            
            if (reference.isEmpty() || account.isEmpty()) {
                Notification.show("Please fill in all required fields", 
                                 3000, Notification.Position.TOP_CENTER);
                return;
            }
            
            String swiftMessage = generateSwiftMessage(messageType, reference, account, amount, description);
            outputArea.setValue(swiftMessage);
            
            Notification.show("SWIFT message generated successfully", 
                             3000, Notification.Position.TOP_CENTER);
            
        } catch (Exception e) {
            Notification.show("Error generating message: " + e.getMessage(), 
                             5000, Notification.Position.TOP_CENTER);
        }
    }
    
    private String generateSwiftMessage(String messageType, String reference, String account, 
                                       String amount, String description) {
        StringBuilder message = new StringBuilder();
        
        // Basic Header Block
        message.append("{1:F01BANKDEFFXXXX1234567890}\n");
        
        // Application Header Block
        if (messageType.contains("MT940")) {
            message.append("{2:I940BANKDEFFXXXXN}\n");
        } else if (messageType.contains("MT942")) {
            message.append("{2:I942BANKDEFFXXXXN}\n");
        } else if (messageType.contains("MT101")) {
            message.append("{2:I101BANKDEFFXXXXN}\n");
        }
        
        // User Header Block
        message.append("{3:{108:").append(reference).append("}}\n");
        
        // Text Block
        message.append("{4:\n");
        message.append(":20:").append(reference).append("\n");
        message.append(":25:").append(account).append("\n");
        message.append(":28C:00001/001\n");
        
        if (!amount.isEmpty()) {
            message.append(":60F:C231001EUR").append(amount).append("\n");
            message.append(":61:231001C").append(amount).append("NTRFNONREF\n");
            message.append(":62F:C231001EUR").append(amount).append("\n");
        }
        
        if (!description.isEmpty()) {
            message.append(":86:").append(description).append("\n");
        }
        
        message.append("-}\n");
        
        // Trailer Block
        message.append("{5:{CHK:123456789}}");
        
        return message.toString();
    }
    
    private void clearAll() {
        messageTypeCombo.setValue("MT940 - Customer Statement");
        referenceField.clear();
        accountField.clear();
        amountField.clear();
        descriptionArea.clear();
        outputArea.clear();
    }
    
    private void copyToClipboard() {
        String content = outputArea.getValue();
        if (content.isEmpty()) {
            Notification.show("No message to copy", 3000, Notification.Position.TOP_CENTER);
            return;
        }
        
        // Use JavaScript to copy to clipboard
        getUI().ifPresent(ui -> {
            ui.getPage().executeJs("navigator.clipboard.writeText($0)", content);
            Notification.show("Message copied to clipboard", 3000, Notification.Position.TOP_CENTER);
        });
    }
}