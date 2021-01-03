package oas.api.validator.web.model;

public class OpenAPIValidation {

	private String operation;
	//TODO create enumeration
	private String method;
	//TODO create enumeration
	private String statusCode;
	private String contract;
	private String payload;
	private boolean isValid;
	private String validationReport;
	private String testType;
	private String headers;
	
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getContract() {
		return contract;
	}
	public void setContract(String contract) {
		this.contract = contract;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	public String getValidationReport() {
		return validationReport;
	}
	public void setValidationReport(String validationReport) {
		this.validationReport = validationReport;
	}
	public String getTestType() {
		return testType;
	}
	public void setTestType(String testType) {
		this.testType = testType;
	}
	public String getHeaders() {
		return headers;
	}
	public void setHeaders(String headers) {
		this.headers = headers;
	}

	@Override
	public String toString() {
		return "OpenAPIValidation [operation=" + operation + ", method=" + method + ", statusCode=" + statusCode
				+ ", contract=" + contract + ", payload=" + payload + ", isValid=" + isValid + ", validationReport="
				+ validationReport + ", testType=" + testType + ", headers=" + headers + "]";
	}
}