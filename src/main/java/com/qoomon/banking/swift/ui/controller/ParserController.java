package com.qoomon.banking.swift.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for handling SWIFT message parsing functionality.
 */
@Controller
public class ParserController {

    @GetMapping("/parser")
    public String parser(Model model) {
        model.addAttribute("currentPage", "parser");
        return "parser";
    }

    @PostMapping("/parser")
    public String parseMessage(@RequestParam(required = false) String message, Model model) {
        model.addAttribute("currentPage", "parser");

        if (message == null || message.trim().isEmpty()) {
            model.addAttribute("error", "Please provide a SWIFT message to parse.");
            return "parser";
        }

        try {
            ParseResult result = parseSwiftMessage(message);
            model.addAttribute("result", result);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to parse message: " + e.getMessage());
        }

        model.addAttribute("message", message);
        return "parser";
    }

    private ParseResult parseSwiftMessage(String message) {
        ParseResult result = new ParseResult();
        List<ParsedField> fields = new ArrayList<>();

        // Simple field parsing logic
        String[] lines = message.split("\\r?\\n");
        result.setMessageType(extractMessageType(message));

        for (String line : lines) {
            if (line.startsWith(":")) {
                ParsedField field = parseField(line);
                if (field != null) {
                    fields.add(field);
                }
            }
        }

        result.setFields(fields);
        result.setFieldCount(fields.size());
        result.setStatus("Valid");

        return result;
    }

    private String extractMessageType(String message) {
        if (message.contains("{2:I940") || message.contains("{2:O940") || message.contains(":940:"))
            return "MT940";
        if (message.contains("{2:I942") || message.contains("{2:O942") || message.contains(":942:"))
            return "MT942";
        if (message.contains("{2:I101") || message.contains("{2:O101") || message.contains(":101:"))
            return "MT101";
        if (message.contains("{2:I103") || message.contains("{2:O103") || message.contains(":103:"))
            return "MT103";
        return "Unknown";
    }

    private ParsedField parseField(String line) {
        // Parse field in format :TAG:VALUE
        int colonIndex = line.indexOf(':', 1); // Skip first colon
        if (colonIndex == -1)
            return null;

        String tag = line.substring(0, colonIndex + 1);
        String value = line.substring(colonIndex + 1);

        ParsedField field = new ParsedField();
        field.setTag(tag);
        field.setValue(value);
        field.setName(getFieldName(tag));

        return field;
    }

    private String getFieldName(String tag) {
        switch (tag) {
            case ":20:":
                return "Transaction Reference";
            case ":25:":
                return "Account Identification";
            case ":28C:":
                return "Statement Number";
            case ":60F:":
                return "Opening Balance";
            case ":61F:":
                return "Transaction";
            case ":62F:":
                return "Closing Balance";
            case ":64:":
                return "Closing Available Balance";
            case ":86:":
                return "Information to Account Owner";
            case ":23B:":
                return "Bank Operation Code";
            case ":32A:":
                return "Value Date/Currency/Amount";
            case ":33B:":
                return "Instructed Amount";
            case ":50K:":
                return "Ordering Customer";
            case ":59:":
                return "Beneficiary Customer";
            case ":71A:":
                return "Details of Charges";
            case ":70:":
                return "Remittance Information";
            default:
                return "Field " + tag;
        }
    }

    // Data transfer objects
    public static class ParseResult {
        private String messageType;
        private int fieldCount;
        private String status;
        private List<ParsedField> fields;

        // Getters and setters
        public String getMessageType() {
            return messageType;
        }

        public void setMessageType(String messageType) {
            this.messageType = messageType;
        }

        public int getFieldCount() {
            return fieldCount;
        }

        public void setFieldCount(int fieldCount) {
            this.fieldCount = fieldCount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<ParsedField> getFields() {
            return fields;
        }

        public void setFields(List<ParsedField> fields) {
            this.fields = fields;
        }
    }

    public static class ParsedField {
        private String tag;
        private String name;
        private String value;

        // Getters and setters
        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}