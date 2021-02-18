package oas.api.validator.model;

import java.util.HashMap;
import java.util.Map;

public class Example {

	protected String name;
	protected String payload;
	protected Map<String, String> queryParameters;
	protected Map<String, String> headerParameters;
	protected Map<String, String> pathParameters;


	public Example() {
		queryParameters = new HashMap<String, String>();
		headerParameters = new HashMap<String, String>();
		pathParameters = new HashMap<String, String>();
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public Map<String, String> getQueryParameters() {
		return queryParameters;
	}

	public Map<String, String> getHeaderParameters() {
		return headerParameters;
	}

	public void setHeaderParameters(Map<String, String> requestHeaders) {
		this.headerParameters = requestHeaders;
	}

	public void setQueryParameters(Map<String, String> queryParameters) {
		this.queryParameters = queryParameters;
	}

	public Map<String, String> getPathParameters() {
		return pathParameters;
	}

	public void setPathParameters(Map<String, String> pathParameters) {
		this.pathParameters = pathParameters;
	}

}