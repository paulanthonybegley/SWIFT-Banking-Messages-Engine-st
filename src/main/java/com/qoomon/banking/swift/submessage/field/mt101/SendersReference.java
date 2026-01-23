package com.qoomon.banking.swift.submessage.field.mt101;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qoomon.banking.swift.notation.FieldNotationParseException;
import com.qoomon.banking.swift.notation.SwiftNotation;
import com.qoomon.banking.swift.submessage.field.GeneralField;
import com.qoomon.banking.swift.submessage.field.SwiftField;

import java.util.List;

/**
 * <b>Sender's Reference</b>
 * <p>
 * <b>Field Tag</b> :20:
 * <p>
 * <b>Format</b> 16x
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 16x    - Sender's Reference
 * </pre>
 */
public class SendersReference implements SwiftField {

    public static final String FIELD_TAG_20 = "20";

    public static final SwiftNotation SWIFT_NOTATION = new SwiftNotation("16x");

    private final String content;

    public SendersReference(String content) {

        Preconditions.checkArgument(content != null, "content can't be null");

        this.content = content;
    }

    public static SendersReference of(GeneralField field) throws FieldNotationParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_20), "unexpected field tag '%s'", field.getTag());

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        String value = subFields.get(0);

        return new SendersReference(value);
    }

    @Override
    public String getTag() {
        return FIELD_TAG_20;
    }

    @Override
    public String getContent() {
        try {
            return SWIFT_NOTATION.render(Lists.newArrayList(content));
        } catch (FieldNotationParseException e) {
            throw new IllegalStateException("Invalid field values within " + getClass().getSimpleName() + " instance", e);
        }
    }


}