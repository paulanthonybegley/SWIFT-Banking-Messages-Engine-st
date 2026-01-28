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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * View for parsing SWIFT messages from files or text input.
 */
@Route(value = "parser", layout = MainLayout.class)
@PageTitle("SWIFT Message Parser - Professional Banking Interface")
@CssImport("./styles/parser.css")
public class ParserView extends VerticalLayout {

    private static final Logger logger = LoggerFactory.getLogger(ParserView.class);
    
    private TextArea inputArea;
    private Div resultsArea;
    private MemoryBuffer buffer;
    private Upload upload;
    
    public ParserView() {
        addClassName("parser-view");
        setSizeFull();
        setSpacing(true);
        
        // Header
        H2 header = new H2("SWIFT Message Parser");
        header.addClassName("view-header");
        add(header);
        
        // Description
        Paragraph description = new Paragraph(
            "Upload SWIFT message files or paste message text directly. Supports MT940, MT942, and MT101 formats."
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
        
        // File upload
        buffer = new MemoryBuffer();
        upload = new Upload(buffer);
        upload.addClassName("file-upload");
        upload.setDropLabel(new Div("Drop SWIFT message files here or click to upload"));
        upload.setAcceptedFileTypes(".txt", ".swift", ".mt940", ".mt942", ".mt101");
        upload.setMaxFileSize(5 * 1024 * 1024); // 5MB
        
        upload.addSucceededListener(event -> {
            try {
                InputStream inputStream = buffer.getInputStream();
                String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                inputArea.setValue(content);
                Notification.show("File uploaded successfully", 3000, Notification.Position.TOP_CENTER);
            } catch (Exception e) {
                logger.error("Error reading uploaded file", e);
                Notification.show("Error reading file: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER);
            }
        });
        
        upload.addFailedListener(event -> {
            Notification.show("File upload failed: " + event.getReason().getMessage(), 
                             5000, Notification.Position.TOP_CENTER);
        });
        
        // Text input area
        inputArea = new TextArea("Or paste SWIFT message text:");
        inputArea.addClassName("input-area");
        inputArea.setWidth("100%");
        inputArea.setHeight("200px");
        inputArea.setPlaceholder("Paste your SWIFT message here...");
        
        inputSection.add(upload, inputArea);
        add(inputSection);
    }
    
    private void createResultsSection() {
        resultsArea = new Div();
        resultsArea.addClassName("results-area");
        resultsArea.setWidth("100%");
        resultsArea.setHeight("400px");
        
        // Initial message
        Div initialMessage = new Div();
        initialMessage.addClassName("initial-message");
        initialMessage.setText("Upload a file or paste SWIFT message text to see parsing results.");
        resultsArea.add(initialMessage);
        
        add(resultsArea);
    }
    
    private void createActionButtons() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        
        // Parse button
        Button parseButton = new Button("Parse Messages", new Icon(VaadinIcon.SEARCH));
        parseButton.addClassName("parse-button");
        parseButton.addClickListener(event -> parseMessages());
        
        // Clear button
        Button clearButton = new Button("Clear", new Icon(VaadinIcon.ERASER));
        clearButton.addClassName("clear-button");
        clearButton.addClickListener(event -> clearAll());
        
        // Sample button
        Button sampleButton = new Button("Load Sample", new Icon(VaadinIcon.FILE_TEXT));
        sampleButton.addClassName("sample-button");
        sampleButton.addClickListener(event -> loadSampleMessage());
        
        buttonLayout.add(parseButton, clearButton, sampleButton);
        add(buttonLayout);
    }
    
    private void parseMessages() {
        String input = inputArea.getValue().trim();
        if (input.isEmpty()) {
            Notification.show("Please enter SWIFT message text or upload a file", 
                             3000, Notification.Position.TOP_CENTER);
            return;
        }
        
        try {
            resultsArea.removeAll();
            
            // Parse the input
            List<ParsedMessage> messages = parseSwiftMessages(input);
            
            if (messages.isEmpty()) {
                Div noResults = new Div();
                noResults.addClassName("no-results");
                noResults.setText("No valid SWIFT messages found in the input.");
                resultsArea.add(noResults);
                return;
            }
            
            // Display results
            Div resultsHeader = new Div();
            resultsHeader.addClassName("results-header");
            resultsHeader.setText("Found " + messages.size() + " SWIFT message(s):");
            resultsArea.add(resultsHeader);
            
            for (ParsedMessage message : messages) {
                resultsArea.add(createMessageCard(message));
            }
            
            Notification.show("Successfully parsed " + messages.size() + " message(s)", 
                             3000, Notification.Position.TOP_CENTER);
            
        } catch (Exception e) {
            logger.error("Error parsing SWIFT messages", e);
            
            Div errorDiv = new Div();
            errorDiv.addClassName("error-message");
            errorDiv.setText("Error parsing messages: " + e.getMessage());
            resultsArea.removeAll();
            resultsArea.add(errorDiv);
            
            Notification.show("Error parsing messages", 3000, Notification.Position.TOP_CENTER);
        }
    }
    
    private List<ParsedMessage> parseSwiftMessages(String input) {
        List<ParsedMessage> messages = new ArrayList<>();
        
        // Simple parsing logic - in a real implementation, this would use the SWIFT library
        String[] lines = input.split("\\r?\\n");
        StringBuilder currentMessage = new StringBuilder();
        boolean inMessage = false;
        
        for (String line : lines) {
            line = line.trim();
            
            if (line.startsWith("{1:")) {
                if (inMessage && currentMessage.length() > 0) {
                    messages.add(createParsedMessage(currentMessage.toString()));
                }
                currentMessage = new StringBuilder();
                currentMessage.append(line).append("\n");
                inMessage = true;
            } else if (inMessage) {
                currentMessage.append(line).append("\n");
                
                if (line.startsWith("}") || line.startsWith("-}")) {
                    messages.add(createParsedMessage(currentMessage.toString()));
                    inMessage = false;
                }
            }
        }
        
        // Add the last message if we're still in one
        if (inMessage && currentMessage.length() > 0) {
            messages.add(createParsedMessage(currentMessage.toString()));
        }
        
        return messages;
    }
    
    private ParsedMessage createParsedMessage(String content) {
        // Simple message type detection
        String messageType = "Unknown";
        if (content.contains("I940") || content.contains("O940")) {
            messageType = "MT940 (Customer Statement)";
        } else if (content.contains("I942") || content.contains("O942")) {
            messageType = "MT942 (Interim Transaction Report)";
        } else if (content.contains("I101") || content.contains("O101")) {
            messageType = "MT101 (Direct Debit)";
        }
        
        return new ParsedMessage(messageType, content);
    }
    
    private Div createMessageCard(ParsedMessage message) {
        Div card = new Div();
        card.addClassName("message-card");
        
        // Message header
        Div header = new Div();
        header.addClassName("message-header");
        header.setText(message.getType());
        card.add(header);
        
        // Message content
        TextArea contentArea = new TextArea();
        contentArea.addClassName("message-content");
        contentArea.setValue(message.getContent());
        contentArea.setWidth("100%");
        contentArea.setHeight("150px");
        contentArea.setReadOnly(true);
        card.add(contentArea);
        
        return card;
    }
    
    private void clearAll() {
        inputArea.clear();
        resultsArea.removeAll();
        
        Div initialMessage = new Div();
        initialMessage.addClassName("initial-message");
        initialMessage.setText("Upload a file or paste SWIFT message text to see parsing results.");
        resultsArea.add(initialMessage);
    }
    
    private void loadSampleMessage() {
        String sampleMessage = "{1:F01BANKDEFFXXXX1234567890}\n" +
                               "{2:I940BANKDEFFXXXXN}\n" +
                               "{3:{108:TEST12345}}\n" +
                               "{4:\n" +
                               ":20:TEST123456\n" +
                               ":25:1234567890/DE1234567890\n" +
                               ":28C:00001/001\n" +
                               ":60F:C231001EUR1000,00\n" +
                               ":61:231001C1000,00NTRFNONREF\n" +
                               ":62F:C231001EUR2000,00\n" +
                               "-}\n" +
                               "{5:{CHK:123456789}}";
        
        inputArea.setValue(sampleMessage);
        Notification.show("Sample MT940 message loaded", 3000, Notification.Position.TOP_CENTER);
    }
    
    /**
     * Simple data class for parsed messages.
     */
    private static class ParsedMessage {
        private final String type;
        private final String content;
        
        public ParsedMessage(String type, String content) {
            this.type = type;
            this.content = content;
        }
        
        public String getType() { return type; }
        public String getContent() { return content; }
    }
}