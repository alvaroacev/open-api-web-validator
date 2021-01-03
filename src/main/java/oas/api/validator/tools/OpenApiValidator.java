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
import oas.api.validator.model.Example;
import oas.api.validator.model.OpenAPIExamples;

public class OpenApiValidator {

    private static final Logger logger = LoggerFactory.getLogger(OpenApiValidator.class);

    public final static String APPLICATION_JSON = "application/json";

    private OpenApiValidator() {}
	
	public static List<OpenAPIExamples> printResults(OpenAPI openApi) {
		List<OpenAPIExamples> openAPIExamples = parseExamples(openApi);
		openAPIExamples.forEach(e -> {
			e.getExamples().forEach((keyE, valueE) -> {
				logger.info("On path {} and HTTP method {}, {} response tests were found", e.getPath(), keyE, valueE.size());
			});
		});
		logger.info("Results of the extraction: \n {}", openAPIExamples);
		return openAPIExamples;
	}

	public static List<OpenAPIExamples> parseExamples(OpenAPI openApi) {
		List<OpenAPIExamples> openAPIExamples = new ArrayList<>();
		if (openApi.getComponents().getExamples() == null) {
			logger.warn("OpenAPI Specification {} - {} has not defined any example", openApi.getInfo().getTitle(), openApi.getInfo().getVersion());
			return openAPIExamples;
		}
		openApi.getPaths().forEach((key, value) -> {
			final OpenAPIExamples api = new OpenAPIExamples();
			api.setPath(key);
			if (value.getGet() != null) {
				logger.debug("Found definition of GET on path: {}", key);
				final List<Example> responses = new ArrayList<>();
				value.getGet().getResponses().forEach((keyR, valueR) -> {
					responses.addAll(searchResponseExamples(openApi, keyR, valueR));
				});
				api.getExamples().put("GET", responses);
			}
			if (value.getPost() != null) {
				logger.debug("Found definition of POST on path: {}", key);
				final List<Example> responses = new ArrayList<>();
				value.getPost().getResponses().forEach((keyR, valueR) -> {
					responses.addAll(searchResponseExamples(openApi, keyR, valueR));
				});
				api.getExamples().put("POST", responses);
			}
			if (value.getPut() != null) {
				logger.debug("Found definition of PUT on path: {}", key);
				final List<Example> responses = new ArrayList<>();
				value.getPut().getResponses().forEach((keyR, valueR) -> {
					responses.addAll(searchResponseExamples(openApi, keyR, valueR));
				});
				api.getExamples().put("PUT", responses);
			}
			if (value.getPatch() != null) {
				logger.debug("Found definition of PATCH on path: {}", key);
				final List<Example> responses = new ArrayList<>();
				value.getPut().getResponses().forEach((keyR, valueR) -> {
					responses.addAll(searchResponseExamples(openApi, keyR, valueR));
				});
				api.getExamples().put("PATCH", responses);
			}
			openAPIExamples.add(api);
		});
		return openAPIExamples;
	}

	private static List<Example> searchResponseExamples(OpenAPI openApi, String statusCode, ApiResponse response) {
		final List<Example> responses = new ArrayList<>();
		final Integer status = buildStatusCode(statusCode);
		if (status != null) {
			if (response.get$ref() != null) {
				final String componentName = response.get$ref().substring(response.get$ref().lastIndexOf("/") + 1);
					logger.debug("Component {} found for status code: {}", componentName, status);
					extractExamples(openApi.getComponents().getResponses().get(componentName).getContent(), //
							componentName, openApi.getComponents()) //
									.forEach((keyE, valueE) -> {
										responses.add(new Example(keyE, status, valueE));
									});
			} else {
				extractExamples(response.getContent(), //
						null, openApi.getComponents()) //
								.forEach((keyE, valueE) -> {
									responses.add(new Example(keyE, status, valueE));
								});
			}
		}
		return responses;
	}
	
	private static Map<String, String> extractExamples(Content content, String componentName, Components components) {
		Map<String, String> examples = new HashMap<>();
		if (content != null) {
			MediaType mediaType = content.get(APPLICATION_JSON);
			if (mediaType != null) {
				if (mediaType.getExample() != null) {
					logger.debug("Adding the example found on component name {}", componentName);
					examples.put("InlineExample", mediaType.getExample().toString());
					return examples;
				} else if(mediaType.getExamples() != null) {
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
	//TODO remove authData param - not used
	public static OpenAPI loadApiFromLocation(@Nonnull final String oasPath, @Nonnull final List<AuthorizationValue> authData) {
		final SwaggerParseResult parseResult = new OpenAPIParser().readLocation(oasPath, authData, new ParseOptions());
		if (parseResult == null || parseResult.getOpenAPI() == null
				|| (parseResult.getMessages() != null && !parseResult.getMessages().isEmpty())) {
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
