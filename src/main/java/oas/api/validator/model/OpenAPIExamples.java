package oas.api.validator.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenAPIExamples {
	
	private String path;
	private Map<String, List<ResponseExample>> responseExamples;
	private Map<String, List<RequestExample>> requestExamples;
	
	public OpenAPIExamples() {
		setResponseExamples(new HashMap<>());
		setRequestExamples(new HashMap<>());
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	public Map<String, List<ResponseExample>> getResponseExamples() {
		return responseExamples;
	}

	public void setResponseExamples(Map<String, List<ResponseExample>> examples) {
		this.responseExamples = examples;
	}

	public Map<String, List<RequestExample>> getRequestExamples() {
		return requestExamples;
	}

	public void setRequestExamples(Map<String, List<RequestExample>> requestExamples) {
		this.requestExamples = requestExamples;
	}

	@Override
	public String toString() {
		return "OpenAPIExamples [path=" + path + ", examples=" + responseExamples + "]";
	}

}