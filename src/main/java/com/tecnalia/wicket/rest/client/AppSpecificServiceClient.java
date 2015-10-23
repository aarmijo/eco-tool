package com.tecnalia.wicket.rest.client;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

public class AppSpecificServiceClient {
	public static void main(String[] args) {
    	
		// Create a RESTFul client
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		WebTarget target = client.target(getBaseURI());		
		
		// Invoke the calculation of an application specific service for Alberdi
		String jsonAnswer = target.path("app-specific-service").path("calculate-alberdi/551122/1").request().accept(MediaType.APPLICATION_JSON).get(String.class);		
		System.out.println("Product System 551122 single score: " + jsonAnswer);
		
		client.close();
	}
	
	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:8080").build();
	}
}