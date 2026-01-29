package com.qoomon.banking.swift.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for SWIFT message validation functionality.
 */
@Controller
public class ValidatorController {

    @GetMapping("/validator")
    public String validator(Model model) {
        model.addAttribute("currentPage", "validator");
        return "validator";
    }

    @PostMapping("/validator")
    public String validateMessage(@RequestParam(required = false) String message, Model model) {
        model.addAttribute("currentPage", "validator");

        if (message == null || message.trim().isEmpty()) {
            model.addAttribute("error", "Please provide a SWIFT message to validate.");
            return "validator";
        }

        ValidationResult result = validateSwiftMessage(message);
        model.addAttribute("result", result);
        model.addAttribute("message", message);

        return "validator";
    }

    private ValidationResult validateSwiftMessage(String message) {
        ValidationResult result = new ValidationResult();

        // Basic validation checks
        result.setValid(true);
        result.setMessageType(extractMessageType(message));
        result.setFieldCount(countFields(message));

        // Validate required fields for different message types
        if (result.getMessageType().equals("MT940")) {
            validateMT940(message, result);
        } else if (result.getMessageType().equals("MT942")) {
            validateMT942(message, result);
        } else if (result.getMessageType().equals("MT101")) {
            validateMT101(message, result);
        } else if (result.getMessageType().equals("MT103")) {
            validateMT103(message, result);
        }

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

    private int countFields(String message) {
        return message.split(":").length - 1;
    }

    private void validateMT940(String message, ValidationResult result) {
        if (!message.contains(":20:")) {
            result.setValid(false);
            result.addError("Missing required field :20: (Transaction Reference)");
        }
        if (!message.contains(":25:")) {
            result.setValid(false);
            result.addError("Missing required field :25: (Account Identification)");
        }
        if (!message.contains(":60F:") && !message.contains(":60M:")) {
            result.setValid(false);
            result.addError("Missing required field :60F: or :60M: (Opening Balance)");
        }
        if (!message.contains(":62F:") && !message.contains(":62M:")) {
            result.setValid(false);
            result.addError("Missing required field :62F: or :62M: (Closing Balance)");
        }
    }

    private void validateMT942(String message, ValidationResult result) {
        validateMT940(message, result); // Similar validation as MT940
    }

    private void validateMT101(String message, ValidationResult result) {
        if (!message.contains(":20:")) {
            result.setValid(false);
            result.addError("Missing required field :20: (Transaction Reference)");
        }
    }

    private void validateMT103(String message, ValidationResult result) {
        if (!message.contains(":20:")) {
            result.setValid(false);
            result.addError("Missing required field :20: (Transaction Reference)");
        }
        if (!message.contains(":23B:")) {
            result.setValid(false);
            result.addError("Missing required field :23B: (Bank Operation Code)");
        }
        if (!message.contains(":32A:")) {
            result.setValid(false);
            result.addError("Missing required field :32A: (Value Date/Currency/Amount)");
        }
        if (!message.contains(":50K:") && !message.contains(":50A:") && !message.contains(":50F:")) {
            result.setValid(false);
            result.addError("Missing required field :50K/A/F: (Ordering Customer)");
        }
        if (!message.contains(":59:")) {
            result.setValid(false);
            result.addError("Missing required field :59: (Beneficiary Customer)");
        }
        if (!message.contains(":71A:")) {
            result.setValid(false);
            result.addError("Missing required field :71A: (Details of Charges)");
        }
    }

    public static class ValidationResult {
        private boolean valid;
        private String messageType;
        private int fieldCount;
        private java.util.List<String> errors = new java.util.ArrayList<>();

        // Getters and setters
        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

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

        public java.util.List<String> getErrors() {
            return errors;
        }

        public void addError(String error) {
            this.errors.add(error);
        }
    }
}