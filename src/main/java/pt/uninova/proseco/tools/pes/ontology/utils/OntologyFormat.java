/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.uninova.proseco.tools.pes.ontology.utils;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Guilherme
 */
public class OntologyFormat {

    public static String serviceNameToOntology(String serviceName) {
        String res = "";
        String SimpleClassName = getSimpleNameFromFullName(serviceName);
        if (StringUtils.countMatches(SimpleClassName, "Service") == 1 && !StringUtils.startsWith(SimpleClassName, "Service")) {
            res = SimpleClassName.replace("Service", "");
        }
        return res;
    }

    public static String ontologyToServiceName(String ontology) {
        return ontology + "Service";
    }

    public static String getSimpleNameFromFullName(String fullName) {
        return ClassUtils.getShortClassName(fullName);
    }

    public static String insertSpaceBeforeCaps(String ontology) {
        String res = ontology.replaceAll("([A-Z])", " $1");
        return StringUtils.trim(res);
    }

    public static String getSpacedBeforeCapsFromFullName(String fullName) {
        String simpleName = getSimpleNameFromFullName(fullName);
        String ontology = serviceNameToOntology(simpleName);
        String res = insertSpaceBeforeCaps(ontology);
        return StringUtils.trim(res);
    }

    public static String getOntologyFromSpacedName(String spacedName) {
        String res = StringUtils.deleteWhitespace(spacedName);
        return res;
    }

    public static String getServiceNameFromSpacedOntology(String spacedOntology) {
        String ontology = getOntologyFromSpacedName(spacedOntology);
        String simpleName = ontologyToServiceName(ontology);
        return StringUtils.trim(simpleName);
    }

    public static String getKMBVocabularyValueFromConfigurationName(String ConfigName) {
        String res = "";
        if (ConfigName.contains("Configuration")) {
            res = ConfigName.replace("Configuration", "");
        }
        return res;
    }
}
