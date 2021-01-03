package oas.api.validator.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenAPIExamples {
	
	private String path;
	private Map<String, List<Example>> examples;
	
	public OpenAPIExamples() {
		setExamples(new HashMap<>());
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	public Map<String, List<Example>> getExamples() {
		return examples;
	}

	public void setExamples(Map<String, List<Example>> examples) {
		this.examples = examples;
	}

	@Override
	public String toString() {
		return "OpenAPIExamples [path=" + path + ", examples=" + examples + "]";
	}

}