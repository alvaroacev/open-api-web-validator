Overview:
===================
OpenAPI Validator validates HTTP requests and responses against an OpenAPI Specification (OAS).

This Web project validates JSON request or responses against an Open API Specification (OAS) API. The logic is based on [Atlasian Swagger Request Validator framework](https://bitbucket.org/atlassian/swagger-request-validator/src/master/)

The application displays a simple HTML form, where the user inputs the API path, method, optionally the HTTP headers and for responses the expected HTTP status code. On form submission the application displays the results of the validation and the possible list of errors.