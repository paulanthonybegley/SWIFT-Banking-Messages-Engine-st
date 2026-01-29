package com.qoomon.banking.swift.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for documentation page.
 */
@Controller
public class DocumentationController {

    @GetMapping("/documentation")
    public String documentation(Model model) {
        model.addAttribute("currentPage", "documentation");
        return "documentation";
    }
}