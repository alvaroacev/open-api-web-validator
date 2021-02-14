package oas.api.validator.model;

import java.util.HashMap;
import java.util.Map;

public class RequestExample extends Example {

	private Map<String, String> queryParameters;
	private Map<String, String> requestHeaders;
	private Map<String, String> pathParameters;

	public RequestExample(String name, String payload) {
		super();
		this.payload = payload;
		this.name = name;
		queryParameters = new HashMap<String, String>();
		requestHeaders = new HashMap<String, String>();
		pathParameters = new HashMap<String, String>();
	}

	public Map<String, String> getQueryParameters() {
		return queryParameters;
	}

	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}

	public void setRequestHeaders(Map<String, String> requestHeaders) {
		this.requestHeaders = requestHeaders;
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
