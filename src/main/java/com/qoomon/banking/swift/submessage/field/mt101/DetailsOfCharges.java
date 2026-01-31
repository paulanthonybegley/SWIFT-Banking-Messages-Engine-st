package com.qoomon.banking.swift.submessage.field.mt101;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qoomon.banking.swift.notation.FieldNotationParseException;
import com.qoomon.banking.swift.notation.SwiftNotation;
import com.qoomon.banking.swift.submessage.field.GeneralField;
import com.qoomon.banking.swift.submessage.field.SwiftField;

import java.util.List;

/**
 * <b>Details of Charges</b>
 * <p>
 * <b>Field Tag</b> :71A:
 * <p>
 * <b>Format</b> 3!a
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 3!a    - Charge Code (OUR, BEN, SHA)
 * </pre>
 */
public class DetailsOfCharges implements SwiftField {

    public static final String FIELD_TAG_71A = "71A";

    public static final SwiftNotation SWIFT_NOTATION = new SwiftNotation("3!a");

    public enum ChargeCode {
        OUR("Our charges - sender pays"),
        BEN("Beneficiary charges - sender pays"),
        SHA("Shared charges - split");

        private final String description;

        ChargeCode(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private final ChargeCode chargeCode;

    public DetailsOfCharges(ChargeCode chargeCode) {
        Preconditions.checkArgument(chargeCode != null, "chargeCode can't be null");
        this.chargeCode = chargeCode;
    }

    public static DetailsOfCharges of(GeneralField field) throws FieldNotationParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_71A), "unexpected field tag '%s'", field.getTag());

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        String codeStr = subFields.get(0);
        ChargeCode code = parseChargeCode(codeStr);

        return new DetailsOfCharges(code);
    }

    private static ChargeCode parseChargeCode(String codeStr) {
        switch (codeStr) {
            case "OUR": return ChargeCode.OUR;
            case "BEN": return ChargeCode.BEN;
            case "SHA": return ChargeCode.SHA;
            default: throw new IllegalArgumentException("Unknown charge code: " + codeStr);
        }
    }

    @Override
    public String getTag() {
        return FIELD_TAG_71A;
    }

    @Override
    public String getContent() {
        try {
            return SWIFT_NOTATION.render(Lists.newArrayList(chargeCode.name()));
        } catch (FieldNotationParseException e) {
            throw new IllegalStateException("Invalid field values within " + getClass().getSimpleName() + " instance", e);
        }
    }

    public ChargeCode getChargeCode() {
        return chargeCode;
    }
}