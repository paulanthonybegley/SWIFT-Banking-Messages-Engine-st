# SWIFT MT103 Field Implementation Specifications

## **Mandatory Fields - Phase 1**

### **1. SendersReference (`:20:`)**

**SWIFT Specification:**
- Tag: `:20:`
- Format: `16x` (16 characters, any)
- Purpose: Unique reference assigned by sender

**Java Implementation:**
```java
package com.qoomon.banking.swift.submessage.field.mt103;

public class SendersReference implements SwiftField {
    public static final String TAG = "20";
    public static final String NOTATION = "16x";
    private static final SwiftNotation SWIFT_NOTATION = new SwiftNotation(NOTATION);
    
    private final String reference;
    
    public SendersReference(String reference) {
        this.reference = Preconditions.checkNotNull(reference);
    }
    
    public static SendersReference of(GeneralField field) {
        Preconditions.checkArgument(field.getTag().equals(TAG), 
            "Expected tag %s, got %s", TAG, field.getTag());
        
        List<String> values = SWIFT_NOTATION.parse(field.getContent());
        return new SendersReference(values.get(0));
    }
    
    @Override public String getTag() { return TAG; }
    @Override public String getContent() { return reference; }
}
```

**Test Cases:**
- Valid: 16 characters or less
- Invalid: >16 characters, null, empty
- Edge: exactly 16 characters

---

### **2. BankOperationCode (`:23B:`)**

**SWIFT Specification:**
- Tag: `:23B:`
- Format: `4!c` (4 specific codes)
- Valid codes: CRED, SPAY, SPRI, SSTD
- Purpose: Type of operation

**Java Implementation:**
```java
public class BankOperationCode implements SwiftField {
    public static final String TAG = "23B";
    public static final String NOTATION = "4!c";
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

**Test Cases:**
- Valid: CRED, SPAY, SPRI, SSTD
- Invalid: CREDX, null, lowercase

---

### **3. ValueDateCurrencyAmount (`:32A:`)**

**SWIFT Specification:**
- Tag: `:32A:`
- Format: `6!n3!a15d` (YYMMDD + Currency + Amount)
- Example: `231201EUR1234,56`
- Purpose: Value date, currency, and interbank settled amount

**Java Implementation:**
```java
public class ValueDateCurrencyAmount implements SwiftField {
    public static final String TAG = "32A";
    public static final String NOTATION = "6!n3!a15d";
    private static final SwiftNotation SWIFT_NOTATION = new SwiftNotation(NOTATION);
    
    private final LocalDate valueDate;
    private final Currency currency; 
    private final Money amount;
    
    public static ValueDateCurrencyAmount of(GeneralField field) {
        List<String> values = SWIFT_NOTATION.parse(field.getContent());
        String dateStr = values.get(0);      // YYMMDD
        String currencyStr = values.get(1);  // 3-letter currency
        String amountStr = values.get(2);    // Decimal with comma
        
        LocalDate valueDate = parseYYMMDD(dateStr);
        Currency currency = Currency.getInstance(currencyStr);
        Money amount = parseSwiftAmount(amountStr, currency);
        
        return new ValueDateCurrencyAmount(valueDate, currency, amount);
    }
    
    private static LocalDate parseYYMMDD(String dateStr) {
        int year = Integer.parseInt(dateStr.substring(0, 2));
        year += (year < 50) ? 2000 : 1900; // Y2K handling
        int month = Integer.parseInt(dateStr.substring(2, 4));
        int day = Integer.parseInt(dateStr.substring(4, 6));
        return LocalDate.of(year, month, day);
    }
    
    private static Money parseSwiftAmount(String amountStr, Currency currency) {
        // Convert "1234,56" to "1234.56" for Joda Money
        String normalizedAmount = amountStr.replace(',', '.');
        return Money.of(BigDecimal.valueOf(Double.parseDouble(normalizedAmount)), currency);
    }
}
```

**Test Cases:**
- Valid: `231201EUR1234,56`, `991231USD0,01`
- Invalid: invalid dates, invalid currencies, malformed amounts
- Edge: Y2K dates (years < 50 vs >= 50)

---

### **4. DetailsOfCharges (`:71A:`)**

**SWIFT Specification:**
- Tag: `:71A:`
- Format: `3!c` (3 specific codes)
- Valid codes: OUR, BEN, SHA
- Purpose: Who pays charges

**Java Implementation:**
```java
public class DetailsOfCharges implements SwiftField {
    public static final String TAG = "71A";
    public static final String NOTATION = "3!c";
    private static final Set<String> VALID_CODES = ImmutableSet.of("OUR", "BEN", "SHA");
    private static final SwiftNotation SWIFT_NOTATION = new SwiftNotation(NOTATION);
    
    private final String code;
    
    public static DetailsOfCharges of(GeneralField field) {
        List<String> values = SWIFT_NOTATION.parse(field.getContent());
        String code = values.get(0);
        Preconditions.checkArgument(VALID_CODES.contains(code), 
            "Invalid charges code: %s. Valid codes: %s", code, VALID_CODES);
        return new DetailsOfCharges(code);
    }
}
```

**Test Cases:**
- Valid: OUR, BEN, SHA
- Invalid: OURX, null, other codes

---

## **Party Identification Fields - Phase 1**

### **5. PartyIdentificationField (Abstract Base)**

**SWIFT Specification:**
- Multiple options: A, F, K (and B, C, D for some fields)
- Option A: `/account/name`
- Option F: Multi-line address + name
- Option K: Free text statement

**Java Implementation:**
```java
public abstract class PartyIdentificationField implements SwiftField {
    
    // Option A: /account/name
    public static class OptionA extends PartyIdentificationField {
        private final String account;
        private final String name;
        
        public OptionA(String account, String name) {
            this.account = Preconditions.checkNotNull(account);
            this.name = Preconditions.checkNotNull(name);
        }
        
        public static PartyIdentificationField of(String content) {
            Preconditions.checkArgument(content.startsWith("/"), 
                "Option A must start with '/'");
            String[] parts = content.substring(1).split("/", 2);
            String account = parts[0];
            String name = parts.length > 1 ? parts[1] : "";
            return new OptionA(account, name);
        }
        
        public String getAccount() { return account; }
        public String getName() { return name; }
    }
    
    // Option F: address/name (multiple lines)
    public static class OptionF extends PartyIdentificationField {
        private final List<String> addressLines;
        private final String name;
        
        public OptionF(List<String> addressLines, String name) {
            this.addressLines = ImmutableList.copyOf(addressLines);
            this.name = Preconditions.checkNotNull(name);
        }
        
        public static PartyIdentificationField of(String content) {
            String[] lines = content.split("\n");
            String name = lines[lines.length - 1]; // Last line is name
            List<String> address = Arrays.asList(Arrays.copyOf(lines, lines.length - 1));
            return new OptionF(address, name);
        }
        
        public List<String> getAddressLines() { return addressLines; }
        public String getName() { return name; }
    }
    
    // Option K: Beneficiary statement
    public static class OptionK extends PartyIdentificationField {
        private final String statement;
        
        public OptionK(String statement) {
            this.statement = Preconditions.checkNotNull(statement);
        }
        
        public static PartyIdentificationField of(String content) {
            return new OptionK(content);
        }
        
        public String getStatement() { return statement; }
    }
    
    // Factory method for automatic option detection
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
    
    private static boolean isAddressPattern(String content) {
        // Heuristic: if it looks like an address (street, city, etc.)
        return content.matches(".*\\d+.*") && content.length() > 20;
    }
}
```

---

### **6. OrderingCustomer (`:50a:`)**

**SWIFT Specification:**
- Tag: `:50:`, `:50A:`, `:50F:`, `:50K:`
- Options: A, F, K
- Purpose: Who is sending the payment

**Java Implementation:**
```java
public class OrderingCustomer extends PartyIdentificationField {
    
    public static OrderingCustomer of(GeneralField field) {
        return (OrderingCustomer) PartyIdentificationField.of("50", field);
    }
    
    // Convenience methods for specific options
    public static OrderingCustomer ofOptionA(String account, String name) {
        return new OrderingCustomer(OptionA.of("/" + account + "/" + name));
    }
    
    public static OrderingCustomer ofOptionF(List<String> address, String name) {
        String content = String.join("\n", address) + "\n" + name;
        return new OrderingCustomer(OptionF.of(content));
    }
    
    public static OrderingCustomer ofOptionK(String statement) {
        return new OrderingCustomer(OptionK.of(statement));
    }
    
    // Type-safe getters
    public Optional<String> getAccount() {
        if (this instanceof OptionA) {
            return Optional.of(((OptionA) this).getAccount());
        }
        return Optional.empty();
    }
    
    public Optional<String> getName() {
        if (this instanceof OptionA) {
            return Optional.of(((OptionA) this).getName());
        } else if (this instanceof OptionF) {
            return Optional.of(((OptionF) this).getName());
        }
        return Optional.empty();
    }
}
```

---

### **7. BeneficiaryCustomer (`:59a:`)**

**SWIFT Specification:**
- Tag: `:59:`, `:59A:`, `:59F:`, `:59:`
- Options: A, F (no K option for beneficiary)
- Purpose: Who is receiving the payment

**Java Implementation:**
```java
public class BeneficiaryCustomer extends PartyIdentificationField {
    
    public static BeneficiaryCustomer of(GeneralField field) {
        String content = field.getContent();
        PartyIdentificationField base;
        
        if (content.startsWith("/")) {
            base = OptionA.of(content);
        } else if (content.contains("\n") || isAddressPattern(content)) {
            base = OptionF.of(content);
        } else {
            // For beneficiary, treat non-address as OptionA with no account
            base = OptionA.of("//" + content);
        }
        
        return new BeneficiaryCustomer(base);
    }
    
    private BeneficiaryCustomer(PartyIdentificationField base) {
        // Copy constructor pattern
        super(base.getTag(), base.getContent());
    }
}
```

---

## **Optional Fields - Phase 2**

### **8. TimeIndication (`:13C:`)**

**SWIFT Specification:**
- Tag: `:13C:`
- Format: `/8c/4!n1!x4!n`
- Structure: `/Code/Time+Sign+Offset`
- Valid codes: TIME, CLSTIME, SETT
- Example: `/TIME/1430+0500`

**Java Implementation:**
```java
public class TimeIndication implements SwiftField {
    public static final String TAG = "13C";
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
        
        Preconditions.checkArgument(VALID_CODES.contains(code), 
            "Invalid time code: %s", code);
        
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

---

### **9. InstitutionIdentifier (Reusable for Multiple Fields)**

**SWIFT Specification:**
- Used for: `:51A:`, `:52A:`, `:53A:`, `:54A:`, `:55A:`, `:56A:`, `:57A:`
- Option A: `8!a` (BIC format)
- Option D: `//4!c/34x` (structured format)
- Options B/C: Address format

**Java Implementation:**
```java
public class InstitutionIdentifier implements SwiftField {
    public static final String NOTATION_A = "8!a"; // BIC
    public static final String NOTATION_D = "//4!c/34x"; // D option
    private static final SwiftNotation SWIFT_NOTATION_A = new SwiftNotation(NOTATION_A);
    private static final SwiftNotation SWIFT_NOTATION_D = new SwiftNotation(NOTATION_D);
    
    public enum Option {
        A, B, C, D
    }
    
    private final Option option;
    private final String identifier;
    private final String tag;
    
    public static InstitutionIdentifier of(String tag, GeneralField field) {
        String content = field.getContent();
        Option option;
        String identifier;
        
        if (content.matches("^[A-Z]{6}[A-Z0-9]{2}[A-Z0-9]{3}$")) {
            // Option A - BIC format
            SWIFT_NOTATION_A.parse(content);
            option = Option.A;
            identifier = content;
        } else if (content.startsWith("//")) {
            // Option D format
            SWIFT_NOTATION_D.parse(content);
            option = Option.D;
            identifier = content;
        } else {
            // Option B/C - address format
            option = content.contains("\n") ? Option.C : Option.B;
            identifier = content;
        }
        
        return new InstitutionIdentifier(tag, option, identifier);
    }
    
    public Optional<BIC> getBIC() {
        return option == Option.A ? Optional.of(BIC.of(identifier)) : Optional.empty();
    }
    
    public Option getOption() { return option; }
    public String getIdentifier() { return identifier; }
    
    @Override public String getTag() { return tag; }
    @Override public String getContent() { return identifier; }
}
```

---

### **10. RemittanceInformation (`:70:`)**

**SWIFT Specification:**
- Tag: `:70:`
- Format: `4*35x` (up to 4 lines, 35 chars each)
- Purpose: Payment description, invoice numbers, etc.

**Java Implementation:**
```java
public class RemittanceInformation implements SwiftField {
    public static final String TAG = "70";
    public static final String NOTATION = "4*35x";
    private static final SwiftNotation SWIFT_NOTATION = new SwiftNotation(NOTATION);
    
    private final List<String> lines;
    
    public static RemittanceInformation of(GeneralField field) {
        List<String> values = SWIFT_NOTATION.parse(field.getContent());
        return new RemittanceInformation(values);
    }
    
    public RemittanceInformation(List<String> lines) {
        this.lines = ImmutableList.copyOf(lines);
    }
    
    public List<String> getLines() { return lines; }
    
    public String getSingleLine() {
        return String.join(" ", lines);
    }
}
```

---

## **Advanced Fields - Phase 3**

### **11. SenderToReceiverInformation (`:72:`)**

**SWIFT Specification:**
- Tag: `:72:`
- Format: `[/8c/70x]` (repeatable structured format)
- Structure: `/Code/Information`
- Valid codes: ACC, BK, CHR, COM, DEX, FXR, INS, INT, INV, REJ, REC, RER, RFS, RIR, TSU

**Java Implementation:**
```java
public class SenderToReceiverInformation implements SwiftField {
    public static final String TAG = "72";
    public static final String NOTATION = "[/8c/70x]";
    private static final SwiftNotation SWIFT_NOTATION = new SwiftNotation(NOTATION);
    
    private final List<StructuredInformation> informationList;
    
    public static class StructuredInformation {
        private final String code;
        private final String information;
        
        public static final Set<String> VALID_CODES = ImmutableSet.of(
            "ACC", "BK", "CHR", "COM", "DEX", "FXR", "INS", "INT", 
            "INV", "REJ", "REC", "RER", "RFS", "RIR", "TSU"
        );
        
        public StructuredInformation(String code, String information) {
            this.code = Preconditions.checkNotNull(code);
            this.information = Preconditions.checkNotNull(information);
        }
        
        public static StructuredInformation of(String content) {
            if (content.startsWith("/")) {
                String[] parts = content.substring(1).split("/", 2);
                String code = parts[0];
                String info = parts.length > 1 ? parts[1] : "";
                return new StructuredInformation(code, info);
            } else {
                return new StructuredInformation("", content);
            }
        }
        
        public String getCode() { return code; }
        public String getInformation() { return information; }
    }
    
    public static SenderToReceiverInformation of(GeneralField field) {
        String content = field.getContent();
        List<StructuredInformation> infoList = new ArrayList<>();
        
        // Parse structured format - handle multiple segments
        String[] segments = content.split("(?<=\\\\/)");
        for (String segment : segments) {
            if (!segment.trim().isEmpty()) {
                infoList.add(StructuredInformation.of(segment.trim()));
            }
        }
        
        return new SenderToReceiverInformation(infoList);
    }
}
```

---

## **Field Implementation Order**

### **Phase 1 - Core Fields (Implementation Order)**
1. **SendersReference** - Simplest, good starting point
2. **BankOperationCode** - Simple validation
3. **DetailsOfCharges** - Simple validation
4. **PartyIdentificationField** - Abstract base class
5. **OrderingCustomer** - Extends PartyIdentificationField
6. **BeneficiaryCustomer** - Extends PartyIdentificationField
7. **ValueDateCurrencyAmount** - Most complex, do last

### **Phase 2 - Common Optional Fields**
1. **TimeIndication** - Time parsing logic
2. **InstitutionIdentifier** - Reusable for multiple fields
3. **RemittanceInformation** - Multi-line text

### **Phase 3 - Advanced Fields**
1. **SenderToReceiverInformation** - Complex structured parsing
2. **Remaining optional fields** - Following established patterns

---

## **Test Data Examples**

### **Valid MT103 Message Example**
```
:20:REFERENCE12345
:13C:/TIME/1430+0500
:23B:CRED
:32A:231201EUR1234,56
:33B:EUR1234,56
:50A:/DE12345678901234567890/COMPANY NAME
:52A:BANKDEFFXXX
:56A:INTERMEDIARYXXX
:57A:BENEFICIARYBANKXXX
:59:/FR87654321098765432109/BENEFICIARY NAME
:70:INVOICE 12345
:71A:SHA
:72:/INV/INVOICE12345/COM/PAYMENT FOR GOODS
```

### **Test Cases for Each Field**
- Valid formats
- Invalid formats
- Edge cases (min/max lengths, special characters)
- Null/empty handling
- Cross-field validation

This specification provides complete implementation guidance for all MT103 fields with detailed Java code examples and comprehensive test requirements.