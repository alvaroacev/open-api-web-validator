package oas.api.validator.web;

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

import com.atlassian.oai.validator.report.SimpleValidationReportFormat;
import com.atlassian.oai.validator.report.ValidationReport;

import oas.api.validator.web.model.OpenAPIValidation;
import oas.api.validator.web.tools.OpenApiSpecifcationValidator;

@Controller
public class ValidationController  {
	
	private static final Logger logger = LoggerFactory.getLogger(OpenApiSpecifcationValidator.class);

	@GetMapping("/index")
	public String indexForm(Model model) {
		OpenAPIValidation validation = new OpenAPIValidation();
		validation.setTestType("Response");
		model.addAttribute("validation", validation);
		return "index";
	}

	@PostMapping("/index")
	public String validateForm(@ModelAttribute OpenAPIValidation validation, Model model) {
		logger.info("received a POST request to validate: {}", validation);
		
		ValidationReport validationReport = null;
		
		if(validation.getTestType().equals("Response")) {
			validationReport = OpenApiSpecifcationValidator.validateResponse(validation.getContract(), //
					validation.getMethod(), //
					validation.getOperation(), //
					validation.getStatusCode(), //
					validation.getPayload());
		} else {
			 validationReport = OpenApiSpecifcationValidator.validateRequest(validation.getContract(), //
					validation.getMethod(), //
					validation.getOperation(), //
					null, null, //
					validation.getPayload());
		}

		if (!validationReport.hasErrors()) {
			validation.setValid(true);
			validation.setValidationReport(
					"The payload was successfully validated.");
		} else {
			validation.setValid(false);
			validation.setValidationReport(
					SimpleValidationReportFormat.getInstance().apply(validationReport));
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