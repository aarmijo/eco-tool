/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.uninova.proseco.servicecomposition.kmb;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.atb.proseco.kmb.KMBApi;
import java.util.HashMap;
import java.util.List;
import pt.uninova.proseco.tools.pes.ontology.utils.ConfigurationSerializer;
import pt.uninova.proseco.tools.pes.ontology.PESConfiguration;
import pt.uninova.proseco.tools.pes.ontology.utils.ConfigurationsUtilities;

/**
 *
 * @author Giovanni
 */
public class KMBManagement {

    private static final KMBApi kmbApi = new KMBApi();

    @SuppressWarnings("unused")
	private static PESConfiguration createConfiguration(String ConfigurationId, String ClazzName) {
        String configuration = kmbApi.readElementConfiguration(ConfigurationId);
        //T object = (T) gson.fromJson(configuration, Class.forName(ClazzName));
        PESConfiguration StringfiedConfiguration = new PESConfiguration();
        StringfiedConfiguration.setValue(configuration);
        StringfiedConfiguration.setType(ClazzName);
        return StringfiedConfiguration;
    }

    @SuppressWarnings("unused")
	private static PESConfiguration createConfiguration(String ConfigurationId) {
        String configuration = kmbApi.readElementConfiguration(ConfigurationId);
        //T object = (T) gson.fromJson(configuration, Class.forName(ClazzName));
        PESConfiguration StringfiedConfiguration = ConfigurationSerializer.newPESConfigurationFromJson(configuration);
        //StringfiedConfiguration.setValue(configuration);
        //StringfiedConfiguration.setType(ClazzName);
        return StringfiedConfiguration;
    }

    public static <K, V> HashMap<K, V> mapFromArrays(K[] keys, V[] values) {
        HashMap<K, V> result = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            result.put(keys[i], values[i]);
        }
        return result;

    }

    @SuppressWarnings("unchecked")
	public static <T extends PESConfiguration> T retrieveConfiguration(List<PESConfiguration> configurations, Class<T> clazz) {
        for (PESConfiguration configuration : configurations) {
            if (clazz.isInstance(configuration)) {
                return (T) configuration;
            }
        }
        return null;
    }

    public static String retrieveAuthor(String PesId) {
        return kmbApi.readElementPropertyValue(PesId, "CreatedBy");
    }

    public static boolean writeConfigurationToKMB(String PesId, String JsonBean, String ConfigVocabularyString, String ConfigId, String ConfigSimpleName) {
        if (PesId == null || JsonBean == null) {
            return false;
        }        
        // Double the quotes as indicated in KMB API Spec document.
        String JsonBeanDoubleQuotes = JsonBean.replace("\"", "\"\"");
        String property = ConfigurationsUtilities.unifyStringForKMBInformation(ConfigVocabularyString, ConfigId);
        String propertyDoubleQuotes = property.replace("\"", "\"\"");
        kmbApi.writeElementInformation(PesId, propertyDoubleQuotes);
        kmbApi.writeElementConfiguration(ConfigId,
                ConfigurationsUtilities.placeNameSpaceBeforeName(ConfigSimpleName), JsonBeanDoubleQuotes);
        return true;
    }

    public static String ReadConfigurationFromKMB(String PesId, String ConfigVocabularyString) {
        String res = "";
        if (PesId == null || ConfigVocabularyString == null) {
            return res;
        }
        String configIdFromKMB = kmbApi.readElementPropertyValue(PesId, ConfigVocabularyString);
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonElement = (JsonObject) jsonParser.parse(configIdFromKMB);
        String configIdFromJson = jsonElement.get(ConfigVocabularyString).getAsString();
        String ConfigStringfromkmb = kmbApi.readElementConfiguration(configIdFromJson);
        return ConfigStringfromkmb;
    }

    public static boolean WriteElementInformation(String id_Element, String JsonTextDoubleQuoted) {
        try {
            kmbApi.writeElementInformation(id_Element, JsonTextDoubleQuoted);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String readElementPropertyValue(String pesId, String property) {
        String resJson = kmbApi.readElementPropertyValue(pesId, property);
        if (!resJson.contains("does not exist")) {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonElement = (JsonObject) jsonParser.parse(resJson);
            String configIdFromJson = jsonElement.get(property).getAsString();
            //System.out.println(configIdFromJson);
            return configIdFromJson;
        }
        return null;
    }
}
