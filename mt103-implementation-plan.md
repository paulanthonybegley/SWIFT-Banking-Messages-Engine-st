# SWIFT MT103 Implementation Plan

## **Overview**

This document outlines the comprehensive plan for implementing SWIFT MT103 (Single Customer Credit Transfer) support in the banking-swift-messages-java library.

## **Phase 1: Core Infrastructure (Week 1-2)**

### **Step 1.1: Package Structure Setup**
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

### **Step 1.2: Mandatory Field Implementation**
**Priority Order:**
1. **SendersReference** (`:20:`) - Reuses existing TransactionReference pattern
2. **BankOperationCode** (`:23B:`) - Simple 4-character code validation
3. **ValueDateCurrencyAmount** (`:32A:`) - Most complex, combines date/currency/amount
4. **DetailsOfCharges** (`:71A:`) - Simple 3-character validation (OUR/BEN/SHA)
5. **OrderingCustomer** (`:50a:`) - Multi-format field (A/F/K options)
6. **BeneficiaryCustomer** (`:59a:`) - Similar to existing MT101 Beneficiary

### **Step 1.3: Core Page Structure**
- **MT103Page** class implementing `Page` interface
- **MT103PageReader** extending `PageReader<MT103Page>`
- Basic state machine with field validation
- Constructor with mandatory fields only initially

## **Phase 2: Common Optional Fields (Week 3-4)**

### **Step 2.1: High-Priority Optional Fields**
1. **TimeIndication** (`:13C:`) - Time and UTC offset
2. **SendingInstitution** (`:51A:`) - BIC format validation
3. **OrderingInstitution** (`:52a:`) - Multiple format options
4. **IntermediaryInstitution** (`:56a:`) - Common in international payments
5. **RemittanceInformation** (`:70:`) - Multi-line text fields

### **Step 2.2: Party Identification Enhancement**
- Enhance existing BIC validation if needed
- Add support for address fields with multiple lines
- Implement party option variants (A/B/C/D/F/K)

### **Step 2.3: Integration Testing**
- Add optional field support to MT103Page
- Update parser state machine
- Create comprehensive test scenarios

## **Phase 3: Advanced Features (Week 5-6)**

### **Step 3.1: Remaining Optional Fields**
1. **InstructionCode** (`:23E:`) - Complex structured field
2. **TransactionTypeCode** (`:26T:`) - Regulatory codes
3. **CurrencyInstructedAmount** (`:33B:`) - Alternative amount field
4. **ExchangeRate** (`:36:`) - Currency conversion
5. **SenderToReceiverInfo** (`:72:`) - Structured communication
6. **Other correspondent fields** (`:53a:`, `:54a:`, `:55a:`, `:57a:`)

### **Step 3.2: Validation Framework**
- Cross-field validation (currency consistency)
- Business rule validation (amount relationships)
- Enhanced error messages with context

## **Phase 4: Testing & Documentation (Week 7-8)**

### **Step 4.1: Comprehensive Test Coverage**
- Unit tests for each field type
- Integration tests for complete message parsing
- Real-world MT103 message file tests
- Exception handling and edge cases
- Performance tests for large message batches

### **Step 4.2: Documentation & Examples**
- Javadoc for all public APIs
- Usage examples in test classes
- Integration with existing README
- Add MT103 to supported message types

## **Technical Implementation Details**

### **Key Challenges & Solutions**

#### **1. Complex Field: ValueDateCurrencyAmount (`:32A:`)**
```java
// Format: YYMMDDCCCAMOUNT
// Example: 231201EUR1234,56
public class ValueDateCurrencyAmount implements SwiftField {
    private static final String NOTATION = "6!n3!a15d"; // Date + Currency + Amount
    private static final SwiftNotation SWIFT_NOTATION = new SwiftNotation(NOTATION);
}
```

#### **2. Multi-Format Fields: OrderingCustomer (`:50a:`)**
```java
// Option A: Account + Name
// Option F: Address + Name  
// Option K: Beneficiary statement
public class OrderingCustomer implements SwiftField {
    public static OrderingCustomer of(GeneralField field) {
        String content = field.getContent();
        if (content.matches("^/")) {
            return ofOptionA(content);
        } else if (matchesOptionF(content)) {
            return ofOptionF(content);
        } else {
            return ofOptionK(content);
        }
    }
}
```

#### **3. Message Integration**
Following existing pattern exactly:
- `SwiftMessageReader` remains unchanged
- `TextBlock` passes content to `MT103PageReader`
- Message type "103" detected in `ApplicationHeaderBlock`
- No routing logic changes needed

### **Validation Strategy**
1. **Field-level validation** during parsing
2. **Cross-field validation** after parsing complete
3. **Business rule validation** as optional enhancement
4. **Line number tracking** for debugging

### **Error Handling**
- Specific exception types for different validation failures
- Meaningful error messages with field context
- Graceful handling of missing optional fields
- Consistent error patterns with existing code

## **Dependencies & Resources**

### **Existing Components to Reuse**
- `SwiftFieldReader` - Universal field parsing
- `SwiftNotation` - Format validation
- `GeneralField` - Field representation
- `PageReader<T>` - Base parsing class
- Exception hierarchy
- Test utilities (`TestUtils.collectUntilNull`)
- Joda Money for amount handling

### **External Resources Needed**
- Real MT103 message samples for testing
- SWIFT documentation for field format validation
- BIC code validation rules

## **Success Criteria**
1. ✅ All mandatory MT103 fields implemented
2. ✅ Common optional fields supported  
3. ✅ Tests pass with 90%+ coverage
4. ✅ Integration with existing message flow
5. ✅ Performance comparable to MT940/MT942
6. ✅ Documentation complete with examples

## **Risk Mitigation**
- **Complex field formats**: Start with simple implementations, enhance iteratively
- **Message routing**: Follow existing pattern exactly to avoid breaking changes
- **Test coverage**: Parallel test development with feature development
- **Performance**: Monitor against existing MT type benchmarks

## **Implementation Timeline**
- **Week 1-2**: Phase 1 - Core infrastructure and mandatory fields
- **Week 3-4**: Phase 2 - Common optional fields and integration
- **Week 5-6**: Phase 3 - Advanced features and validation
- **Week 7-8**: Phase 4 - Testing, documentation, and optimization

This plan provides a structured, phased approach that leverages existing patterns while systematically adding MT103 support.