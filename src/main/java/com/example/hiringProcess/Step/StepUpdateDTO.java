package com.example.hiringProcess.Step;

/**
 * DTO για ενημέρωση ενός Step.
 * Χρησιμοποιείται σε PATCH/UPDATE requests.
 */
public record StepUpdateDTO(
        String description  // optional
) {}
