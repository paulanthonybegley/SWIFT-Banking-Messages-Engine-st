package com.qoomon.banking.swift.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for SWIFT message composer functionality.
 */
@Controller
public class ComposerController {

    @GetMapping("/composer")
    public String composer(Model model) {
        model.addAttribute("currentPage", "composer");
        return "composer";
    }

    @PostMapping("/composer")
    public String composeMessage(
            @RequestParam String messageType,
            @RequestParam String transactionReference,
            @RequestParam String accountIdentification,
            @RequestParam String openingBalance,
            @RequestParam String transactionDetails,
            Model model) {

        model.addAttribute("currentPage", "composer");

        try {
            String composedMessage = buildSwiftMessage(messageType, transactionReference,
                    accountIdentification, openingBalance, transactionDetails);

            model.addAttribute("result", composedMessage);
            model.addAttribute("success", "Message composed successfully!");

        } catch (Exception e) {
            model.addAttribute("error", "Failed to compose message: " + e.getMessage());
        }

        return "composer";
    }

    private String buildSwiftMessage(String messageType, String ref, String account,
            String balance, String details) {
        StringBuilder message = new StringBuilder();
        message.append("{1:F01BANKDEFFXXXX0000000000}");
        message.append("{2:I").append(messageType.substring(2)).append("BANKDEFFXXXXN}");
        message.append("{3:{108:").append(ref).append("}}");

        switch (messageType) {
            case "MT940":
                message.append("{4:\n");
                message.append(":20:").append(ref).append("\n");
                message.append(":25:").append(account).append("\n");
                message.append(":28C:0/1\n");
                message.append(":60F:").append(balance).append("\n");
                if (!details.isEmpty()) {
                    message.append(":61:").append(details).append("\n");
                }
                message.append(":62F:C").append(balance).append("\n");
                message.append("}");
                break;

            case "MT101":
                message.append("{4:\n");
                message.append(":20:").append(ref).append("\n");
                message.append(":23:E\n");
                message.append(":32A:").append(balance).append("\n");
                if (!details.isEmpty()) {
                    message.append(":50K:").append(details).append("\n");
                }
                message.append("}");
                break;

            case "MT103":
                message.append("{4:\n");
                message.append(":20:").append(ref).append("\n");
                message.append(":23B:CRED\n");
                message.append(":32A:").append(balance).append("\n");
                message.append(":50K:").append(account).append("\n");
                if (!details.isEmpty()) {
                    message.append(":59:").append(details).append("\n");
                }
                message.append(":71A:SHA\n");
                message.append("}");
                break;

            case "MT104":
                message.append("{4:\n");
                // Sequence A - General Information
                message.append(":20:").append(ref).append("\n");
                message.append(":30G:").append(details.split("/")[0]).append("\n"); // Assume format Date/Ref in details
                                                                                    // for simplicity

                // Sequence B - Individual Transaction (Simple version for demo)
                message.append(":21:").append(ref).append("-TR\n");
                message.append(":32B:").append(balance).append("\n"); // Assume balance input is ISO Currency + Amount
                message.append(":50K:").append(account).append("\n");

                // Sequence C - Settlement Details
                message.append(":19:").append(balance.replaceAll("[^0-9,.]", "")).append("\n");
                message.append(":30:").append(details.split("/")[0]).append("\n");
                message.append("}");
                break;

            default:
                throw new IllegalArgumentException("Unsupported message type: " + messageType);
        }

        return message.toString();
    }
}