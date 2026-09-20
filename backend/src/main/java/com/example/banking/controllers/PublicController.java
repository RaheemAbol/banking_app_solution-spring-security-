package com.example.banking.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class PublicController {
    @GetMapping("/info")
    public String info() {
        return "Branch support: Monday-Friday, 9 AM-5 PM. All amounts in this application are fictional USD.";
    }
}
