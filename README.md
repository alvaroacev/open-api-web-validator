# open-api-web-validator

This project is meant to validate JSON responses against a known Open API Specification (OAS) API contract. The logic is based on [Atlasian Swagger Request Validator framework](https://bitbucket.org/atlassian/swagger-request-validator/src/master/)

The application displays a simple HTML form, where the user input the path, method, the expected http code, the OpenAPI Specification and the API response or request. On form submission the application will display the errors should there be any.