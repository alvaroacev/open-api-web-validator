package oas.api.validator.tools;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.OpenApiInteractionValidator.Builder;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.model.Response;
import com.atlassian.oai.validator.model.SimpleRequest;
import com.atlassian.oai.validator.model.SimpleResponse;
import com.atlassian.oai.validator.report.SimpleValidationReportFormat;
import com.atlassian.oai.validator.report.ValidationReport;

import io.swagger.v3.oas.models.OpenAPI;

public class ValidateReqResp {

    private static final Logger logger = LoggerFactory.getLogger(ValidateReqResp.class);

    private static final String APPLICATION_JSON = "application/json";

    private ValidateReqResp() {}

    /**
     * Validate a body response against the OpenAPI Spec pass as parameter
     * @param openAPISpec The path to the YAML file containing the OAS.
     * @param verb The HTTP verb (for example, POST, GET, HEAD, PUT, PATCH or DELETE).
     * @param path The path component of the URI that you want to test.
     * @param statusCode The HTTP status code (for example, 200 OK, 404 Not Found or 500 Internal Server Error).
     * @param responseBody The HTTP response body.
     * @return String containing a validation error report, empty otherwise
     */
	public static String validateResponse(String openAPISpec, String verbString, String path, Map<String, String> responseHeaders, String statusCode, String responseBody) {
		String response = new String();
		final OpenApiInteractionValidator openApiInteractionValidator = OpenApiInteractionValidator.createForInlineApiSpecification(openAPISpec).build();
		final Request.Method verb = getVerbFromVerbString(verbString);
		final ValidationReport validateResponse = validateResponse(openApiInteractionValidator, verb, path, responseHeaders, statusCode, responseBody);
		if (validateResponse.hasErrors()) {
			response = SimpleValidationReportFormat.getInstance().apply(validateResponse);
		}
		return response;
	}

	/**
	 * Parses all the response examples included in the OpenAPI Spec passed as parameter and validate them against it
	 * @param openAPISpec
	 * @return String containing a validation error report, empty otherwise
	 */
	public static String validateExamples(String openAPISpec) {
		final StringBuilder b = new StringBuilder();
		final StringBuilder successReport = new StringBuilder();
		final OpenAPI api = OpenApiValidator.loadApiFromString(openAPISpec);
		logger.info("OpenAPI Specification {} - {}", api.getInfo().getVersion());
		final OpenApiInteractionValidator openApiInteractionValidator = new Builder()
				.withInlineApiSpecification(openAPISpec).build();
		OpenApiValidator.parseExamples(api).forEach(endpoint -> {
			endpoint.getExamples().forEach((examplesKey, value) -> {
				successReport.append('\n').append("On path ").append(endpoint.getPath()).append(" and HTTP method ")
						.append(examplesKey).append(", ").append(value.size()).append(" response tests were found");
				logger.debug("On path {} and HTTP method {}, {} response tests were found", endpoint.getPath(),
						examplesKey, value.size());
				value.forEach(example -> {
					logger.info("Validate example response with verb {}, endpoint: {}, status: {},  name: {}",
							examplesKey, endpoint.getPath(), example.getStatusCode(), example.getName());
					final ValidationReport validationReport = validateResponse(openApiInteractionValidator,
							getVerbFromVerbString(examplesKey), endpoint.getPath(), new HashMap<String, String>(),
							new Integer(example.getStatusCode()).toString(), example.getPayload());
					if (validationReport.hasErrors()) {
						final String report = SimpleValidationReportFormat.getInstance().apply(validationReport);

						b.append('\n').append("Example: ").append(example.getName()).append('\n').append(report);
						logger.error("{}\n", report);
					}
				});
			});
		});
		if (b.length() == 0) {
			b.append("There were no issues found when validating the example responses for OpenAPI Specification: ") //
					.append(api.getInfo().getTitle()).append(" and version: ") //
					.append(api.getInfo().getVersion());
			b.append(successReport);
		}

		return b.toString();
	}
	
    /**
     * Validate a request body against the open api specification passed as parameter
     * @param openAPISpec The path to the YAML file containing the OAS.
     * @param verb The HTTP verb (for example, POST, GET, HEAD, PUT, PATCH or DELETE).
     * @param path The path component of the URI that you want to test.
     * @param queryParameters The query parameters of the URI.
     * @param requestHeaders The HTTP request headers.
     * @param requestBody The HTTP request body.
     * @return String containing a validation error report, empty otherwise
     */
    public static String validateRequest(String openAPISpec, String verbString, String path, Map<String, String> queryParameters, Map<String, String> requestHeaders, String requestBody) {
    	String response = new String();
        final OpenApiInteractionValidator openApiInteractionValidator = OpenApiInteractionValidator.createForInlineApiSpecification(openAPISpec).build();
        final Request.Method verb = getVerbFromVerbString(verbString);
        final ValidationReport validateRequest = validateRequest(openApiInteractionValidator, verb, path, queryParameters, requestHeaders, requestBody);
		if (validateRequest.hasErrors()) {
			response = SimpleValidationReportFormat.getInstance().apply(validateRequest);
		}
		return response;
    }
    
	private static ValidationReport validateResponse(OpenApiInteractionValidator openApiInteractionValidator, Request.Method verb, String path, Map<String, String> responseHeaders, String statusCode, String responseBody) {
		final Response response = buildResponse(statusCode, responseBody, responseHeaders);
		logger.debug("Response: {}", response);
		final ValidationReport validationReport = openApiInteractionValidator.validateResponse(path, verb, response);
		return validationReport;
	}
	
    /**
     * Build an HTTP response from all the parameters.
     * @param statusCode The HTTP status code (for example, 200 OK, 404 Not Found or 500 Internal Server Error).
     * @param responseBody The HTTP response body.
     * @return The HTTP response.
     */
    private static Response buildResponse(String statusCode, String responseBody, Map<String, String> responseHeaders) {
        Integer buildStatusCode = OpenApiValidator.buildStatusCode(statusCode);
        if(buildStatusCode == null) {
        	return null;
        }
        final SimpleResponse.Builder builder = SimpleResponse.Builder.status(buildStatusCode);
        
        if (responseBody != null) {
            builder.withContentType(APPLICATION_JSON).withBody(responseBody);
        }
        if (responseHeaders != null) {
        	responseHeaders.forEach(builder::withHeader);
        }
        return builder.build();
    }

    /**
     * Convert an HTTP verb string into a Request.Method object.
     * @param verbString The HTTP verb string to be converted.
     * @return The Request.Method object.
     */
    private static Request.Method getVerbFromVerbString(String verbString) {
        final Request.Method verb;
        logger.debug("String {} will be converted", verbString);
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
     * Validate a request body against the open api specification passed as parameter
     * @param openApiInteractionValidator The OpenApiInteractionValidator object used to validate the request.
     * @param verb The HTTP verb (for example, POST, GET, HEAD, PUT, PATCH or DELETE).
     * @param path The path component of the URI that you want to test.
     * @param queryParameters The query parameters of the URI.
     * @param requestHeaders The HTTP request headers.
     * @param requestBody The HTTP request body.
     * @return The ValidationReport object containing the results of the HTTP request validation against the OAS.
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
