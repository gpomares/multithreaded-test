package com.gpomares.multithreadedtest.validation;

import com.gpomares.multithreadedtest.control.ValidationError;

import java.util.List;

public class ColumnCountValidation implements Validation {
    private final String VALIDATION_CODE = "A1";
    private final List<String> FILE_TYPES = List.of("SLB", "FCDB");

    @Override
    public boolean isApplicable(String fileType) {
        return FILE_TYPES.contains(fileType);
    }

    @Override
    public List<ValidationError> validate(String line, int lineNumber, String fileName) {
        String[] columns = line.split(";");
        if (columns.length != 5) {
            return List.of(new ValidationError(fileName, lineNumber, "Invalid number of columns", VALIDATION_CODE));
        }
        return List.of();
    }

    @Override
    public String getValidationCode() {
        return VALIDATION_CODE;
    }
}
