package oas.api.validator.model;

public class Example {
	
	private String name;
	private int statusCode;
	private String payload;
	
	public Example(String name, int statusCode, String payload) {
		super();
		this.payload = payload;
		this.name = name;
		this.statusCode = statusCode;
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
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	@Override
	public String toString() {
		return "Example [name=" + name + ", statusCode=" + statusCode + ", payload=" + payload + "]";
	}

}
