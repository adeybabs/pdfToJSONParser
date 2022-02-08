package com.trial.pdfToJSONReader.controller;

import com.trial.pdfToJSONReader.entities.SampleModel;
import com.trial.pdfToJSONReader.utils.FileReader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/pdf")
public class AppController {

    private final FileReader fileReader;

    //I injected the file reader class
    public AppController(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    @PostMapping(value = "/read", produces = "application/json", consumes = "application/json")
    public SampleModel readFileAndGiveResults(@RequestBody AppRequestObject appRequestObject) {
        return fileReader.read(appRequestObject.getFilePath());
    }
}
