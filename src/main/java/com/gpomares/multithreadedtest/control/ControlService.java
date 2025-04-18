package com.gpomares.multithreadedtest.control;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ControlService {

    List<ValidationError> doControl(MultipartFile multipartFile) throws IOException;
}
