package com.gpomares.multithreadedtest.control;

import java.time.LocalDate;

public class Metadata {
    private String fileType;
    private LocalDate date;
    private String status;

    public Metadata(String fileType, LocalDate date, String status) {
        this.fileType = fileType;
        this.date = date;
        this.status = status;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
