package oas.api.validator.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceLoader {

	private static final Logger logger = LoggerFactory.getLogger(ResourceLoader.class);

	private ResourceLoader() {
	}

	/**
	 * Return a String object with the content of the file.
	 * 
	 * @param filePath The path to the file to be read.
	 * @return A string with the content of the file.
	 */
	public static String load(String filePath) {
		logger.info("filePath: {}", filePath);
		if (filePath == null) {
			return null;
		}
		try {
			@SuppressWarnings("resource")
			final BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(filePath)));
			final StringBuilder stringBuilder = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				logger.info("line: {}", line);
				stringBuilder.append(line).append('\n');
			}
			return stringBuilder.toString();
		} catch (Exception e) {
			logger.error("Unexpected error", e);
			return null;
		}
	}


	/**
	 * 
	 * When dealing with local resources, for example when loading a file including
	 * the API request tests. This method may be useful as one can pass the name of
	 * the local resource to be loaded as String
	 * 
	 * @param resourceName
	 * @return
	 */
	public static String getResourceAsString(String resourceName) {
		URL u = ClassLoader.getSystemClassLoader().getResource(resourceName);
		if (u == null) {
			return null;
		}
		String path = u.getPath().replaceFirst("^/(.:/)", "$1");
		logger.info("absolute resource path found : {}", path);
		String s = null;
		try {
			s = new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			logger.error("Unexpected error", e);
			return null;
		}
		return s;
	}

}
