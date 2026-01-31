# AGENTS.md

## Project Overview

This is a Java library for parsing and composing SWIFT banking messages. It supports MT940 and MT942 message formats and is built with Maven using Java 17 (targeting Java 21 from version 2.0.0+).

## Build Commands

### Maven Commands
- **Build project**: `mvn compile` 
- **Run tests**: `mvn test`
- **Run single test**: `mvn test -Dtest=ClassName#methodName` (e.g., `mvn test -Dtest=SwiftMessageReaderTest#parse_SHOULD_read_multiple_messages`)
- **Run all tests with coverage**: `mvn verify`
- **Package JAR**: `mvn package`
- **Generate javadoc**: `mvn javadoc:javadoc`
- **Generate source JAR**: `mvn source:jar`
- **Clean build**: `mvn clean`

### Test Coverage
- JaCoCo is configured for coverage reporting
- Reports generated at `target/site/jacoco/jacoco.xml`
- Coverage automatically runs during `test` phase

## Code Style Guidelines

### Imports
- Use explicit imports (no wildcard imports)
- Group imports: standard Java libraries, third-party libraries, project packages
- Third-party libraries used: Google Guava, JUnit, AssertJ, Mockito, Joda Money

### Formatting
- Use standard Java naming conventions
- 4-space indentation (no tabs)
- Line length should be reasonable (not explicitly enforced)
- Use final modifiers where appropriate
- Private static final constants for immutable values

### Types and Generics
- Use specific types instead of raw types
- Use diamond operator for type inference when possible
- Prefer interfaces over implementations for method parameters and return types
- Use Google Guava's `ImmutableList` and `ImmutableSet` for immutable collections

### Naming Conventions
- Classes: PascalCase (e.g., `SwiftMessageReader`, `GeneralField`)
- Methods: camelCase (e.g., `read()`, `getTag()`)
- Constants: UPPER_SNAKE_CASE (e.g., `BLOCK_ID_1`, `MESSAGE_START_BLOCK_ID_SET`)
- Package names: lowercase with dots (e.g., `com.qoomon.banking.swift.message`)
- Variables: camelCase, descriptive names

### Error Handling
- Use custom exceptions with descriptive names (e.g., `SwiftMessageParseException`, `FieldParseException`)
- Include line numbers in parse exceptions for debugging
- Use Google Guava's `Preconditions.checkArgument()` for input validation
- Wrap checked exceptions in custom unchecked exceptions where appropriate
- Provide meaningful error messages with context

### Class Design
- Favor immutable classes where possible
- Use interfaces for key abstractions (e.g., `SwiftField`, `SwiftBlock`)
- Implement static factory methods named `of()` for creating instances from generic types
- Use builder pattern for complex object construction
- Separate concerns with distinct packages (message, field, block, notation)

### Testing Guidelines
- Use JUnit 4 with AssertJ assertions
- Test method naming: `methodName_SHOULD_expectedBehavior_WHEN_condition()`
- Use `@Test` annotation for test methods
- Use AssertJ's fluent assertion style: `assertThat(actual).isEqualTo(expected)`
- Test exceptions with `catchThrowable()` and assert exception type and properties
- Use parameterized tests for multiple similar test cases
- Include integration tests with real SWIFT message files

### Documentation
- Add Javadoc comments for public APIs
- Include usage examples in test classes
- Document charset notation details in comments
- Explain complex regex patterns and parsing logic
- Use markdown in README with examples

### Dependencies
- **Guava**: For collections, Preconditions, Throwables
- **Joda Money**: For financial amount handling
- **JUnit 4**: Testing framework
- **AssertJ**: Fluent assertions
- **Mockito**: Mocking framework for tests

### Package Structure
- `com.qoomon.banking.swift.message`: Core message parsing
- `com.qoomon.banking.swift.message.block`: SWIFT block handling
- `com.qoomon.banking.swift.submessage.field`: Field parsing and models
- `com.qoomon.banking.swift.submessage.field.mt940/mt942/mt101`: Message type specific fields
- `com.qoomon.banking.swift.notation`: SWIFT notation parsing
- `com.qoomon.banking.bic/iban`: Bank identifier utilities

### Code Quality
- Aim for high test coverage (monitored by CodeClimate)
- Keep methods small and focused
- Use meaningful variable names
- Avoid deep nesting
- Extract complex logic into separate methods
- Use static imports judiciously for test assertions

### Git Workflow
- Feature branches for new developments
- CI builds run on every push with `mvn verify`
- Main branch triggers CodeClimate coverage upload
- Follow conventional commit messages