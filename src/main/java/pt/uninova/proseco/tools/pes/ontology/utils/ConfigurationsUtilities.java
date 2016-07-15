/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.uninova.proseco.tools.pes.ontology.utils;

import java.util.UUID;

import de.atb.proseco.kmb.KMBApi;

/**
 *
 * @author Guilherme
 */
public class ConfigurationsUtilities {

    static String deployableConfigStringId = "Task_";
    static KMBApi kmbapi = new KMBApi();
    static String configNamespace = ("ProSEco_Ontological_Model:");



    public static boolean WriteDeployableConfiguration(String PesId, String configId, String configJsonString, KMBConfigsVocabulary voc, String classSimpleName) {
        try {
            kmbapi.writeElementInformation(PesId, unifyStringForKMBInformation(voc.getSearchName(), configId));
            kmbapi.writeElementConfiguration(configId, placeNameSpaceBeforeName(classSimpleName), configJsonString);
            return true;
        } catch (RuntimeException e) {
            System.out.println(e);
        }
        return false;
    }

    public static String unifyStringForKMBInformation(String ontName, String configId) {
        String res = new StringBuilder("{").append(ontName).append(":").append(configId).append("}").toString();
        return res;
    }

    public static String CreateConfigurationIdForDeployableService() {
        String res = new StringBuilder(deployableConfigStringId).append(UUID.randomUUID().toString()).toString();
        return res;
    }

    public static String placeNameSpaceBeforeName(String classSimpleName) {
        String res = configNamespace + classSimpleName;
        return res;
    }
}
