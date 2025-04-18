package com.gpomares.multithreadedtest.validation;

import com.gpomares.multithreadedtest.control.ValidationError;

import java.util.List;

public interface Validation {
    boolean isApplicable(String fileType);
    List<ValidationError> validate(String line, int lineNumber, String fileName);
    String getValidationCode();
}
