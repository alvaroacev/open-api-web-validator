package oas.api.validator.tools;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.models.OpenAPI;
import oas.api.validator.model.OpenAPIExamples;

public class ParseOpenAPI {

	private static final Logger logger = LoggerFactory.getLogger(ParseOpenAPI.class);

	@Test
	public void testOnlyGetAPI() {
		final String oasPath = "openapi-specs/petstore.yaml";
		OpenAPI openApi = OpenApiValidator.loadApiFromLocation(oasPath);
		assertNotNull(openApi);
		logger.info("API Title: {} - version: {} ", openApi.getInfo().getTitle(), openApi.getInfo().getVersion());
		OpenApiValidator.printResults(openApi);
	}

	@Test
	public void testCompleteAPI() {
		final String oasPath = "openapi-specs/petstore-multi-operations.yaml";
		OpenAPI openApi = OpenApiValidator.loadApiFromLocation(oasPath);
		assertNotNull(openApi);
		logger.info("API Title: {} - version: {} ", openApi.getInfo().getTitle(), openApi.getInfo().getVersion());
		List<OpenAPIExamples> openAPIExamples = OpenApiValidator.printResults(openApi);
		Optional<OpenAPIExamples> result = openAPIExamples.stream().filter(example -> example.getPath().equals("/pets/findByStatus")).findFirst();
		assertTrue(result.isPresent());
		assertTrue(result.get().getExamples().size() == 1);
	}
	
	@Test
	public void testPetsPOST() {
		final String oasPath = "openapi-specs/petstore-multi-operations.yaml";
		OpenAPI openApi = OpenApiValidator.loadApiFromLocation(oasPath);
		assertNotNull(openApi);
		logger.info("API Title: {} - version: {} ", openApi.getInfo().getTitle(), openApi.getInfo().getVersion());
		List<OpenAPIExamples> openAPIExamples = OpenApiValidator.printResults(openApi);
		Optional<OpenAPIExamples> result = openAPIExamples.stream().filter(example -> example.getPath().equals("/pets")).findFirst();
		assertTrue(result.isPresent());
		assertTrue(result.get().getExamples().get("POST").size() == 1);
	}
	
	@Test
	public void testPetsPUT() {
		final String oasPath = "openapi-specs/petstore-multi-operations.yaml";
		OpenAPI openApi = OpenApiValidator.loadApiFromLocation(oasPath);
		assertNotNull(openApi);
		logger.info("API Title: {} - version: {} ", openApi.getInfo().getTitle(), openApi.getInfo().getVersion());
		List<OpenAPIExamples> openAPIExamples = OpenApiValidator.printResults(openApi);
		Optional<OpenAPIExamples> result = openAPIExamples.stream().filter(example -> example.getPath().equals("/pets")).findFirst();
		assertTrue(result.isPresent());
		assertTrue(result.get().getExamples().get("PUT").size() == 1);
	}
	
}
