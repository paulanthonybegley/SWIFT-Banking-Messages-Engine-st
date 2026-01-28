# SWIFT MT103 Technical Specifications

## **Phase 1: Core Infrastructure - Detailed Technical Specs**

### **Step 1.1: Package Structure and File Creation**

**New Package Structure:**
```
src/main/java/com/qoomon/banking/swift/submessage/field/mt103/
├── SendersReference.java
├── BankOperationCode.java  
├── ValueDateCurrencyAmount.java
├── OrderingCustomer.java
├── BeneficiaryCustomer.java
├── DetailsOfCharges.java
└── PartyIdentificationField.java (abstract base for 50a, 52a, 56a, 57a, 58a, 59a)

src/main/java/com/qoomon/banking/swift/submessage/mt103/
├── MT103Page.java
└── MT103PageReader.java

src/test/java/com/qoomon/banking/swift/submessage/mt103/
├── MT103PageReaderTest.java
├── MT103PageTest.java
└── field/
    ├── SendersReferenceTest.java
    ├── BankOperationCodeTest.java
    ├── ValueDateCurrencyAmountTest.java
    ├── OrderingCustomerTest.java
    ├── BeneficiaryCustomerTest.java
    └── DetailsOfChargesTest.java
```

### **Step 1.2: Mandatory Field Implementation - Technical Specs**

#### **1. SendersReference Field (`:20:`)**
```java
// Technical Specification for SendersReference.java
public class SendersReference implements SwiftField {
    public static final String TAG = "20";
    public static final String NOTATION = "16x";  // 16 characters, any
    private static final SwiftNotation SWIFT_NOTATION = new SwiftNotation(NOTATION);
    
    private final String reference;
    
    public SendersReference(String reference) {
        // Validation: not null, not empty, max 16 chars
        this.reference = reference;
    }
    
    public static SendersReference of(GeneralField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG), "Expected tag %s, got %s", TAG, field.getTag());
        // Parse using existing SwiftFieldReader pattern
        List<String> values = SWIFT_NOTATION.parse(field.getContent());
        return new SendersReference(values.get(0));
    }
    
    @Override public String getTag() { return TAG; }
    @Override public String getContent() { return reference; }
}
```

#### **2. BankOperationCode Field (`:23B:`)**
```java
// Technical Specification for BankOperationCode.java
public class BankOperationCode implements SwiftField {
    public static final String TAG = "23B";
    public static final String NOTATION = "4!c";  // 4 specific codes
    private static final Set<String> VALID_CODES = ImmutableSet.of("CRED", "SPAY", "SPRI", "SSTD");
    private static final SwiftNotation SWIFT_NOTATION = new SwiftNotation(NOTATION);
    
    private final String code;
    
    public static BankOperationCode of(GeneralField field) {
        List<String> values = SWIFT_NOTATION.parse(field.getContent());
        String code = values.get(0);
        Preconditions.checkArgument(VALID_CODES.contains(code), 
            "Invalid bank operation code: %s. Valid codes: %s", code, VALID_CODES);
        return new BankOperationCode(code);
    }
}
```

#### **3. ValueDateCurrencyAmount Field (`:32A:`) - Most Complex**
```java
// Technical Specification for ValueDateCurrencyAmount.java
public class ValueDateCurrencyAmount implements SwiftField {
    public static final String TAG = "32A";
    public static final String NOTATION = "6!n3!a15d";  // YYMMDD + Currency + Amount
    private static final SwiftNotation SWIFT_NOTATION = new SwiftNotation(NOTATION);
    
    private final LocalDate valueDate;
    private final Currency currency; 
    private final Money amount;
    
    public static ValueDateCurrencyAmount of(GeneralField field) {
        List<String> values = SWIFT_NOTATION.parse(field.getContent());
        String dateStr = values.get(0);      // YYMMDD
        String currencyStr = values.get(1);  // 3-letter currency
        String amountStr = values.get(2);    // Decimal with comma
        
        // Date validation and parsing
        LocalDate valueDate = parseYYMMDD(dateStr);
        
        // Currency validation
        Currency currency = Currency.getInstance(currencyStr);
        
        // Amount parsing using existing pattern
        Money amount = parseSwiftAmount(amountStr, currency);
        
        return new ValueDateCurrencyAmount(valueDate, currency, amount);
    }
    
    private static LocalDate parseYYMMDD(String dateStr) {
        // Handle Y2K ambiguity: if year < 50, assume 2000s, else 1900s
        int year = Integer.parseInt(dateStr.substring(0, 2));
        year += (year < 50) ? 2000 : 1900;
        int month = Integer.parseInt(dateStr.substring(2, 4));
        int day = Integer.parseInt(dateStr.substring(4, 6));
        return LocalDate.of(year, month, day);
    }
}
```

#### **4. Party Identification Fields (`:50a:`, `:59a:`) - Multi-Format**
```java
// Technical Specification for PartyIdentificationField.java
public abstract class PartyIdentificationField implements SwiftField {
    
    // Option A: /account/name
    public static class OptionA extends PartyIdentificationField {
        private final String account;
        private final String name;
        
        public static PartyIdentificationField of(String content) {
            Preconditions.checkArgument(content.startsWith("/"), "Option A must start with '/'");
            String[] parts = content.substring(1).split("/", 2);
            return new OptionA(parts[0], parts.length > 1 ? parts[1] : "");
        }
    }
    
    // Option F: address/name (multiple lines)
    public static class OptionF extends PartyIdentificationField {
        private final List<String> addressLines;
        private final String name;
        
        public static PartyIdentificationField of(String content) {
            // Parse multi-line format
            String[] lines = content.split("\n");
            String name = lines[lines.length - 1]; // Last line is name
            List<String> address = Arrays.asList(Arrays.copyOf(lines, lines.length - 1));
            return new OptionF(address, name);
        }
    }
    
    // Option K: Beneficiary statement
    public static class OptionK extends PartyIdentificationField {
        private final String statement;
        
        public static PartyIdentificationField of(String content) {
            return new OptionK(content);
        }
    }
    
    public static PartyIdentificationField of(String tag, GeneralField field) {
        String content = field.getContent();
        if (content.startsWith("/")) {
            return OptionA.of(content);
        } else if (content.contains("\n") || isAddressPattern(content)) {
            return OptionF.of(content);
        } else {
            return OptionK.of(content);
        }
    }
}
```

### **Step 1.3: MT103Page and MT103PageReader - Technical Specs**

#### **MT103Page Class:**
```java
// Technical Specification for MT103Page.java
public class MT103Page implements Page {
    
    // Mandatory fields
    private final SendersReference sendersReference;
    private final BankOperationCode bankOperationCode;
    private final ValueDateCurrencyAmount valueDateCurrencyAmount;
    private final PartyIdentificationField orderingCustomer;
    private final PartyIdentificationField beneficiaryCustomer;
    private final DetailsOfCharges detailsOfCharges;
    
    // Optional fields (Phase 2+)
    private final Optional<TimeIndication> timeIndication;
    private final Optional<SendingInstitution> sendingInstitution;
    private final Optional<OrderingInstitution> orderingInstitution;
    private final Optional<IntermediaryInstitution> intermediaryInstitution;
    private final Optional<RemittanceInformation> remittanceInformation;
    
    public MT103Page(
        SendersReference sendersReference,
        BankOperationCode bankOperationCode,
        ValueDateCurrencyAmount valueDateCurrencyAmount,
        PartyIdentificationField orderingCustomer,
        PartyIdentificationField beneficiaryCustomer,
        DetailsOfCharges detailsOfCharges,
        // Optional parameters with Optional.empty() defaults
        Optional<TimeIndication> timeIndication,
        Optional<SendingInstitution> sendingInstitution,
        Optional<OrderingInstitution> orderingInstitution,
        Optional<IntermediaryInstitution> intermediaryInstitution,
        Optional<RemittanceInformation> remittanceInformation
    ) {
        // Validation: all mandatory non-null
        this.sendersReference = Preconditions.checkNotNull(sendersReference);
        this.bankOperationCode = Preconditions.checkNotNull(bankOperationCode);
        // ... other mandatory fields
        
        // Optional fields
        this.timeIndication = timeIndication != null ? timeIndication : Optional.empty();
        // ... other optional fields
    }
    
    @Override
    public List<SwiftField> getFields() {
        // Return all fields in correct order
        List<SwiftField> fields = new ArrayList<>();
        fields.add(sendersReference);
        // Add fields in SWIFT order
        return fields;
    }
}
```

#### **MT103PageReader Class:**
```java
// Technical Specification for MT103PageReader.java
public class MT103PageReader extends PageReader<MT103Page> {
    
    private static final Set<String> MANDATORY_FIELDS = ImmutableSet.of(
        SendersReference.TAG,
        BankOperationCode.TAG, 
        ValueDateCurrencyAmount.TAG,
        "50", // Ordering Customer (various options)
        "59", // Beneficiary Customer (various options)
        DetailsOfCharges.TAG
    );
    
    // State machine configuration - following existing pattern
    private static final Set<String> START_FIELDS = ImmutableSet.of(SendersReference.TAG);
    private static final Map<String, Set<String>> FIELD_TRANSITIONS = ImmutableMap.of(
        SendersReference.TAG, ImmutableSet.of("13C", BankOperationCode.TAG),
        BankOperationCode.TAG, ImmutableSet.of("23E", "26T", "32A", "33B", "36"),
        // ... complete field transition map
    );
    
    @Override
    public MT103Page read() throws SwiftMessageParseException {
        // Follow exact same pattern as MT940PageReader
        Map<String, GeneralField> fieldMap = new HashMap<>();
        Set<String> nextValidFieldSet = START_FIELDS;
        
        while (fieldReader.hasNext()) {
            GeneralField currentField = fieldReader.readField();
            ensureValidField(currentField, nextValidFieldSet, fieldReader);
            
            fieldMap.put(currentField.getTag(), currentField);
            nextValidFieldSet = getNextValidFields(currentField.getTag(), nextValidFieldSet);
            
            // Check for message end
            if (isMessageEnd(currentField)) {
                break;
            }
        }
        
        return buildMT103Page(fieldMap);
    }
    
    private MT103Page buildMT103Page(Map<String, GeneralField> fieldMap) {
        // Validate mandatory fields present
        MANDATORY_FIELDS.forEach(tag -> Preconditions.checkArgument(
            fieldMap.containsKey(tag), "Mandatory field %s is missing", tag));
        
        // Parse all fields
        SendersReference sendersReference = SendersReference.of(fieldMap.get(SendersReference.TAG));
        // ... parse other mandatory fields
        
        // Parse optional fields
        Optional<TimeIndication> timeIndication = Optional.ofNullable(fieldMap.get("13C"))
            .map(TimeIndication::of);
        // ... parse other optional fields
        
        return new MT103Page(
            sendersReference, bankOperationCode, valueDateCurrencyAmount,
            orderingCustomer, beneficiaryCustomer, detailsOfCharges,
            timeIndication, sendingInstitution, orderingInstitution,
            intermediaryInstitution, remittanceInformation
        );
    }
}
```

## **Phase 2: Common Optional Fields - Technical Specs**

### **Step 2.1: High-Priority Optional Fields**

#### **1. TimeIndication (`:13C:`)**
```java
public class TimeIndication implements SwiftField {
    public static final String TAG = "13C";
    // Format: /8c/4!n1!x4!n - /Code/Time+Sign+Offset
    public static final String NOTATION = "/8c/4!n1!x4!n";
    private static final Set<String> VALID_CODES = ImmutableSet.of("TIME", "CLSTIME", "SETT");
    private static final SwiftNotation SWIFT_NOTATION = new SwiftNotation(NOTATION);
    
    private final String code;
    private final LocalTime time;
    private final ZoneOffset offset;
    
    public static TimeIndication of(GeneralField field) {
        List<String> values = SWIFT_NOTATION.parse(field.getContent());
        String code = values.get(0);
        String timeStr = values.get(1);
        String sign = values.get(2);
        String offsetStr = values.get(3);
        
        Preconditions.checkArgument(VALID_CODES.contains(code), "Invalid time code: %s", code);
        
        LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HHmm"));
        int offsetHours = Integer.parseInt(offsetStr.substring(0, 2));
        int offsetMinutes = Integer.parseInt(offsetStr.substring(2, 4));
        ZoneOffset offset = ZoneOffset.ofHoursMinutes(
            sign.equals("+") ? offsetHours : -offsetHours,
            sign.equals("+") ? offsetMinutes : -offsetMinutes
        );
        
        return new TimeIndication(code, time, offset);
    }
}
```

#### **2. BIC Institution Fields (`:51A:`, `:52A:`, etc.)**
```java
public class InstitutionIdentifier implements SwiftField {
    public static final String NOTATION_A = "8!a"; // BIC
    public static final String NOTATION_D = "//4!c/34x"; // D option
    public static final SwiftNotation SWIFT_NOTATION_A = new SwiftNotation(NOTATION_A);
    public static final SwiftNotation SWIFT_NOTATION_D = new SwiftNotation(NOTATION_D);
    
    public enum Option {
        A, B, C, D
    }
    
    private final Option option;
    private final String identifier;
    
    public static InstitutionIdentifier of(String tag, GeneralField field) {
        String content = field.getContent();
        if (content.matches("^[A-Z]{6}[A-Z0-9]{2}[A-Z0-9]{3}$")) {
            // Option A - BIC format
            SWIFT_NOTATION_A.parse(content);
            return new InstitutionIdentifier(Option.A, content);
        } else if (content.startsWith("//")) {
            // Option D format
            SWIFT_NOTATION_D.parse(content);
            return new InstitutionIdentifier(Option.D, content);
        } else {
            // Option B/C - address format
            return new InstitutionIdentifier(
                content.contains("\n") ? Option.C : Option.B, 
                content
            );
        }
    }
    
    public Optional<BIC> getBIC() {
        return option == Option.A ? Optional.of(BIC.of(identifier)) : Optional.empty();
    }
}
```

## **Phase 3: Advanced Features - Technical Specs**

### **Step 3.1: Complex Structured Fields**

#### **1. SenderToReceiverInformation (`:72:`)**
```java
public class SenderToReceiverInformation implements SwiftField {
    public static final String TAG = "72";
    // Structured format: /Code/Information
    public static final String NOTATION = "[/8c/70x]";  // Can repeat
    private static final SwiftNotation SWIFT_NOTATION = new SwiftNotation(NOTATION);
    
    private final List<StructuredInformation> informationList;
    
    public static class StructuredInformation {
        private final String code;
        private final String information;
        
        public static StructuredInformation of(String content) {
            if (content.startsWith("/")) {
                String[] parts = content.substring(1).split("/", 2);
                return new StructuredInformation(
                    parts[0], 
                    parts.length > 1 ? parts[1] : ""
                );
            } else {
                return new StructuredInformation("", content);
            }
        }
        
        public static final Set<String> VALID_CODES = ImmutableSet.of(
            "ACC", "BK", "CHR", "COM", "DEX", "FXR", "INS", "INT", 
            "INV", "REJ", "REC", "RER", "RFS", "RIR", "TSU"
        );
    }
    
    public static SenderToReceiverInformation of(GeneralField field) {
        String content = field.getContent();
        List<StructuredInformation> infoList = new ArrayList<>();
        
        // Parse structured format
        String[] parts = content.split("\\\\/");
        for (String part : parts) {
            if (!part.isEmpty()) {
                infoList.add(StructuredInformation.of("/" + part));
            }
        }
        
        return new SenderToReceiverInformation(infoList);
    }
}
```

## **Phase 4: Testing & Documentation - Technical Specs**

### **Step 4.1: Comprehensive Test Patterns**

#### **Field Test Template:**
```java
public class SendersReferenceTest {
    
    @Test
    public void of_WHEN_valid_field_SHOULD_parse_successfully() {
        // Given
        String content = "REFERENCE123456";
        GeneralField field = new GeneralField(SendersReference.TAG, content);
        
        // When
        SendersReference result = SendersReference.of(field);
        
        // Then
        assertThat(result.getTag()).isEqualTo(SendersReference.TAG);
        assertThat(result.getContent()).isEqualTo(content);
    }
    
    @Test 
    public void of_WHEN_invalid_tag_SHOULD_throw_exception() {
        // Given
        GeneralField field = new GeneralField("99", "CONTENT");
        
        // When & Then
        assertThatThrownBy(() -> SendersReference.of(field))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Expected tag 20, got 99");
    }
    
    @Test
    public void of_WHEN_content_too_long_SHOULD_throw_exception() {
        // Given - content longer than 16 chars
        String longContent = "12345678901234567"; // 17 chars
        GeneralField field = new GeneralField(SendersReference.TAG, longContent);
        
        // When & Then
        assertThatThrownBy(() -> SendersReference.of(field))
            .isInstanceOf(FieldNotationParseException.class);
    }
    
    @Test
    public void of_WHEN_multiple_values_SHOULD_parse_all() {
        // Parameterized test for various valid inputs
    }
}
```

#### **Integration Test Template:**
```java
public class MT103PageReaderTest {
    
    private static final String VALID_MT103_MESSAGE = ""
        + ":20:REFERENCE12345\n"
        + ":23B:CRED\n"
        + ":32A:231201EUR1234,56\n"
        + ":50:/12345678/ACCOUNT NAME\n"
        + ":59:/87654321/BENEFICIARY NAME\n"
        + ":71A:OUR\n";
        
    @Test
    public void read_WHEN_complete_message_SHOULD_parse_all_fields() {
        // Given
        MT103PageReader reader = new MT103PageReader(new StringReader(VALID_MT103_MESSAGE));
        
        // When
        MT103Page page = reader.read();
        
        // Then
        assertThat(page.getSendersReference().getContent()).isEqualTo("REFERENCE12345");
        assertThat(page.getBankOperationCode().getCode()).isEqualTo("CRED");
        // ... assert all mandatory fields
        assertThat(page.getTimeIndication()).isEmpty();
    }
    
    @Test
    public void read_WHEN_mandatory_field_missing_SHOULD_throw_exception() {
        // Test with missing :23B field
        String messageWithoutMandatory = ":20:REF\n:32A:231201EUR123,45\n...";
        
        MT103PageReader reader = new MT103PageReader(new StringReader(messageWithoutMandatory));
        
        assertThatThrownBy(() -> reader.read())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Mandatory field 23B is missing");
    }
}
```

#### **Step 4.2: Performance Testing Requirements**

```java
public class MT103PerformanceTest {
    
    @Test
    public void read_WHEN_large_batch_SHOULD_meet_performance_requirements() {
        // Given - 1000 MT103 messages
        List<String> messages = generateTestMessages(1000);
        
        // When
        long startTime = System.currentTimeMillis();
        for (String message : messages) {
            MT103PageReader reader = new MT103PageReader(new StringReader(message));
            reader.read();
        }
        long endTime = System.currentTimeMillis();
        
        // Then - Should complete within reasonable time (e.g., < 5 seconds)
        assertThat(endTime - startTime).isLessThan(5000);
    }
}
```

## **Integration Specifications**

### **Message Flow Integration**
1. **SwiftMessageReader**: No changes needed
2. **ApplicationHeaderBlock**: Already extracts message type "103"
3. **TextBlock**: Passes content unchanged to MT103PageReader
4. **MT103PageReader**: Processes field content and creates MT103Page

### **Dependencies on Existing Code**
- `SwiftFieldReader` - Universal field parsing
- `SwiftNotation` - Format validation
- `GeneralField` - Field representation
- `PageReader<T>` - Base parsing class
- Exception hierarchy
- Test utilities (`TestUtils.collectUntilNull`)
- Joda Money for amount handling

### **Validation Requirements**
1. **Field-level validation** during parsing
2. **Cross-field validation** after parsing complete
3. **Business rule validation** as optional enhancement
4. **Line number tracking** for debugging

### **Error Handling Standards**
- Specific exception types for different validation failures
- Meaningful error messages with field context
- Graceful handling of missing optional fields
- Consistent error patterns with existing code

## **Implementation Order and Dependencies**

### **Phase 1 Dependencies**
1. Create package structure
2. Implement SendersReference (simplest)
3. Implement BankOperationCode (simple validation)
4. Implement ValueDateCurrencyAmount (most complex)
5. Implement PartyIdentificationField (abstract base)
6. Implement OrderingCustomer/BeneficiaryCustomer
7. Implement DetailsOfCharges
8. Create MT103Page and MT103PageReader
9. Add comprehensive tests

### **Phase 2 Dependencies**
1. Implement TimeIndication
2. Implement InstitutionIdentifier (reusable for multiple fields)
3. Implement RemittanceInformation
4. Update MT103Page to include optional fields
5. Update MT103PageReader state machine
6. Add integration tests

### **Phase 3 Dependencies**
1. Implement remaining optional fields
2. Add cross-field validation
3. Enhance error handling
4. Performance optimization

### **Phase 4 Dependencies**
1. Complete test coverage
2. Add performance tests
3. Create documentation
4. Integration with existing README

This technical specification provides complete implementation guidance for all phases of the MT103 implementation.