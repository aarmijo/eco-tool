/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.uninova.proseco.tools.pes.ontology.utils;

/**
 *
 * @author Guilherme
 */
public enum KMBConfigsVocabulary {

    AmIMonitoring("AmIMonitoring"), 
    ContextExtraction("ContextExtraction"), 
    DataMining ("DataMining"), 
    KnowledgeProvisioning("KnowledgeProvisioning"), 
    EcoAndOptimisation("EcoAndOptimisation"),
    ServiceComposition ("ServiceComposition"),
    Security("Security"),
    ApplicationSpecific("ApplicationSpecific");

    private final String searchName;


    private KMBConfigsVocabulary(String searchName) {
        this.searchName = searchName;
    }

    public String getSearchName() {
        return ConfigurationsUtilities.placeNameSpaceBeforeName("has"+searchName+"Configuration");
    }
    
    public String getServiceSimpleName(){
        return searchName+"Service";
    }
    
    public String getOntology(){
       return searchName;
    }
    
    public String getConfigurationSimpleName(){
        return searchName+"Configuration";
    }
}
