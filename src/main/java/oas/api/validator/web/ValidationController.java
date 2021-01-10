package oas.api.validator.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import oas.api.validator.tools.ValidateReqResp;
import oas.api.validator.web.model.OpenAPIValidation;

@Controller
public class ValidationController {

	private static final Logger logger = LoggerFactory.getLogger(ValidationController.class);

	@GetMapping("/index")
	public String indexForm(Model model) {
		OpenAPIValidation validation = new OpenAPIValidation();
		validation.setTestType("Response");
		model.addAttribute("validation", validation);
		return "index";
	}

	@PostMapping("/index")
	public String validateForm(@ModelAttribute OpenAPIValidation validation, Model model) {
		logger.debug("received a request to validate: {}", validation);

		String validationReport = new String();

		switch (validation.getTestType()) {
		case "Examples":
			validationReport = ValidateReqResp.validateExamples(validation.getContract());
			validation.setValid(!validationReport.contains("ERROR"));
			validation.setValidationReport(validationReport.replaceAll("\n", "<br>"));
			logger.info("Resuls of the example validations: {}", validation);
			break;

		case "Response":
			final Map<String, String> responseHeaders = new HashMap<>();
			if (validation.getHeaders() == null || !validation.getHeaders().isEmpty()) {
				responseHeaders.putAll(Arrays.stream(validation.getHeaders().split(",")) //
						.map(i -> i.split(":")) //
						.collect(Collectors.toMap(a -> a[0], a -> a[1])));
				logger.info("headers: {}", responseHeaders);
			}
			validationReport = ValidateReqResp.validateResponse(validation.getContract(), //
					validation.getMethod(), //
					validation.getOperation(), //
					responseHeaders,
					validation.getStatusCode(), //
					validation.getPayload());
			if (validationReport.isEmpty()) {
				validation.setValid(true);
				validation.setValidationReport("The response payload was successfully validated.");
			} else {
				validation.setValid(false);
				validation.setValidationReport(validationReport);
			}
			break;
			
		case "Request":
			final Map<String, String> requestHeaders = new HashMap<>();
			if (validation.getHeaders() == null || !validation.getHeaders().isEmpty()) {
				requestHeaders.putAll(Arrays.stream(validation.getHeaders().split(",")) //
						.map(i -> i.split(":")) //
						.collect(Collectors.toMap(a -> a[0], a -> a[1])));
				logger.info("headers: {}", requestHeaders);
			}
			
			validationReport = ValidateReqResp.validateRequest(validation.getContract(), //
					validation.getMethod(), //
					validation.getOperation(), //
					null, requestHeaders, //
					validation.getPayload());
			if (validationReport.isEmpty()) {
				validation.setValid(true);
				validation.setValidationReport("The request payload was successfully validated.");
			} else {
				validation.setValid(false);
				validation.setValidationReport(validationReport);
			}
			break;
			
		default:
			String format = "Validation type " + validation.getTestType() + " is not supported";
			logger.warn(format);
			validation.setValid(false);
			validation.setValidationReport(format);
			break;
		}
		model.addAttribute("validation", validation);

		return "index";
	}

	@ExceptionHandler(Exception.class)
	public ModelAndView handleError(HttpServletRequest request, Exception ex) {
		logger.error("Error when processing request: \n{}, error: \n{}", request, ex.getMessage());
		OpenAPIValidation validation = new OpenAPIValidation();
		validation.setValid(false);
		validation.setValidationReport(ex.getMessage());
		ModelAndView mav = new ModelAndView();
		mav.addObject("validation", validation);
		mav.setViewName("index");
		return mav;
	}

}