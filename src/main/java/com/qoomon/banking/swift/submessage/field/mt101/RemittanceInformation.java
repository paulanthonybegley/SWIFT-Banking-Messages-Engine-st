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
 * <b>Remittance Information</b>
 * <p>
 * <b>Field Tag</b> :70:
 * <p>
 * <b>Format</b> 4*35x
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 4*35x  - Remittance Information Lines
 * </pre>
 */
public class RemittanceInformation implements SwiftField {

    public static final String FIELD_TAG_70 = "70";

    public static final SwiftNotation SWIFT_NOTATION = new SwiftNotation("4*35x");

    private final List<String> informationLines;

    public RemittanceInformation(List<String> informationLines) {

        Preconditions.checkArgument(informationLines != null, "informationLines can't be null");
        Preconditions.checkArgument(!informationLines.isEmpty(), "informationLines can't be empty");

        this.informationLines = informationLines;
    }

    public static RemittanceInformation of(GeneralField field) throws FieldNotationParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_70), "unexpected field tag '%s'", field.getTag());

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        return new RemittanceInformation(subFields);
    }

    @Override
    public String getTag() {
        return FIELD_TAG_70;
    }

    @Override
    public String getContent() {
        try {
            return SWIFT_NOTATION.render(informationLines);
        } catch (FieldNotationParseException e) {
            throw new IllegalStateException("Invalid field values within " + getClass().getSimpleName() + " instance", e);
        }
    }

    public List<String> getInformationLines() {
        return informationLines;
    }
}