package com.qoomon.banking.swift.submessage.field.mt101;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qoomon.banking.swift.notation.FieldNotationParseException;
import com.qoomon.banking.swift.notation.SwiftNotation;
import com.qoomon.banking.swift.submessage.field.GeneralField;
import com.qoomon.banking.swift.submessage.field.SwiftField;

import java.util.List;

/**
 * <b>Requested Execution Date</b>
 * <p>
 * <b>Field Tag</b> :30:
 * <p>
 * <b>Format</b> 6!n
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 6!n    - Date (YYMMDD)
 * </pre>
 */
public class RequestedExecutionDate implements SwiftField {

    public static final String FIELD_TAG_30 = "30";

    public static final SwiftNotation SWIFT_NOTATION = new SwiftNotation("6!n");

    private final String date;

    public RequestedExecutionDate(String date) {

        Preconditions.checkArgument(date != null, "date can't be null");

        this.date = date;
    }

    public static RequestedExecutionDate of(GeneralField field) throws FieldNotationParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_30), "unexpected field tag '%s'", field.getTag());

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        String value = subFields.get(0);

        return new RequestedExecutionDate(value);
    }

    @Override
    public String getTag() {
        return FIELD_TAG_30;
    }

    @Override
    public String getContent() {
        try {
            return SWIFT_NOTATION.render(Lists.newArrayList(date));
        } catch (FieldNotationParseException e) {
            throw new IllegalStateException("Invalid field values within " + getClass().getSimpleName() + " instance", e);
        }
    }

    public String getDate() {
        return date;
    }

}