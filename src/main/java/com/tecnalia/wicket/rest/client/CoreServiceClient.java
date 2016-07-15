package com.tecnalia.wicket.rest.client;

import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

import pt.uninova.proseco.tools.pes.ontology.EcoAndOptimisationConfiguration;
import pt.uninova.proseco.tools.pes.ontology.PESConfiguration;
import pt.uninova.proseco.tools.pes.ontology.utils.ConfigurationSerializer;
import pt.uninova.proseco.tools.pes.ontology.utils.KMBConfigsVocabulary;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.tecnalia.wicket.pages.ecotool.systems.ProductSystemDescriptor;
import com.tecnalia.wicket.pages.ecotool.systems.kmb.KMBUtils;

import de.atb.proseco.kmb.KMBApi;

public class CoreServiceClient {
	public static void main(String[] args) {
    	
		// Configure the core service with the configuration object related to the PES_ID 5784c8260ffcfd9232dc0e8c 
		KMBApi api = new KMBApi();
		String configIdFromKMB = api.readElementPropertyValue("5784c8260ffcfd9232dc0e8c", KMBConfigsVocabulary.EcoAndOptimisation.getSearchName());
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonElement = (JsonObject) jsonParser.parse(configIdFromKMB);
		String configIdFromJson = jsonElement.get(KMBConfigsVocabulary.EcoAndOptimisation.getSearchName()).getAsString();
		String ConfigStringfromkmb = api.readElementConfiguration(configIdFromJson);

		PESConfiguration configFromKmb = ConfigurationSerializer.newPESConfigurationFromJson(ConfigStringfromkmb);
		EcoAndOptimisationConfiguration finalConfig = ConfigurationSerializer.getConfigurationFromJson(configFromKmb);
		String jsonConfiguration = finalConfig.getJsonConfiguration();
		//Gson gson = new Gson();
		//List<ProductSystemDescriptor> list = gson.fromJson(jsonConfiguration, new TypeToken<List<ProductSystemDescriptor>>() {}.getType());
		//System.out.println(list);
		
		// Create a RESTFul client
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		WebTarget target = client.target(getBaseURI());		
		
		// Configure the core service
		String jsonAnswer = target.path("core-service").path("configure").request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(jsonConfiguration, MediaType.APPLICATION_JSON), String.class);
				
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
