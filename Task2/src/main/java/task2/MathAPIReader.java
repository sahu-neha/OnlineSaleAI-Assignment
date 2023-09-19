package task2;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;

public class MathAPIReader {

	private static final int MAX_API_REQUESTS = 50;
	private static final int MAX_APP_REQUESTS = 500;
	private static final String API_URL = "http://api.mathjs.org/v4/";

	// Method to invoke the API with a list of expressions and return results
	public List<String> callAPI(List<String> expressions) throws IOException, InterruptedException {
		// Create a JSON request body with the expressions
		JSONObject requestBody = new JSONObject();
		requestBody.put("expr", expressions);

		// Initialize a list to store the API response
		List<String> results = new ArrayList<>();

		// Create a URL and HTTP request
		URL apiUrl = new URL(API_URL);
		HttpRequest apiRequest = HttpRequest.newBuilder(URI.create(API_URL)).header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(requestBody.toString())).build();

		// Send the HTTP request and process the response
		HttpResponse<String> apiResponse = HttpClient.newHttpClient().send(apiRequest,
				HttpResponse.BodyHandlers.ofString());
		if (apiResponse.statusCode() <= 299) {
			// Parse the JSON response and extract the results
			JSONObject jsonResponse = new JSONObject(apiResponse.body());
			JSONArray responseArray = (JSONArray) jsonResponse.get("result");
			for (Object result : responseArray.toList()) {
				results.add((String) result);
			}
		}
		return results;
	}

	public static void main(String[] args) throws Exception {
		MathAPIReader mathReader = new MathAPIReader();

		// Initialize a scanner to read user input and a list to store expressions
		Scanner scanner = new Scanner(System.in);
		List<String> expressions = new ArrayList<>();

		// Create an executor service with a fixed number of threads
		ExecutorService executor = Executors.newFixedThreadPool(MAX_APP_REQUESTS / MAX_API_REQUESTS);

		System.out.println("Please enter any mathematical expression");
		System.out.println("Please type 'end' after writing all the expressions");

		while (true) {

			String userInput = scanner.nextLine();

			if (userInput.equals("end")) {
				// When "end" is entered, submit a task to invoke the API with the expressions
				executor.submit(() -> {
					try {
						// Invoke the API with the collected expressions
						List<String> apiResults = mathReader.callAPI(expressions);

						// Print the results
						for (int i = 0; i < apiResults.size(); i++) {
							System.out.println(expressions.get(i) + " => " + apiResults.get(i));
						}

						// Clear the expressions list
						expressions.clear();
					} catch (IOException | InterruptedException e) {
						throw new RuntimeException(e);
					}
				});
				break;
			}

			// Check if the maximum API request limit is reached
			if (expressions.size() == MAX_API_REQUESTS) {
				throw new Exception("Maximum API request limit reached.");
			} else {
				// Add the user's expression to the list
				expressions.add(userInput);
			}
		}
	}

}
