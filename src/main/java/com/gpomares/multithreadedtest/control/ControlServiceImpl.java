package com.gpomares.multithreadedtest.control;

import com.gpomares.multithreadedtest.validation.ColumnCountValidation;
import com.gpomares.multithreadedtest.validation.Validation;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ControlServiceImpl implements ControlService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final List<Validation> validationList = List.of(new ColumnCountValidation());


    @Override
    public List<ValidationError> doControl(MultipartFile file) throws IOException {
        List<ValidationError> allErrors = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
            List<CompletableFuture<List<ValidationError>>> futures = new ArrayList<>();
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".csv")) {
                    byte[] contents = zis.readAllBytes();
                    String fileName = entry.getName();

                    CompletableFuture<List<ValidationError>> future = CompletableFuture
                            .supplyAsync(() -> processCSV(fileName, contents), executorService);
                    futures.add(future);
                }
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            for (CompletableFuture<List<ValidationError>> future : futures) {
                allErrors.addAll(future.join());
            }
        }

        return allErrors;
    }

    private List<ValidationError> processCSV(String fileName, byte[] contents) {
        List<ValidationError> errors = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(contents), StandardCharsets.UTF_8))) {

            String metadataLine = reader.readLine();
            Metadata metadata = getMetadata(metadataLine);

            String headerLine = reader.readLine();
            validateHeader(headerLine, metadata.getFileType());

            String line;
            int lineNumber = 2;
            while ((line = reader.readLine()) != null) {
                lineNumber++;

                final String currentLine = line;
                final int currentLineNumber = lineNumber;

                validationList.stream().filter(validation -> validation.isApplicable(metadata.getFileType()))
                        .forEach(validation -> {
                            List<ValidationError> validationErrors = validation.validate(currentLine, currentLineNumber, fileName);
                            errors.addAll(validationErrors);
                        });

            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + e.getMessage(), e);
        }
        return errors;
    }

    private Metadata getMetadata(String metadataLine) {
        String[] metadata = metadataLine.split(";");

        if (metadata.length != 3) {
            throw new IllegalArgumentException("Invalid metadata format");
        }

        String fileType = metadata[0];
        String date = metadata[1];
        String status = metadata[2];

        return new Metadata(fileType, LocalDate.parse(date), status);
    }

    private void validateHeader(String headerLine, String fileType) {
        String[] headers = headerLine.split(";");

    }

}
