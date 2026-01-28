# DOE Format Prompt: Java SWIFT Banking Messages Library

## **D** - Detailed Description

Create a comprehensive Java library for parsing and composing SWIFT banking messages (MT940, MT942, MT101). The library should provide a clean, type-safe API for reading and writing SWIFT message formats with full support for the standard SWIFT message structure including Basic Header Block, Application Header Block, User Header Block, Text Block, and Trailer Blocks.

## **O** - Objectives

1. **Core Functionality**: Implement parsers and composers for SWIFT MT940, MT942, and MT101 message types
2. **Message Structure**: Model the complete SWIFT message structure with all 5 block types
3. **Field Parsing**: Create comprehensive field parsers for all SWIFT field types with proper validation
4. **Type Safety**: Use strong typing with immutable classes and proper error handling
5. **Testing**: Achieve high test coverage with unit tests and integration tests using real SWIFT message samples
6. **Build System**: Use Maven with Java 17+ compatibility, proper dependency management, and CI/CD integration

## **E** - Execution Plan

### **Phase 1: Project Setup & Core Infrastructure**
- Create Maven project structure with proper packaging
- Configure dependencies: Google Guava, Joda Money, JUnit 4, AssertJ, Mockito
- Set up JaCoCo for test coverage reporting
- Create base package structure: `com.qoomon.banking.swift.*`
- Implement core exception classes: `SwiftMessageParseException`, `FieldParseException`, etc.

### **Phase 2: SWIFT Notation & Utilities**
- Create `SwiftNotation` class for parsing SWIFT field notation patterns
- Implement `SwiftDecimalFormatter` for number formatting
- Create utility classes for BIC and IBAN validation
- Implement charset handling and text processing utilities

### **Phase 3: Block Layer Implementation**
- Create `SwiftBlock` interface and base classes
- Implement all 5 block types:
  - `BasicHeaderBlock` ({1:})
  - `ApplicationHeaderBlock` ({2:}) with Input/Output variants
  - `UserHeaderBlock` ({3:})
  - `TextBlock` ({4:})
  - `UserTrailerBlock` ({5:}) and `SystemTrailerBlock` ({S:})
- Create `SwiftBlockReader` for parsing blocks from text
- Implement block validation and error handling

### **Phase 4: Field Layer Implementation**
- Create `SwiftField` interface and base classes
- Implement field parsers for common field types:
  - `TransactionReferenceNumber` (20)
  - `RelatedReference` (21)
  - `AccountIdentification` (25)
  - `StatementNumber` (28C)
  - `OpeningBalance` (60F)
  - `StatementLine` (61)
  - `ClosingBalance` (62F)
  - `ClosingAvailableBalance` (64)
- Create MT-specific field packages for MT940, MT942, MT101
- Implement field validation with line number tracking

### **Phase 5: Message Layer Implementation**
- Create `SwiftMessage` class with all block composition
- Implement `SwiftMessageReader` for parsing complete messages
- Create message-specific readers:
  - `MT940PageReader` for customer statement messages
  - `MT942PageReader` for interim transaction reports
  - `MT101PageReader` for single credit transfer messages
- Implement message validation and composition

### **Phase 6: BCS Message Support**
- Create `BCSMessage` class for BCS format messages
- Implement `BCSMessageParser` for BCS-specific parsing
- Add BCS message validation and error handling

### **Phase 7: Comprehensive Testing**
- Create unit tests for all classes with naming convention: `methodName_SHOULD_expectedBehavior_WHEN_condition()`
- Implement integration tests with real SWIFT message files
- Add parameterized tests for multiple similar test cases
- Test exception scenarios with proper assertion patterns
- Achieve >90% test coverage monitored by JaCoCo

### **Phase 8: Documentation & Build Configuration**
- Create comprehensive Javadoc for all public APIs
- Write README with usage examples and supported message types
- Configure Maven plugins for source JAR, Javadoc JAR, and coverage reporting
- Set up CI/CD pipeline with automated testing and coverage upload
- Add CodeClimate integration for coverage monitoring

### **Key Technical Requirements:**
- **Java Version**: Java 17 (targeting Java 21 from v2.0.0+)
- **Build Tool**: Maven 3.x
- **Dependencies**: Guava 33.4.6+, Joda Money 1.0.4+, JUnit 4.13.2+, AssertJ 3.27.3+, Mockito 5.18.0+
- **Code Style**: 4-space indentation, explicit imports, immutable classes where possible
- **Error Handling**: Custom exceptions with line numbers, meaningful error messages
- **Collections**: Use Google Guava's `ImmutableList` and `ImmutableSet` for immutable collections
- **Testing**: JUnit 4 with AssertJ fluent assertions, Mockito for mocking

### **Expected Deliverables:**
- Complete Maven project with all source code
- Comprehensive test suite with >90% coverage
- Javadoc documentation for all public APIs
- README with usage examples and integration guide
- Build artifacts: JAR, source JAR, Javadoc JAR
- CI/CD configuration with automated testing and coverage reporting

This prompt provides the complete blueprint for reproducing the Java SWIFT banking messages library with all its features, architecture, and quality standards.