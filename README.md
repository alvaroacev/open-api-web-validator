## Overview:

**OpenAPI Validator** helps you to ensure the API Specification is compliant against the [OpenAPI Specification (OAS)](https://spec.openapis.org/oas/v3.1.0) and that the examples are valid against your API specification. 


The application presents a very simple form to provide the OpenAPI Specification and the example to validate. There are two modes:

* In-line examples

OpenAPI Validator can validate a specific request or response example provided as input, in this mode indicate also the API Method and Path of the specification the example is linked to, for examples GET /dogs. For response validations, indicate which status code is the response example linked to, for example 200, 201 or 400.


* Validate all examples defined in the API specification

When this option is selected, only the OpenAPI Specification is taken as input, the application validates all the requests and responses examples defined in the specification under section [Examples of the media type](https://spec.openapis.org/oas/v3.1.0#example-object) or [example](https://spec.openapis.org/oas/v3.1.0#media-type-object). Please note if referencing a schema which contains an example, the examples value overrides the example provided by the schema.


There may be HTTP headers required as part of the response validation, the form allows you to input the header name and value, following a simple structure key:value, separated by comma ',' should there be multiple headers


## Install and run

The validation engine of this project is based on [Atlasian Swagger Request Validator framework](https://bitbucket.org/atlassian/swagger-request-validator/src/master/) and [Spring MVC](https://spring.io/guides/gs/serving-web-content/) which is already indicated as dependency in the pom file

To install OpenAPI Validator, first clone project, mvn install and run as java, follow the following commands:

```
git clone https://github.com/alvaroacev/open-api-web-validator.git
mvn install
java -jar target/open-api-web-validator-1.0.1-SNAPSHOT.jar
```

## Usage

Open a browser, the application is exposed by default on port 8080, http://localhost:8080/index
Following form will be displayed <br/><img src="./src/main/resources/imgs/input form.png" width="400" height="790"/>


After form submission, a console will appear with the test results 
<br/><img src="./src/main/resources/imgs/results console.png" width="50%" height="50%"/>


## License

Copyright [2021]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
