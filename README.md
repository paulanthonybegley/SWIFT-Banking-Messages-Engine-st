# Banking Swift Messages Parser and Composer [![starline](https://starlines.qoo.monster/assets/qoomon/banking-swift-messages-java)](https://github.com/qoomon/starline)

Professional SWIFT Banking Messages Library with Web Interface
SWIFT = Society for Worldwide Interbank Financial Telecommunication

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Build Workflow](https://github.com/qoomon/banking-swift-messages-java/workflows/Build/badge.svg)](https://github.com/qoomon/banking-swift-messages-java/actions)
[![Test Coverage](https://api.codeclimate.com/v1/badges/e611239eea560ee9c72c/test_coverage)](https://codeclimate.com/github/qoomon/banking-swift-messages-java/test_coverage)

## 🌟 Features

- **Web Interface**: Professional Vaadin-based UI for parsing, composing, and validating SWIFT messages
- **File Upload**: Drag-and-drop SWIFT message file parsing
- **Message Composer**: Form-based SWIFT message creation with validation
- **Code Validator**: IBAN and BIC format validation with detailed error reporting
- **Responsive Design**: Modern, professional interface that works on all devices
- **Dark Theme**: Sleek dark theme optimized for extended use

### Releases

[![Release](https://jitpack.io/v/qoomon/banking-swift-messages-java.svg)](https://jitpack.io/#qoomon/banking-swift-messages-java)

> [!Important]
> From version `2.0.0` on Java 21 is required


#### Supported Message Types
* **MT940** - Customer Statement Message
* **MT942** - Interim Transaction Report  
* **MT101** - Direct Debit Message

#### Web Interface Features
- **Parser View**: Upload or paste SWIFT messages for parsing and validation
- **Composer View**: Create SWIFT messages using intuitive forms
- **Validator View**: Validate IBAN and BIC codes with detailed feedback
- **Documentation**: Comprehensive help and API reference

If you need more MT formats just let me know and create a new [issue](https://github.com/qoomon/banking-swift-messages-java/issues)

## 🚀 Quick Start

### Running the Web Application

1. **Build and Run**:
   ```bash
   mvn clean package
   mvn jetty:run
   ```

2. **Access the Application**:
   Open your browser and navigate to `http://localhost:8080`

3. **Using the Interface**:
   - **Parser**: Upload SWIFT files or paste message text
   - **Composer**: Fill forms to generate SWIFT messages
   - **Validator**: Check IBAN/BIC formats
   - **Documentation**: View API reference and examples

### Library Usage

see [tests](/src/test/java/com/qoomon/banking/swift/message/SwiftMessageReaderTest.java)


## 📚 Web Interface Documentation

### Parser View
- **File Upload**: Drag-and-drop SWIFT files (.txt, .swift, .mt940, .mt942, .mt101)
- **Text Input**: Paste SWIFT message text directly
- **Sample Data**: Load sample MT940 message for testing
- **Error Handling**: Clear error messages with line numbers

### Composer View
- **Message Types**: MT940, MT942, MT101 with appropriate fields
- **Field Validation**: Real-time validation for all inputs
- **Generated Output**: Properly formatted SWIFT messages
- **Clipboard Copy**: One-click copy to clipboard

### Validator View
- **IBAN Validation**: Format and basic checksum validation
- **BIC Validation**: Structure and format checking
- **Detailed Feedback**: Specific error messages and explanations
- **Sample Data**: Pre-loaded valid examples

## 🛠 Development

### Building the Application
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Build WAR file
mvn package

# Run with Jetty (development)
mvn jetty:run

# Production build
mvn clean package -Pproduction
```

### Frontend Development
- **Framework**: Vaadin 24.x with Lumo Dark theme
- **Styling**: Custom CSS with responsive design
- **Testing**: Vaadin TestBench for UI testing
- **Build**: Maven with frontend optimization

## Dev Notes
[SEPA Verwendugszweck Fields](https://www.hettwer-beratung.de/sepa-spezialwissen/sepa-technische-anforderungen/sepa-gesch%C3%A4ftsvorfallcodes-gvc-mt-940/)
* EREF : Ende-zu-Ende Referenz
* KREF : Kundenreferenz
* MREF : Mandatsreferenz
* BREF : Bankreferenz
* RREF : Retourenreferenz
* CRED : Creditor-ID
* DEBT : Debitor-ID
* COAM : Zinskompensationsbetrag
* OAMT : Ursprünglicher Umsatzbetrag
* SVWZ : Verwendungszweck
* ABWA : Abweichender Auftraggeber
* ABWE : Abweichender Empfänger
* IBAN : IBAN des Auftraggebers
* BIC : BIC des Auftraggebers
