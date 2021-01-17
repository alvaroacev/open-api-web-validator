package oas.api.validator.tools;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;

class ValidateReqRespTest {

	@Test
	void testValidateExamplesErrorResponse() {
		final String apiContent = ResourceLoader.getResourceAsString("openapi-specs/petstore-with-errors.yaml");
		final String validateExamples = ValidateReqResp.validateExamples(apiContent);
		assertTrue(validateExamples.contains("ERROR"));
	}

	@Test
	void testValidateExamplesMultiResponse() {
		final String apiContent = ResourceLoader.getResourceAsString("openapi-specs/petstore-multi-operations.yaml");
		final String validateExamples = ValidateReqResp.validateExamples(apiContent);
		assertFalse(validateExamples.contains("ERROR"));
	}

	@Test
	void testValidateExamplesResponse() {
		final String apiContent = ResourceLoader.getResourceAsString("openapi-specs/petstore.yaml");
		final String validateExamples = ValidateReqResp.validateExamples(apiContent);
		assertFalse(validateExamples.contains("ERROR"));
	}

	@Test
	void testValidateResponse() {
		final String apiContent = ResourceLoader.getResourceAsString("openapi-specs/petstore.yaml");
		final String responseBody = "{\"id\":0,\"name\":\"string\",\"tag\":\"string\"}";
		final String report = ValidateReqResp.validateResponse(apiContent, "GET", "/pets/{id}",
				new HashMap<String, String>(), "200", responseBody);
		assertEquals("", report);
	}

	@Test
	void testInvalidResponse() {
		final String apiContent = ResourceLoader.getResourceAsString("openapi-specs/petstore.yaml");
		final String responseBody = "{\"id\":0,\"name\":1,\"tag\":\"string\"}";
		final String report = ValidateReqResp.validateResponse(apiContent, "GET", "/pets/{id}",
				new HashMap<String, String>(), "200", responseBody);
		assertTrue(report.contains("[ERROR]")); 
	}


	@Ignore //TODO issue reported by issue #314 - https://bitbucket.org/atlassian/swagger-request-validator 
	public void testDefinedOnResponse() {
		final String apiContent = ResourceLoader.getResourceAsString("openapi-specs/oneOf-defined-on-schema.yaml");
		final String validateExamples = ValidateReqResp.validateExamples(apiContent);
		assertFalse(validateExamples.contains("ERROR")); 
	}
	
	@Test 
	public void testNoExample() {
		final String apiContent = ResourceLoader.getResourceAsString("openapi-specs/no-example-defined.yaml");
		final String validateExamples = ValidateReqResp.validateExamples(apiContent);
		assertTrue(validateExamples.contains("On path /solicitors and HTTP method POST, 0 response tests were found")); 
	}

}
