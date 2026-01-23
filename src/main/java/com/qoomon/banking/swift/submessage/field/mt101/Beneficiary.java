package com.qoomon.banking.swift.submessage.field.mt101;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qoomon.banking.swift.notation.FieldNotationParseException;
import com.qoomon.banking.swift.notation.SwiftNotation;
import com.qoomon.banking.swift.submessage.field.GeneralField;
import com.qoomon.banking.swift.submessage.field.SwiftField;

import java.util.List;

/**
 * <b>Beneficiary</b>
 * <p>
 * <b>Field Tag</b> :59:
 * <p>
 * <b>Format</b> /34x4*35x OR A
 * <p>
 * <b>SubFields</b>
 * <pre>
 * Option NO_OPTION: /34x4*35x
 * Option A: [/34x]4!a2!a2!c[3!c]
 * </pre>
 */
public class Beneficiary implements SwiftField {

    public static final String FIELD_TAG_59 = "59";

    public static final SwiftNotation SWIFT_NOTATION_OPTION_WITH_ACCOUNT = new SwiftNotation("/34x4*35x");
    public static final SwiftNotation SWIFT_NOTATION_OPTION_A = new SwiftNotation("[/34x]4!a2!a2!c[3!c]");

    public enum Option { NO_OPTION, OPTION_A }

    private final Option option;
    private final String account;
    private final List<String> nameAndAddress;
    private final String identifierCode;  // For OPTION_A

    public Beneficiary(String account, List<String> nameAndAddress) {
        Preconditions.checkArgument(account != null, "account can't be null");
        Preconditions.checkArgument(nameAndAddress != null, "nameAndAddress can't be null");

        this.option = Option.NO_OPTION;
        this.account = account;
        this.nameAndAddress = nameAndAddress;
        this.identifierCode = null;
    }

    public Beneficiary(String identifierCode, String account) {
        Preconditions.checkArgument(identifierCode != null, "identifierCode can't be null");
        Preconditions.checkArgument(account != null, "account can't be null");

        this.option = Option.OPTION_A;
        this.account = account;
        this.nameAndAddress = Lists.newArrayList();
        this.identifierCode = identifierCode;
    }

    public static Beneficiary of(GeneralField field) throws FieldNotationParseException {
        Preconditions.checkArgument(field.getTag().equals(FIELD_TAG_59), "unexpected field tag '%s'", field.getTag());

        String content = field.getContent();
        
        // Split content into lines for proper handling
        String[] lines = content.split("\n");
        String firstLine = lines[0];
        
        // Check if OPTION_A (BIC code followed by account on next line)
        if (firstLine.matches("^\\d{4}[A-Z]{6}[A-Z0-9]{3}$")) {
            // OPTION_A format: BIC on first line, account on subsequent lines
            String bic = firstLine;
            String account = lines.length > 1 ? lines[1] : "";
            return new Beneficiary(bic, account);
        } else if (firstLine.startsWith("/")) {
            // NO_OPTION format: account identifier on first line, name/address on subsequent lines
            String account = firstLine;
            List<String> nameAndAddress = Lists.newArrayList();
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (!line.isEmpty()) {
                    nameAndAddress.add(line);
                }
            }
            return new Beneficiary(account, nameAndAddress);
        } else {
            // Fallback to original parsing for single-line format
            List<String> subFields = SWIFT_NOTATION_OPTION_WITH_ACCOUNT.parse(content);
            String acc = subFields.get(0); // /34x or empty
            String name = subFields.size() > 1 ? subFields.get(1) : "";
            String address = subFields.size() > 2 ? subFields.get(2) : "";
            String address2 = subFields.size() > 3 ? subFields.get(3) : "";
            String address3 = subFields.size() > 4 ? subFields.get(4) : "";
            return new Beneficiary(acc, Lists.newArrayList(name, address, address2, address3));
        }
    }

    @Override
    public String getTag() {
        return FIELD_TAG_59;
    }

    @Override
    public String getContent() {
        try {
            if (option == Option.OPTION_A) {
                return SWIFT_NOTATION_OPTION_A.render(Lists.newArrayList(identifierCode, account));
            } else {
                List<String> parts = Lists.newArrayList(account);
                parts.addAll(nameAndAddress);
                return SWIFT_NOTATION_OPTION_WITH_ACCOUNT.render(parts);
            }
        } catch (FieldNotationParseException e) {
            throw new IllegalStateException("Invalid field values within " + getClass().getSimpleName() + " instance", e);
        }
    }

    public Option getOption() {
        return option;
    }

    public String getAccount() {
        return account;
    }

    public List<String> getNameAndAddress() {
        return nameAndAddress;
    }

    public String getIdentifierCode() {
        return identifierCode;
    }
}