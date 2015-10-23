package com.tecnalia.wicket.rest.client;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tecnalia.wicket.pages.ecotool.systems.kmb.KMBUtils;

public class CoreServiceClient {
	public static void main(String[] args) {
    	
		// Configure the core service with the configuration object related to PES_12345
		String propertyValue = KMBUtils.readElementPropertyValue("PES_12345", "hasEcoConfiguration");
		JsonObject jsonObject = new JsonParser().parse(propertyValue).getAsJsonObject();
		String ecotoolId = jsonObject.get("hasEcoConfiguration").getAsString();
		String configText = KMBUtils.readElementConfiguration(ecotoolId);
		
		// Create a RESTFul client
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		WebTarget target = client.target(getBaseURI());		
		
		// Configure the core service
		String jsonAnswer = target.path("core-service").path("configure").request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(configText, MediaType.APPLICATION_JSON), String.class);
				
		// Invoke the calculation of the impact - Characterization
		jsonAnswer = target.path("core-service").path("calculate/1/characterization").request().accept(MediaType.APPLICATION_JSON).get(String.class);		
		System.out.println("Product System 1 characterization: " + jsonAnswer);
		
		// Invoke the calculation of the impact - Normalization
		jsonAnswer = target.path("core-service").path("calculate/1/normalization").request().accept(MediaType.APPLICATION_JSON).get(String.class);		
		System.out.println("Product System 1 normalization: " + jsonAnswer);
		
		// Invoke the calculation of the impact - Single Score
		jsonAnswer = target.path("core-service").path("calculate/1").request().accept(MediaType.APPLICATION_JSON).get(String.class);		
		System.out.println("Product System 1 single score: " + jsonAnswer);
		
		client.close();
	}
	
	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:8080").build();
	}
}
