package oas.api.validator.model;

public class ResponseExample extends Example {
	
	private int statusCode;
	public ResponseExample(String name, int statusCode, String payload) {
		super();
		this.payload = payload;
		this.name = name;
		this.statusCode = statusCode;
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
