package com.tecnalia.wicket.rest.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;

public class AppSpecificServiceClient {
	
	// Get logger
	private static final Logger logger = Logger.getLogger(AppSpecificServiceClient.class);
	
	public static void main(String[] args) {
		// Test with Jersey JAX-RS
		test1();
		// Test with HTTP
		test2();
	}

	private static void test1() {
		
		// Cookies reference: http://stackoverflow.com/questions/3467114/how-are-cookies-passed-in-the-http-protocol
		
		// Create a RESTFul client
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		WebTarget target = client.target(getBaseURI());		
		
		// Authenticate the user
		Response loginResponse = target.path("app-specific-service").path("/calculate-alberdi/login/proseco/proseco").request().accept(MediaType.APPLICATION_JSON).get();		
		String jsonAnswer = loginResponse.readEntity(String.class);
		logger.debug("Test 1 - Login result: " + jsonAnswer);
		Map<String, NewCookie> cookies = target.path("app-specific-service").path("/calculate-alberdi/login/proseco/proseco").request().accept(MediaType.APPLICATION_JSON).get().getCookies();
		String cookie = cookies.get("JSESSIONID").toString();
		
		// Invoke the calculation of an application specific service for Alberdi
		jsonAnswer = target.path("app-specific-service").path("calculate-alberdi/turning-aluminium/2").request().header("Cookie", cookie).accept(MediaType.APPLICATION_JSON).get(String.class);
		logger.debug("Test 1 - Calulation result for 2Kg of turning-aluminium: " + jsonAnswer);
		
		client.close();		
	}
	
	private static void test2() {
		
		try {
			HttpURLConnection connection;
			InputStreamReader inputStreamReader;
			BufferedReader br;
			StringBuilder sb;
			String jsonAnswer = null;
			String cookie = null;
			URL url = null;

			url = new URL("http://localhost:8080/app-specific-service/calculate-alberdi/login/proseco/proseco");
			connection = (HttpURLConnection) url.openConnection();
			cookie = connection.getHeaderField("Set-Cookie");
			inputStreamReader = new InputStreamReader(connection.getInputStream());
			br = new BufferedReader(inputStreamReader);
			sb = new StringBuilder();
			while ((jsonAnswer = br.readLine()) != null) {
				sb.append(jsonAnswer);
			}
			jsonAnswer = sb.toString();
			logger.debug("Test 2 - Login result: " + jsonAnswer);

			url = new URL("http://localhost:8080/app-specific-service/calculate-alberdi/turning-aluminium/2");
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Cookie", cookie);
			inputStreamReader = new InputStreamReader(connection.getInputStream());
			br = new BufferedReader(inputStreamReader);
			sb = new StringBuilder();
			while ((jsonAnswer = br.readLine()) != null) {
				sb.append(jsonAnswer);
			}
			jsonAnswer = sb.toString();
			logger.debug("Test 2 - Calulation result for 2Kg of turning-aluminium: " + jsonAnswer);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:8080").build();
	}
}