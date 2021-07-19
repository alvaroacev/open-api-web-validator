package oas.api.validator.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.parser.core.models.AuthorizationValue;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import oas.api.validator.model.OpenAPIExamples;
import oas.api.validator.model.RequestExample;
import oas.api.validator.model.ResponseExample;

public class OpenApiValidator {

    private static final Logger logger = LoggerFactory.getLogger(OpenApiValidator.class);

    private final static String APPLICATION_JSON = "application/json";

    private OpenApiValidator() {}
	
	public static List<OpenAPIExamples> printResults(OpenAPI openApi) {
		List<OpenAPIExamples> openAPIExamples = parseExamples(openApi);
		openAPIExamples.forEach(e -> {
			e.getResponseExamples().forEach((keyE, valueE) -> {
				logger.info("On path {} and HTTP method {}, {} response tests were found", e.getPath(), keyE, valueE.size());
			});
		});
		logger.info("Results of the extraction: \n {}", openAPIExamples);
		return openAPIExamples;
	}

	public static List<OpenAPIExamples> parseExamples(OpenAPI openApi) {
		final List<OpenAPIExamples> openAPIExamples = new ArrayList<>();

		openApi.getPaths().forEach((path, pathObject) -> {
			final OpenAPIExamples apiExamples = new OpenAPIExamples();
			apiExamples.setPath(path);
			if (pathObject.getGet() != null) {
				logger.debug("Found definition of GET on path: {}", path);
				final List<ResponseExample> responses = new ArrayList<>();
				pathObject.getGet().getResponses().forEach((keyR, valueR) -> {
					responses.addAll(findResponseExamples(openApi, keyR, valueR));
				});
				apiExamples.getResponseExamples().put("GET", responses);
			}
			if (pathObject.getPost() != null) {
				logger.debug("Found definition of POST on path: {}", path);
				final List<ResponseExample> responses = new ArrayList<>();
				pathObject.getPost().getResponses().forEach((keyR, valueR) -> {
					responses.addAll(findResponseExamples(openApi, keyR, valueR));
				});
				apiExamples.getResponseExamples().put("POST", responses);
				
				final List<RequestExample> requests = new ArrayList<>();
				requests.addAll(findRequestsExamples(openApi, pathObject.getPost().getRequestBody()));
				if(requests.size() > 0) {
					apiExamples.getRequestExamples().put("POST", requests);
					buildRequestParameters(openApi, requests, pathObject.getPost().getParameters());
				}
				
			}
			if (pathObject.getPut() != null) {
				logger.debug("Found definition of PUT on path: {}", path);
				final List<ResponseExample> responses = new ArrayList<>();
				pathObject.getPut().getResponses().forEach((keyR, valueR) -> {
					responses.addAll(findResponseExamples(openApi, keyR, valueR));
				});
				apiExamples.getResponseExamples().put("PUT", responses);
				
				final List<RequestExample> requests = new ArrayList<>();
				requests.addAll(findRequestsExamples(openApi, pathObject.getPut().getRequestBody()));
				if(requests.size() > 0) {
					apiExamples.getRequestExamples().put("PUT", requests);
					buildRequestParameters(openApi, requests, pathObject.getPut().getParameters());
				}
			}
			if (pathObject.getPatch() != null) {
				logger.debug("Found definition of PATCH on path: {}", path);
				final List<ResponseExample> responses = new ArrayList<>();
				pathObject.getPatch().getResponses().forEach((keyR, valueR) -> {
					responses.addAll(findResponseExamples(openApi, keyR, valueR));
				});
				apiExamples.getResponseExamples().put("PATCH", responses);
				
				final List<RequestExample> requests = new ArrayList<>();
				requests.addAll(findRequestsExamples(openApi, pathObject.getPatch().getRequestBody()));
				if(requests.size() > 0) {
					apiExamples.getRequestExamples().put("PATCH", requests);
					buildRequestParameters(openApi, requests, pathObject.getPatch().getParameters());
				}
			}
			if (pathObject.getDelete() != null) {
				logger.debug("Found definition of DELETE on path: {}", path);
				final List<ResponseExample> responses = new ArrayList<>();
				pathObject.getDelete().getResponses().forEach((keyR, valueR) -> {
					responses.addAll(findResponseExamples(openApi, keyR, valueR));
				});
				apiExamples.getResponseExamples().put("DELETE", responses);
				
				final List<RequestExample> requests = new ArrayList<>();
				requests.addAll(findRequestsExamples(openApi, pathObject.getDelete().getRequestBody()));
				if(requests.size() > 0) {
					apiExamples.getRequestExamples().put("DELETE", requests);
					buildRequestParameters(openApi, requests, pathObject.getDelete().getParameters());
				}
			}
			openAPIExamples.add(apiExamples);
		});
		return openAPIExamples;
	}

	private static void buildRequestParameters(final OpenAPI openApi, final List<RequestExample> requests, final List<Parameter> parameters) {
		final Map<String, String> pathParameters = new HashMap<String, String>();
		final Map<String, String> queryParameters = new HashMap<String, String>();
		final Map<String, String> requestHeaders = new HashMap<String, String>();
		if(parameters != null) {
			parameters.forEach(param -> {
				if (param.get$ref() != null) {
					final String componentName = param.get$ref().substring(param.get$ref().lastIndexOf("/") + 1);
						logger.debug("Component {} found for parameter: {}", componentName, param);
						 param = openApi.getComponents().getParameters().get(componentName);
				}
				if (param.getRequired() != null && param.getRequired() == true) {
					switch (param.getIn()) {
					case "path":
						if (param.getExample() != null) {
							pathParameters.put(param.getName(), param.getExample().toString());
						}
						break;
					case "query":
						if (param.getExample() != null) {
							queryParameters.put(param.getName(), param.getExample().toString());
						}
						break;
					case "header":
						if (param.getExample() != null) {
							requestHeaders.put(param.getName(), param.getExample().toString());
						}
						break;
					default:
						logger.warn("parameter type {}, not suported", param.getIn());
						break;
					}
				}});
			requests.forEach(request -> {
				request.getQueryParameters().putAll(queryParameters);
				request.getHeaderParameters().putAll(requestHeaders);
				request.getPathParameters().putAll(pathParameters);
			});
		}
	}

	private static List<ResponseExample> findResponseExamples(OpenAPI openApi, String statusCode, ApiResponse response) {
		final List<ResponseExample> responses = new ArrayList<>();
		final Map<String, String> headerParameters = new HashMap<String, String>();
		final Integer status = buildStatusCode(statusCode);
		if (status != null) {
			if (response.get$ref() != null) {
				final String componentName = response.get$ref().substring(response.get$ref().lastIndexOf("/") + 1);
					logger.debug("Component {} found for status code: {}", componentName, status);
					extractExamples(openApi.getComponents().getResponses().get(componentName).getContent(), //
							componentName, openApi.getComponents()) //
									.forEach((keyE, valueE) -> {
										responses.add(new ResponseExample(keyE, status, valueE));
									});
			} else {
				extractExamples(response.getContent(), //
						null, openApi.getComponents()) //
								.forEach((keyE, valueE) -> {
									responses.add(new ResponseExample(keyE, status, valueE));
								});
			}
			if (!responses.isEmpty()) {
				if (response.getHeaders() != null) {
					response.getHeaders().forEach((headerKey, headerValue) -> {
						if (headerValue.getRequired() != null && headerValue.getRequired() == true) {
							if (headerValue.getExample() != null) {
								headerParameters.put(headerKey, headerValue.getExample().toString());
							}
						}
					});
					responses.forEach(resp -> {
						resp.getHeaderParameters().putAll(headerParameters);
					});
				}
			}
		}
		return responses;
	}


	private static List<RequestExample> findRequestsExamples(OpenAPI openApi, RequestBody request) {
		final List<RequestExample> requests = new ArrayList<>();
		if (request != null) {
			if (request.get$ref() != null) {
				final String componentName = request.get$ref().substring(request.get$ref().lastIndexOf("/") + 1);
				logger.debug("Component {} found", componentName);
				extractExamples(openApi.getComponents().getRequestBodies().get(componentName).getContent(), //
						componentName, openApi.getComponents()) //
								.forEach((keyE, valueE) -> {
									requests.add(new RequestExample(keyE, valueE));
								});
			} else {
				extractExamples(request.getContent(), //
						null, openApi.getComponents()) //
								.forEach((keyE, valueE) -> {
									requests.add(new RequestExample(keyE, valueE));
								});
			}
		}
		return requests;
	}
	
	private static Map<String, String> extractExamples(Content content, String componentName, Components components) {
		final Map<String, String> examples = new HashMap<>();
		if (content != null) {
			MediaType mediaType = content.entrySet().stream() //
					.filter(map -> map.getKey().contains(APPLICATION_JSON)) //
					.map(Map.Entry::getValue).findFirst() //
					.orElse(null);
			if (mediaType != null) {
				if (mediaType.getExample() != null) {
					logger.debug("Adding the example found on component name {}", componentName);
					examples.put("InlineExample", mediaType.getExample().toString());
					return examples;
				} else if (mediaType.getExamples() != null) {
					mediaType.getExamples().forEach((exampleName, exampleObject) -> {
						logger.debug("Adding example named {}", exampleName);
						if (exampleObject.get$ref() != null) {
							String exampleRef = exampleObject.get$ref()
									.substring(exampleObject.get$ref().lastIndexOf("/") + 1);
							examples.put(exampleName, components.getExamples().get(exampleRef).getValue().toString());
						} else {
							examples.put(exampleName, exampleObject.getValue().toString());
						}

					});
				} else {
					logger.warn("No example was found on component {}", componentName);
				}
			} else {
				logger.warn("MediaType {} was not found on component {}", APPLICATION_JSON, componentName);
			}
		}
		return examples;
	}
	
	/**
     * A convenience method to build the response (status) code based on the statusCode passed as parameter 
     * @param statusCode
     * @return {@code SimpleResponse.Builder}
     */
	public static Integer buildStatusCode(String statusCode) {
		switch (statusCode) {
		case "200": // OK
		case "201": // Created
		case "202": // Accepted
		case "204": // No Content
		case "303": // See Other
		case "400": // Bad Request
		case "401": // Unauthorized
		case "403": // Forbidden
		case "404": // Not Found
		case "405": // Method Not Allowed
		case "406": // Not Acceptable
		case "407": // Proxy Authentication Required
		case "409": // Conflict
		case "410": // Gone
		case "413": // Payload Too Large
		case "415": // Unsupported Media Type
		case "429": // Too Many Requests
		case "500": // Internal Server Error
		case "502": // Bad Gateway
		case "503": // Service Unavailable
		case "504": // Gateway Timeout
			return Integer.parseInt(statusCode);
		default:
			logger.warn("The HTTP status code '{}' is not supported.", statusCode);
			return null;
		}
	}


	/**
	 * Loads the {@link OpenAPI} from the specified source and prepares it for
	 * usage.
	 * <p>
	 * See {@link #removeRegexPatternOnStringsOfFormatByte(OpenAPI)} for more
	 * information on the preparation.
	 *
	 * @param specSource The OpenAPI / Swagger specification to use in the
	 *                   validator.
	 * @param authData   Authentication data for reading the specification.
	 * @return the loaded and prepared {@link OpenAPI}
	 */
	public static OpenAPI loadApiFromLocation(@Nonnull final String oasPath) {
		final SwaggerParseResult parseResult = new OpenAPIParser().readLocation(oasPath, new ArrayList<AuthorizationValue>(), new ParseOptions());
		if (parseResult == null || parseResult.getOpenAPI() == null
				|| (parseResult.getMessages() != null && !parseResult.getMessages().isEmpty())) {
			if (parseResult.getMessages() != null && !parseResult.getMessages().isEmpty()) {
				logger.error("Following issues were found when loading the OpenAPI Spec: {}", parseResult.getMessages());
			}
			throw new IllegalArgumentException(
					"Unable to load API spec from provided URL: " + oasPath + " " + parseResult);
		}

		final OpenAPI api = parseResult.getOpenAPI();
		removeRegexPatternOnStringsOfFormatByte(api);
		return api;
	}
	
	/**
	 * Loads the {@link OpenAPI} from the content string passed and prepares it for
	 * usage.
     * Create a new instance using given the OpenAPI / Swagger specification.
     * <p>
     *
     * @param oasContent The OpenAPI / Swagger specification to use in the validator
     * @param authData   Authentication data for reading the specification.
	 * @return the loaded and prepared {@link OpenAPI}
	 */
	public static OpenAPI loadApiFromString(@Nonnull final String oasContent) {
		final SwaggerParseResult parseResult = new OpenAPIParser().readContents(oasContent, new ArrayList<AuthorizationValue>(), new ParseOptions());
		if (parseResult == null || parseResult.getOpenAPI() == null
				|| (parseResult.getMessages() != null && !parseResult.getMessages().isEmpty())) {
			if (parseResult.getMessages() != null && !parseResult.getMessages().isEmpty()) {
				logger.error("Following issues were found when loading the OpenAPI Spec: {}", parseResult.getMessages());
			}
			throw new IllegalArgumentException(
					"Unable to load API spec from provided URL: " + oasContent + " " + parseResult);
		}

		final OpenAPI api = parseResult.getOpenAPI();
		removeRegexPatternOnStringsOfFormatByte(api);
		return api;
	}

	/**
	 * Removes the Base64 pattern on the {@link OpenAPI} model.
	 * <p>
	 * If that pattern would stay on the model all fields of type string / byte
	 * would be validated twice. Once with the
	 * {@link com.github.fge.jsonschema.keyword.validator.common.PatternValidator}
	 * and once with the
	 * {@link com.atlassian.oai.validator.schema.format.Base64Attribute}. To improve
	 * validation performance and memory footprint the pattern on string / byte
	 * fields will be removed - so the PatternValidator will not be triggered for
	 * those kind of fields.
	 *
	 * @param openAPI the {@link OpenAPI} to correct
	 */
	private static void removeRegexPatternOnStringsOfFormatByte(@Nonnull final OpenAPI openAPI) {
		if (openAPI.getPaths() != null) {
			openAPI.getPaths().values().forEach(pathItem -> {
				pathItem.readOperations().forEach(operation -> {
					excludeBase64PatternFromEachValue(operation.getResponses(), ApiResponse::getContent);
					if (operation.getRequestBody() != null) {
						excludeBase64PatternFromSchema(operation.getRequestBody().getContent());
					}
					if (operation.getParameters() != null) {
						operation.getParameters().forEach(it -> excludeBase64PatternFromSchema(it.getContent()));
						operation.getParameters().forEach(it -> excludeBase64PatternFromSchema(it.getSchema()));
					}
				});
			});
		}
		if (openAPI.getComponents() != null) {
			excludeBase64PatternFromEachValue(openAPI.getComponents().getResponses(), ApiResponse::getContent);
			excludeBase64PatternFromEachValue(openAPI.getComponents().getRequestBodies(), RequestBody::getContent);
			excludeBase64PatternFromEachValue(openAPI.getComponents().getHeaders(), Header::getContent);
			excludeBase64PatternFromEachValue(openAPI.getComponents().getHeaders(), Header::getSchema);
			excludeBase64PatternFromEachValue(openAPI.getComponents().getParameters(), Parameter::getContent);
			excludeBase64PatternFromEachValue(openAPI.getComponents().getParameters(), Parameter::getSchema);
			excludeBase64PatternFromEachValue(openAPI.getComponents().getSchemas(), schema -> schema);
		}
	}

	private static <T> void excludeBase64PatternFromEachValue(final Map<String, T> map,
			final Function<T, Object> function) {
		if (map != null) {
			map.values().forEach(it -> excludeBase64PatternFromSchema(function.apply(it)));
		}
	}

	private static void excludeBase64PatternFromSchema(@Nonnull final Object object) {
		if (object instanceof Content) {
			excludeBase64PatternFromEachValue((Content) object, MediaType::getSchema);
		} else if (object instanceof ObjectSchema) {
			excludeBase64PatternFromEachValue(((ObjectSchema) object).getProperties(), schema -> schema);
		} else if (object instanceof ArraySchema) {
			excludeBase64PatternFromSchema(((ArraySchema) object).getItems());
		} else if (object instanceof StringSchema) {
			final StringSchema stringSchema = (StringSchema) object;
			// remove the pattern _only_ if it's a String / Byte field
			if ("byte".equals(stringSchema.getFormat())) {
				stringSchema.setPattern(null);
			}
		}
	}


}
