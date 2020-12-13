package oas.api.validator.web.tools;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.model.Response;
import com.atlassian.oai.validator.model.SimpleRequest;
import com.atlassian.oai.validator.model.SimpleResponse;
import com.atlassian.oai.validator.report.SimpleValidationReportFormat;
import com.atlassian.oai.validator.report.ValidationReport;

public class OpenApiSpecifcationValidator {

    private static final Logger logger = LoggerFactory.getLogger(OpenApiSpecifcationValidator.class);

    private static final String APPLICATION_JSON = "application/json";

    private OpenApiSpecifcationValidator() {}


    /**
     * Build an HTTP response from all the parameters.
     * @param statusCode The HTTP status code (for example, 200 OK, 404 Not Found or 500 Internal Server Error).
     * @param responseBody The HTTP response body.
     * @return The HTTP response.
     */
    private static Response buildResponse(Integer statusCode, String responseBody ) {
        final SimpleResponse.Builder builder;
        switch (statusCode) {
            case 200: // OK
            case 201: // Created
            case 202: // Accepted
            case 204: // No Content
            case 400: // Bad Request
            case 401: // Unauthorized
            case 403: // Forbidden
            case 404: // Not Found
            case 405: // Method Not Allowed
            case 406: // Not Acceptable
            case 407: // Proxy Authentication Required
            case 409: // Conflict
            case 410: // Gone
            case 413: // Payload Too Large
            case 415: // Unsupported Media Type
            case 429: // Too Many Requests
            case 500: // Internal Server Error
            case 503: // Service Unavailable
            case 504: // Gateway Timeout
                builder = SimpleResponse.Builder.status(statusCode);
                break;
            default:
                logger.error("The HTTP status code '{}' is not supported.", statusCode);
                return null;
        }
        if (responseBody != null) {
            builder.withContentType(APPLICATION_JSON).withBody(responseBody);
        }
        return builder.build();
    }

    /**
     * Validate an HTTP response.
     * @param openApiInteractionValidator The OpenApiInteractionValidator object used to validate the response.
     * @param verb The HTTP verb (for example, POST, GET, HEAD, PUT, PATCH or DELETE).
     * @param path The path component of the URI that you want to test.
     * @param statusCode The HTTP status code (for example, 200 OK, 404 Not Found or 500 Internal Server Error).
     * @param responseBody The HTTP response body.
     * @return The ValidationReport object containing the result of the HTTP response validation against the OAS.
     */
    private static ValidationReport validateResponse(OpenApiInteractionValidator openApiInteractionValidator, Request.Method verb, String path, Integer statusCode, String responseBody) {
        final Response response = buildResponse(statusCode, responseBody);
        logger.debug("response: {}", response);

        final ValidationReport validationReport = openApiInteractionValidator.validateResponse(path, verb, response);
        if (validationReport.hasErrors()) {
            logger.error("{}\n", SimpleValidationReportFormat.getInstance().apply(validationReport));
        } else {
            logger.debug("Response validation successful.");
        }

        return validationReport;
    }

    /**
     * Validate an HTTP response.
     * @param openAPISpec The path to the YAML file containing the OAS.
     * @param verb The HTTP verb (for example, POST, GET, HEAD, PUT, PATCH or DELETE).
     * @param path The path component of the URI that you want to test.
     * @param statusCode The HTTP status code (for example, 200 OK, 404 Not Found or 500 Internal Server Error).
     * @param responseBody The HTTP response body.
     * @return The ValidationReport object containing the result of the HTTP response validation against the OAS.
     */
    public static ValidationReport validateResponse(String openAPISpec, String verbString, String path, Integer statusCode, String responseBody) {
        final OpenApiInteractionValidator openApiInteractionValidator = OpenApiInteractionValidator.createForInlineApiSpecification(openAPISpec).build();
        final Request.Method verb = getVerbFromVerbString(verbString);
        logger.debug("verb: {}", verb);
        return validateResponse(openApiInteractionValidator, verb, path, statusCode, responseBody);
    }
    
    /**
     * Convert an HTTP verb string into a Request.Method object.
     * @param verbString The HTTP verb string to be converted.
     * @return The Request.Method object.
     */
    private static Request.Method getVerbFromVerbString(String verbString) {
        final Request.Method verb;
        try {
            if (verbString == null) {
                logger.error("The value of the HTTP verb is null.");
                return null;
            }
            verb = Request.Method.valueOf(verbString);
        } catch (IllegalArgumentException e) {
            logger.error("The value of the HTTP verb is invalid.");
            return null;
        }
        return verb;
    }
  
    /**
     * Validate an HTTP request.
     * @param openApiInteractionValidator The OpenApiInteractionValidator object used to validate the request.
     * @param verb The HTTP verb (for example, POST, GET, HEAD, PUT, PATCH or DELETE).
     * @param path The path component of the URI that you want to test.
     * @param queryParameters The query parameters of the URI.
     * @param requestHeaders The HTTP request headers.
     * @param requestBody The HTTP request body.
     * @return The ValidationReport object containing the result of the HTTP request validation against the OAS.
     */
    private static ValidationReport validateRequest(OpenApiInteractionValidator openApiInteractionValidator, Request.Method verb, String path, Map<String, String> queryParameters, Map<String, String> requestHeaders, String requestBody) {
        final Request request = buildRequest(verb, path, queryParameters, requestHeaders, requestBody);
        logger.debug("request: {}", request);

        final ValidationReport validationReport = openApiInteractionValidator.validateRequest(request);
        if (validationReport.hasErrors()) {
            logger.error("{}\n", SimpleValidationReportFormat.getInstance().apply(validationReport));
        } else {
            logger.info("Request validation successful.");
        }

        return validationReport;
    }

    /**
     * Validate an HTTP request.
     * @param openAPISpec The path to the YAML file containing the OAS.
     * @param verb The HTTP verb (for example, POST, GET, HEAD, PUT, PATCH or DELETE).
     * @param path The path component of the URI that you want to test.
     * @param queryParameters The query parameters of the URI.
     * @param requestHeaders The HTTP request headers.
     * @param requestBody The HTTP request body.
     * @return The ValidationReport object containing the result of the HTTP request validation against the OAS.
     */
    public static ValidationReport validateRequest(String openAPISpec, String verbString, String path, Map<String, String> queryParameters, Map<String, String> requestHeaders, String requestBody) {
        final OpenApiInteractionValidator openApiInteractionValidator = OpenApiInteractionValidator.createForInlineApiSpecification(openAPISpec).build();
        final Request.Method verb = getVerbFromVerbString(verbString);
        return validateRequest(openApiInteractionValidator, verb, path, queryParameters, requestHeaders, requestBody);
    }
    
    /**
     * Build an HTTP request from all the parameters.
     * @param verb The HTTP verb (for example, POST, GET, HEAD, PUT, PATCH or DELETE).
     * @param path The path component of the URI that you want to test.
     * @param queryParameters The query parameters of the URI.
     * @param requestHeaders The HTTP request headers.
     * @param requestBody The HTTP request body.
     * @return The HTTP request.
     */
    private static Request buildRequest(Request.Method verb, String path, Map<String, String> queryParameters, Map<String, String> requestHeaders, String requestBody) {
        final SimpleRequest.Builder builder;
        switch (verb) {
            case POST:
                builder = SimpleRequest.Builder.post(path);
                break;
            case GET:
                builder = SimpleRequest.Builder.get(path);
                break;
            case HEAD:
                builder = SimpleRequest.Builder.head(path);
                break;
            case PUT:
                builder = SimpleRequest.Builder.put(path);
                break;
            case PATCH:
                builder = SimpleRequest.Builder.patch(path);
                break;
            case DELETE:
                builder = SimpleRequest.Builder.delete(path);
                break;
            default:
                logger.error("The HTTP verb '{}' is not supported.", verb);
                return null;
        }
        if (queryParameters != null) {
            queryParameters.forEach(builder::withQueryParam);
        }
        if (requestHeaders != null) {
            requestHeaders.forEach(builder::withHeader);
        }
        if (requestBody != null) {
            builder.withContentType(APPLICATION_JSON).withBody(requestBody);
        }
        return builder.build();
    }
}
