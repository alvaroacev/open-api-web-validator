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

import oas.api.validator.web.model.OASResponse;
import oas.api.validator.web.tools.OpenApiSpecifcationValidator;

@Controller
public class ValidationController  {
	
	private static final Logger logger = LoggerFactory.getLogger(OpenApiSpecifcationValidator.class);

	@GetMapping("/index")
	public String indexForm(Model model) {
		OASResponse response = new OASResponse();
		model.addAttribute("response", response);
		return "index";
	}

	@PostMapping("/index")
	public String validateForm(@ModelAttribute OASResponse response, Model model) {
		logger.info("received a POST request to validate a response: {}", response);

        ValidationReport validationReport = OpenApiSpecifcationValidator.validateResponse(response.getContract(), //
        		response.getMethod(), //
        		response.getOperation(), //
        		response.getStatusCode(), //
        		response.getResponse());

		
		if (!validationReport.hasErrors()) {
			response.setValid(true);
			response.setValidationReport(
					"The response was successfully validated.");
		} else {
			response.setValid(false);
			response.setValidationReport(
					SimpleValidationReportFormat.getInstance().apply(validationReport));
		}
		model.addAttribute("response", response);

		return "index";
	}
	
	@ExceptionHandler(Exception.class)
	public ModelAndView handleError(HttpServletRequest request, Exception ex) {
		logger.error("Error when processing request: \n{}, error: \n{}", request, ex.getMessage());
		OASResponse response = new OASResponse();
		response.setValid(false);
		response.setValidationReport(ex.getMessage());
		ModelAndView mav = new ModelAndView();
		mav.addObject("response", response);
		mav.setViewName("index");
		return mav;
	}

}