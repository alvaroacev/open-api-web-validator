package oas.api.validator.web.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class OASResponse {

	@NotNull(message = "Operation cannot be null")
	private String operation;
	//TODO create enumeration
	@NotNull(message = "Method cannot be null")
	private String method;
	//TODO create enumeration
	@NotNull(message = "Status Code cannot be null")
	@Pattern(regexp = "^[1-5][0-9][0-9]$")
	private int statusCode;
	@NotNull(message = "API Specification cannot be null")
	private String contract;
	@NotNull(message = "Response cannot be null")
	private String response;
	private boolean isValid;
	private String validationReport;
	
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
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getContract() {
		return contract;
	}
	public void setContract(String contract) {
		this.contract = contract;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
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
	@Override
	public String toString() {
		return "OASResponse [operation=" + operation + ", method=" + method + ", statusCode=" + statusCode
				+ ", contract=" + contract + ", response=" + response + ", isValid=" + isValid + ", validationErrors="
				+ validationReport + "]";
	}
}