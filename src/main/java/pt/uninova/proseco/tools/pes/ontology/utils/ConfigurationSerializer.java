/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.uninova.proseco.tools.pes.ontology.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.logging.Level;
import java.util.logging.Logger;

import pt.uninova.proseco.tools.pes.ontology.PESConfiguration;

public class ConfigurationSerializer {

    /**
     *
     * @param <T>
     * @param config
     * @return
     */
    
  
    public static <T extends PESConfiguration> T setConfigurationToJsonAndAddToValue(T config) {

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        //Gson gson = new Gson();
        String stringfyJSON = gson.toJson(config);
        config.setValue(stringfyJSON);
        return config; 
    }

    public static <T extends PESConfiguration> String setConfigurationToJson(T config) {

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        //Gson gson = new Gson();
        String stringfyJSON = gson.toJson(config);
        return stringfyJSON;
    }

    /**
     *
     * @param <T>
     * @param config
     * @return
     */
    @SuppressWarnings("unchecked")
	public static <T extends PESConfiguration> T getConfigurationFromJson(PESConfiguration config) {
        try {
            Gson gson = new Gson();
            T newConfig = gson.fromJson(config.getValue(), (Class<T>) Class.forName(config.getType()));
            newConfig.setValue(config.getValue());
            newConfig.setDescription(config.getDescription());
            return newConfig;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConfigurationSerializer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     *
     * @param jsonConfig
     * @return
     */
    public static PESConfiguration newPESConfigurationFromJson(String jsonConfig) {
        JsonParser parser = new JsonParser();
        JsonObject jsonElement = (JsonObject) parser.parse(jsonConfig);
        String id = jsonElement.get("id").getAsString();
        String type = jsonElement.get("type").getAsString();
        String belongTo = jsonElement.get("belongTo").getAsString();
        PESConfiguration newConfig = new PESConfiguration(type, belongTo);
        newConfig.setValue(jsonConfig);
        newConfig.setId(id);
        newConfig.setDescription("Configuration object to be passed to the config method in the Eco service");
        return newConfig;
    }

}
