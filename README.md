# <h1>pdfToJSONParser</h1>
A project that reads pdf text and converts to JSON

* To run the project you follow the two steps:
    * Start the application, it will run on localhost:8080
    * Send a POST request to the URL localhost:8080/pdf/read
    
* Body of the post request will be:
          ```
        {
            "filePath": "C:/Users/Adey Babs/Downloads/sample.pdf"
        }
        ```
