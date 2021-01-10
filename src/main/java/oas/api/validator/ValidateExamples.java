package oas.api.validator;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.models.OpenAPI;
import oas.api.validator.tools.OpenApiValidator;

public class ValidateExamples {

	private static final Logger logger = LoggerFactory.getLogger(ValidateExamples.class);
	private static final String OAS_FILE_PATH = "oaspath";

	/**
	 * The main entry point of the Command-Line Interface (CLI) application.
	 * 
	 * @param args The testCasesDirectory option.
	 */
	public static void main(String[] args) {
		final Options options = new Options();
		options.addOption(OAS_FILE_PATH, true, "OpenAPI Specification path");

		final CommandLineParser commandLineParser = new DefaultParser();
		try {
			final CommandLine commandLine = commandLineParser.parse(options, args);
			final String openAPIFile = commandLine.getOptionValue(OAS_FILE_PATH);
			logger.info("openAPIFile: {}", openAPIFile);
			if (openAPIFile == null || !Files.exists(Paths.get(openAPIFile))
					|| !Files.isReadable(Paths.get(openAPIFile))) {
				new HelpFormatter().printHelp(
						"java -cp \"open-api-example-validator-1.0.0.jar:dependency/*\" oas.api.validator.ValidateExamples",
						options);
				logger.error("The path to the OpenAPI Specification is missing or invalid.");
				return;
			}
			try {
				OpenAPI openApi = OpenApiValidator.loadApiFromLocation(openAPIFile);
				logger.info("API Title: {} - version: {} ", openApi.getInfo().getTitle(),
						openApi.getInfo().getVersion());
				OpenApiValidator.printResults(openApi);
			} catch (Exception e) {
				logger.error("Error when processing openAPI file: {}.", openAPIFile, e);
			}
		} catch (Exception e) {
			logger.error("Unexpected error", e);
		}
	}

}
