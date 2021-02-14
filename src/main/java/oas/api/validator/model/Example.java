package oas.api.validator.model;

public class Example {

	protected String name;
	protected String payload;

	public Example() {
		super();
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

}