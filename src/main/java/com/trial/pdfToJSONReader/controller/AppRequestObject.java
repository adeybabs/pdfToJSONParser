package com.trial.pdfToJSONReader.controller;

public class AppRequestObject {

    //Please specify the full file pathName, i.e "C:/Users/Adey Babs/Downloads/sample.pdf"
    private String filePath = "C:/Users/Adey Babs/Downloads/sample.pdf";

    public AppRequestObject() {
    }

    public AppRequestObject(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
