# MT940 Parser Field Validation Investigation

## 🔍 Issue Summary
The parser accepts **malformed field tags** like `:2:` instead of properly formatted tags like `:20:`.

## 📋 Test Message Provided
```swift
{1:F01BANKDEFFXXXX0000000000}{2:I940BANKDEFFXXXXN}{3:{108:TEST123456789}}{4:
:2:REFERENCE123
-}
```

## ❌ Current Parser Behavior

### Problem Code
```java
private ParsedField parseField(String line) {
    // Parse field in format :TAG:VALUE
    int colonIndex = line.indexOf(':', 1); // Skip first colon
    if (colonIndex == -1)
        return null;

    String tag = line.substring(0, colonIndex + 1);
    String value = line.substring(colonIndex + 1);
    
    // NO VALIDATION OF TAG FORMAT!
    ParsedField field = new ParsedField();
    field.setTag(tag);
    field.setValue(value);
    field.setName(getFieldName(tag));
    return field;
}
```

### What Happens
✅ **ACCEPTS** `:2:REFERENCE123` (incorrect!)
- Extracts tag: `:2:`
- Extracts value: `REFERENCE123`
- No validation that tag should be 2-3 digits

## 🚨 The Problem

**SWIFT field tags must follow a specific format:**
- Start with `:`
- Followed by **2-3 digits** (e.g., `20`, `940`, `60F`)
- Optionally followed by a **letter** (e.g., `60F`, `62M`)
- End with `:`

**Examples:**
- ✅ Valid: `:20:`, `:25:`, `:60F:`, `:62M:`, `:940:`
- ❌ Invalid: `:2:`, `:1:`, `:ABC:`, `:9999:`

## 📊 Impact

**Severity: MEDIUM** 🟡

### Why This Matters
1. **Data Quality**: Malformed messages are parsed as valid
2. **User Confusion**: Users might think invalid field tags are acceptable
3. **Downstream Issues**: Invalid data could cause problems in processing
4. **Consistency**: Validator rejects these, but parser accepts them

## 💡 Recommended Fix

Add field tag validation using a regex pattern:

```java
private ParsedField parseField(String line) {
    // Parse field in format :TAG:VALUE
    int colonIndex = line.indexOf(':', 1); // Skip first colon
    if (colonIndex == -1)
        return null;

    String tag = line.substring(0, colonIndex + 1);
    String value = line.substring(colonIndex + 1);
    
    // Validate SWIFT field tag format: :NN: or :NNN: or :NNL: (where N=digit, L=letter)
    if (!isValidSwiftFieldTag(tag)) {
        return null; // Skip invalid field tags
    }

    ParsedField field = new ParsedField();
    field.setTag(tag);
    field.setValue(value);
    field.setName(getFieldName(tag));
    return field;
}

private boolean isValidSwiftFieldTag(String tag) {
    // SWIFT field tags: :NN: or :NNN: or :NNL: (2-3 digits, optional letter)
    return tag.matches("^:\\d{2,3}[A-Z]?:$");
}
```

## 📝 Expected Behavior After Fix

With the test message:
```
:2:REFERENCE123
```

**Should:**
- ❌ Reject the field (tag doesn't match pattern)
- Field count: 0
- No fields displayed

With valid message:
```
:20:REFERENCE123
```

**Should:**
- ✅ Accept the field
- Tag: `:20:`
- Value: `REFERENCE123`
- Name: `Transaction Reference`

## 🔗 References
- SWIFT field tag format specification
- Standard SWIFT message structure (2-3 digit tags with optional letter suffix)

---
*Investigation completed: 2026-01-31*
*Status: Ready for implementation*
