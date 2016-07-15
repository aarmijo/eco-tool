/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.uninova.proseco.tools.pes.ontology;

/**
 *
 * @author Giovanni
 */
public class EcoAndOptimisationConfiguration extends PESConfiguration {

	private String jsonConfiguration;

	public EcoAndOptimisationConfiguration() {
        super(EcoAndOptimisationConfiguration.class.getName(), "pt.uninova.proseco.services.EcoAndOptimisationService");

    }
	
    /**
	 * @return the jsonConfiguration
	 */
	public String getJsonConfiguration() {
		return jsonConfiguration;
	}

	/**
	 * @param jsonConfiguration the jsonConfiguration to set
	 */
	public void setJsonConfiguration(String jsonConfiguration) {
		this.jsonConfiguration = jsonConfiguration;
	}

}