package com.qoomon.banking.swift.ui;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * View for documentation and help.
 */
@Route(value = "documentation", layout = MainLayout.class)
@PageTitle("Documentation - Professional Banking Interface")
@CssImport("./styles/documentation.css")
public class DocumentationView extends VerticalLayout {

    public DocumentationView() {
        addClassName("documentation-view");
        setSizeFull();
        setSpacing(true);
        setPadding(true);
        
        // Header
        H2 header = new H2("SWIFT Banking Messages Documentation");
        header.addClassName("view-header");
        add(header);
        
        // Overview section
        createOverviewSection();
        
        // Message types section
        createMessageTypesSection();
        
        // API section
        createApiSection();
        
        // Examples section
        createExamplesSection();
    }
    
    private void createOverviewSection() {
        Div section = new Div();
        section.addClassName("doc-section");
        
        H3 title = new H3("Overview");
        title.addClassName("section-title");
        
        Paragraph overview = new Paragraph(
            "The SWIFT Banking Messages library provides a comprehensive solution for parsing, " +
            "composing, and validating SWIFT banking messages. This web interface offers an " +
            "intuitive way to work with MT940, MT942, and MT101 message formats."
        );
        
        section.add(title, overview);
        add(section);
    }
    
    private void createMessageTypesSection() {
        Div section = new Div();
        section.addClassName("doc-section");
        
        H3 title = new H3("Supported Message Types");
        title.addClassName("section-title");
        
        Div mt940 = createMessageTypeCard(
            "MT940",
            "Customer Statement Message",
            "End-of-day bank account statements showing all booked transactions.",
            "Opening/Closing balances, transaction lines, account identification"
        );
        
        Div mt942 = createMessageTypeCard(
            "MT942",
            "Interim Transaction Report",
            "Interim transaction reporting during the day for real-time monitoring.",
            "Floor limits, transaction summaries, datetime indicators"
        );
        
        Div mt101 = createMessageTypeCard(
            "MT101",
            "Direct Debit Message",
            "Payment instructions and direct debit requests for automated processing.",
            "Sender reference, beneficiary details, remittance information"
        );
        
        section.add(title, mt940, mt942, mt101);
        add(section);
    }
    
    private Div createMessageTypeCard(String type, String title, String description, String features) {
        Div card = new Div();
        card.addClassName("message-type-card");
        
        Div typeHeader = new Div();
        typeHeader.addClassName("type-header");
        typeHeader.setText(type);
        card.add(typeHeader);
        
        Div typeTitle = new Div();
        typeTitle.addClassName("type-title");
        typeTitle.setText(title);
        card.add(typeTitle);
        
        Div typeDescription = new Div();
        typeDescription.addClassName("type-description");
        typeDescription.setText(description);
        card.add(typeDescription);
        
        Div typeFeatures = new Div();
        typeFeatures.addClassName("type-features");
        typeFeatures.setText("Key Features: " + features);
        card.add(typeFeatures);
        
        return card;
    }
    
    private void createApiSection() {
        Div section = new Div();
        section.addClassName("doc-section");
        
        H3 title = new H3("API Reference");
        title.addClassName("section-title");
        
        Paragraph apiIntro = new Paragraph(
            "The underlying Java library provides a clean, type-safe API for working with SWIFT messages:"
        );
        
        Div apiCode = new Div();
        apiCode.addClassName("code-example");
        apiCode.setText(
            "// Parse SWIFT messages\n" +
            "SwiftMessageReader reader = new SwiftMessageReader(stringReader);\n" +
            "SwiftMessage message = reader.read();\n" +
            "List<SwiftMessage> messages = reader.readAll();\n\n" +
            "// Parse specific message types\n" +
            "MT940PageReader mt940Reader = new MT940PageReader(stringReader);\n" +
            "MT940Page mt940Page = mt940Reader.read();\n\n" +
            "// Validate IBAN and BIC\n" +
            "IBAN.ensureValid(ibanText);\n" +
            "BIC.ensureValid(bicText);"
        );
        
        section.add(title, apiIntro, apiCode);
        add(section);
    }
    
    private void createExamplesSection() {
        Div section = new Div();
        section.addClassName("doc-section");
        
        H3 title = new H3("Usage Examples");
        title.addClassName("section-title");
        
        Paragraph exampleIntro = new Paragraph(
            "This web interface provides three main functions:"
        );
        
        Div parserExample = createExampleCard(
            "Parser",
            "Upload SWIFT message files or paste message text to parse and validate. The parser automatically detects message types and displays structured results.",
            "File upload, text parsing, error handling, message type detection"
        );
        
        Div composerExample = createExampleCard(
            "Composer",
            "Create SWIFT messages by filling in required fields. The composer generates properly formatted SWIFT messages with all necessary blocks.",
            "Form-based composition, field validation, message generation, clipboard copy"
        );
        
        Div validatorExample = createExampleCard(
            "Validator",
            "Validate IBAN and BIC codes with detailed error messages and format checking.",
            "Format validation, checksum verification, detailed error reporting"
        );
        
        section.add(title, exampleIntro, parserExample, composerExample, validatorExample);
        add(section);
    }
    
    private Div createExampleCard(String title, String description, String features) {
        Div card = new Div();
        card.addClassName("example-card");
        
        Div cardTitle = new Div();
        cardTitle.addClassName("example-title");
        cardTitle.setText(title);
        card.add(cardTitle);
        
        Div cardDescription = new Div();
        cardDescription.addClassName("example-description");
        cardDescription.setText(description);
        card.add(cardDescription);
        
        Div cardFeatures = new Div();
        cardFeatures.addClassName("example-features");
        cardFeatures.setText("Features: " + features);
        card.add(cardFeatures);
        
        return card;
    }
}