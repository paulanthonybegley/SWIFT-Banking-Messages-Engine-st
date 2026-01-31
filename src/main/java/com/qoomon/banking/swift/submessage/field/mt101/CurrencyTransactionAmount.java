package com.qoomon.banking.swift.submessage.field.mt101;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qoomon.banking.swift.notation.FieldNotationParseException;
import com.qoomon.banking.swift.notation.SwiftNotation;
import com.qoomon.banking.swift.submessage.field.GeneralField;
import com.qoomon.banking.swift.submessage.field.SwiftField;

import java.util.List;

/**
 * <b>Currency/Transaction Amount</b>
 * <p>
 * <b>Field Tag</b> :32B:
 * <p>
 * <b>Format</b> 3!a15d
 * <p>
 * <b>SubFields</b>
 * <pre>
 * 1: 3!a    - Currency Code
 * 2: 15d    - Amount
 * </pre>
 */
public class CurrencyTransactionAmount implements SwiftField {

    public static final String FIELD_TAG_32B = "32B";

    public static final SwiftNotation SWIFT_NOTATION = new SwiftNotation("3!a15d");

    private final String currency;
    private final String amount;

    public CurrencyTransactionAmount(String currency, String amount) {

        Preconditions.checkArgument(currency != null, "currency can't be null");
        Preconditions.checkArgument(amount != null, "amount can't be null");

        this.currency = currency;
        this.amount = amount;
    }

    public static CurrencyTransactionAmount of(GeneralField field) throws FieldNotationParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_32B), "unexpected field tag '%s'", field.getTag());

        List<String> subFields = SWIFT_NOTATION.parse(field.getContent());

        String currencyCode = subFields.get(0);
        String amountValue = subFields.get(1);

        return new CurrencyTransactionAmount(currencyCode, amountValue);
    }

    @Override
    public String getTag() {
        return FIELD_TAG_32B;
    }

    @Override
    public String getContent() {
        try {
            return SWIFT_NOTATION.render(Lists.newArrayList(currency, amount));
        } catch (FieldNotationParseException e) {
            throw new IllegalStateException("Invalid field values within " + getClass().getSimpleName() + " instance", e);
        }
    }

    public String getCurrency() {
        return currency;
    }

    public String getAmount() {
        return amount;
    }

}