package com.qoomon.banking.swift.submessage.field.mt101;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qoomon.banking.swift.notation.FieldNotationParseException;
import com.qoomon.banking.swift.notation.SwiftNotation;
import com.qoomon.banking.swift.submessage.field.GeneralField;
import com.qoomon.banking.swift.submessage.field.SwiftField;

import java.util.List;
import java.util.Optional;

/**
 * <b>Instruction Code</b>
 * <p>
 * <b>Field Tag</b> :23E:
 * <p>
 * <b>Format</b> 4!c[/30x]
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 4!c    - Instruction Code
 * 2: [/30x] - Additional Information
 * </pre>
 */
public class InstructionCode implements SwiftField {

    public static final String FIELD_TAG_23E = "23E";

    public static final SwiftNotation SWIFT_NOTATION = new SwiftNotation("4!c[/30x]");

    public enum Code {
        URGP("Urgent Payment"),
        INTC("Intra-company Payment"), 
        RTGS("Real Time Gross Settlement"),
        CORT("Financial Payment"),
        CHQB("Cheque"),
        DMST("Domestic Payment"),
        INTL("International Payment"),
        SDCL("Same Day Clearing"),
        US("Domestic Polish Tax Payment"),
        VAT53("Domestic Polish Split Payment"),
        BACS("BACS Payment UK"),
        ZUS("Domestic Polish Social Insurance"),
        OTHR("Other");

        private final String description;

        Code(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private final Code code;
    private final Optional<String> additionalInfo;

    public InstructionCode(Code code, String additionalInfo) {
        Preconditions.checkArgument(code != null, "code can't be null");
        this.code = code;
        this.additionalInfo = Optional.ofNullable(additionalInfo);
    }

    public static InstructionCode of(GeneralField field) throws FieldNotationParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_23E), "unexpected field tag '%s'", field.getTag());

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        String codeStr = subFields.get(0);
        Code instructionCode = parseCode(codeStr);
        String additionalInfo = subFields.size() > 1 ? subFields.get(1) : null;

        return new InstructionCode(instructionCode, additionalInfo);
    }

    private static Code parseCode(String codeStr) {
        switch (codeStr) {
            case "URGP": return Code.URGP;
            case "INTC": return Code.INTC;
            case "RTGS": return Code.RTGS;
            case "CORT": return Code.CORT;
            case "CHQB": return Code.CHQB;
            case "DMST": return Code.DMST;
            case "INTL": return Code.INTL;
            case "SDCL": return Code.SDCL;
            case "US": return Code.US;
            case "VAT53": return Code.VAT53;
            case "BACS": return Code.BACS;
            case "ZUS": return Code.ZUS;
            case "OTHR": return Code.OTHR;
            default: return Code.OTHR;
        }
    }

    @Override
    public String getTag() {
        return FIELD_TAG_23E;
    }

    @Override
    public String getContent() {
        try {
            return SWIFT_NOTATION.render(Lists.newArrayList(
                code.name(),
                additionalInfo.orElse(null)
            ));
        } catch (FieldNotationParseException e) {
            throw new IllegalStateException("Invalid field values within " + getClass().getSimpleName() + " instance", e);
        }
    }

    public Code getCode() {
        return code;
    }

    public Optional<String> getAdditionalInfo() {
        return additionalInfo;
    }
}