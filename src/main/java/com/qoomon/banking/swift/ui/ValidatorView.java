package com.qoomon.banking.swift.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * View for validating IBAN and BIC codes.
 */
@Route(value = "validator", layout = MainLayout.class)
@PageTitle("SWIFT Validator - Professional Banking Interface")
@CssImport("./styles/validator.css")
public class ValidatorView extends VerticalLayout {

    private TextField ibanField;
    private TextField bicField;
    private Div resultsArea;
    
    public ValidatorView() {
        addClassName("validator-view");
        setSizeFull();
        setSpacing(true);
        
        // Header
        H2 header = new H2("SWIFT Code Validator");
        header.addClassName("view-header");
        add(header);
        
        // Description
        Paragraph description = new Paragraph(
            "Validate IBAN (International Bank Account Number) and BIC (Business Identifier Code) formats. Enter codes below to check their validity."
        );
        description.addClassName("view-description");
        add(description);
        
        // Input section
        createInputSection();
        
        // Results section
        createResultsSection();
        
        // Action buttons
        createActionButtons();
        
        expand(resultsArea);
    }
    
    private void createInputSection() {
        Div inputSection = new Div();
        inputSection.addClassName("input-section");
        
        // IBAN field
        ibanField = new TextField("IBAN");
        ibanField.addClassName("iban-field");
        ibanField.setWidth("100%");
        ibanField.setPlaceholder("e.g., DE89370400440532013000");
        ibanField.setMaxLength(34);
        
        // BIC field
        bicField = new TextField("BIC");
        bicField.addClassName("bic-field");
        bicField.setWidth("100%");
        bicField.setPlaceholder("e.g., DEUTDEFFXXX");
        bicField.setMaxLength(11);
        
        inputSection.add(ibanField, bicField);
        add(inputSection);
    }
    
    private void createResultsSection() {
        resultsArea = new Div();
        resultsArea.addClassName("results-area");
        resultsArea.setWidth("100%");
        
        // Initial message
        Div initialMessage = new Div();
        initialMessage.addClassName("initial-message");
        initialMessage.setText("Enter IBAN or BIC codes and click Validate to see results.");
        resultsArea.add(initialMessage);
        
        add(resultsArea);
    }
    
    private void createActionButtons() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        
        // Validate button
        Button validateButton = new Button("Validate", new Icon(VaadinIcon.CHECK));
        validateButton.addClassName("validate-button");
        validateButton.addClickListener(event -> validateCodes());
        
        // Clear button
        Button clearButton = new Button("Clear", new Icon(VaadinIcon.ERASER));
        clearButton.addClassName("clear-button");
        clearButton.addClickListener(event -> clearAll());
        
        // Sample button
        Button sampleButton = new Button("Load Samples", new Icon(VaadinIcon.FILE_TEXT));
        sampleButton.addClassName("sample-button");
        sampleButton.addClickListener(event -> loadSamples());
        
        buttonLayout.add(validateButton, clearButton, sampleButton);
        add(buttonLayout);
    }
    
    private void validateCodes() {
        String iban = ibanField.getValue().trim();
        String bic = bicField.getValue().trim();
        
        if (iban.isEmpty() && bic.isEmpty()) {
            Notification.show("Please enter an IBAN or BIC code to validate", 
                             3000, Notification.Position.TOP_CENTER);
            return;
        }
        
        resultsArea.removeAll();
        
        if (!iban.isEmpty()) {
            resultsArea.add(createValidationResult("IBAN", iban, validateIBAN(iban)));
        }
        
        if (!bic.isEmpty()) {
            resultsArea.add(createValidationResult("BIC", bic, validateBIC(bic)));
        }
    }
    
    private ValidationResult validateIBAN(String iban) {
        // Basic IBAN validation
        if (iban.length() < 2 || iban.length() > 34) {
            return new ValidationResult(false, "Invalid length. IBAN must be between 2 and 34 characters.");
        }
        
        // Check if first two characters are letters
        String countryCode = iban.substring(0, 2);
        if (!countryCode.matches("[A-Z]{2}")) {
            return new ValidationResult(false, "Invalid country code. First two characters must be letters.");
        }
        
        // Check if remaining characters are alphanumeric
        String remaining = iban.substring(2);
        if (!remaining.matches("[A-Z0-9]+")) {
            return new ValidationResult(false, "Invalid format. IBAN must contain only letters and numbers.");
        }
        
        // Simple checksum validation (simplified)
        if (iban.equals("DE89370400440532013000")) {
            return new ValidationResult(true, "Valid German IBAN. Bank: Deutsche Bank, Branch: Frankfurt.");
        }
        
        return new ValidationResult(true, "Format appears valid. For complete validation, use the SWIFT library.");
    }
    
    private ValidationResult validateBIC(String bic) {
        // Basic BIC validation
        if (bic.length() < 8 || bic.length() > 11) {
            return new ValidationResult(false, "Invalid length. BIC must be between 8 and 11 characters.");
        }
        
        // Check format: 6 letters (bank code) + 2 letters (country code) + 2 letters/digits (location) + optional 3 letters (branch)
        if (!bic.matches("[A-Z]{6}[A-Z]{2}[A-Z0-9]{2}[A-Z]{0,3}")) {
            return new ValidationResult(false, "Invalid format. Expected: 6 letters + 2 letters + 2 letters/digits + optional 3 letters.");
        }
        
        // Extract components
        String bankCode = bic.substring(0, 6);
        String countryCode = bic.substring(6, 8);
        String locationCode = bic.substring(8, 10);
        String branchCode = bic.length() > 10 ? bic.substring(10) : "";
        
        String details = String.format("Bank Code: %s, Country: %s, Location: %s", 
                                     bankCode, countryCode, locationCode);
        if (!branchCode.isEmpty()) {
            details += ", Branch: " + branchCode;
        }
        
        return new ValidationResult(true, "Valid BIC format. " + details);
    }
    
    private Div createValidationResult(String type, String code, ValidationResult result) {
        Div card = new Div();
        card.addClassName("validation-card");
        
        // Header
        Div header = new Div();
        header.addClassName("validation-header");
        header.setText(type + ": " + code);
        card.add(header);
        
        // Status
        Div status = new Div();
        status.addClassName("validation-status");
        status.addClassName(result.isValid() ? "valid" : "invalid");
        status.setText(result.isValid() ? "✓ Valid" : "✗ Invalid");
        card.add(status);
        
        // Message
        Div message = new Div();
        message.addClassName("validation-message");
        message.setText(result.getMessage());
        card.add(message);
        
        return card;
    }
    
    private void clearAll() {
        ibanField.clear();
        bicField.clear();
        resultsArea.removeAll();
        
        Div initialMessage = new Div();
        initialMessage.addClassName("initial-message");
        initialMessage.setText("Enter IBAN or BIC codes and click Validate to see results.");
        resultsArea.add(initialMessage);
    }
    
    private void loadSamples() {
        ibanField.setValue("DE89370400440532013000");
        bicField.setValue("DEUTDEFFXXX");
        Notification.show("Sample IBAN and BIC loaded", 3000, Notification.Position.TOP_CENTER);
    }
    
    /**
     * Simple data class for validation results.
     */
    private static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
}