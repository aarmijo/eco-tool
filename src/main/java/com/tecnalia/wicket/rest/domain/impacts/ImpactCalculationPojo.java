package com.tecnalia.wicket.rest.domain.impacts;

public class ImpactCalculationPojo {

	private String productSystem;
	private String targetAmount;
	private String impactMethod;
	private String nwMethod;
	private String analysisType;
	private ImpactCategoryPojo [] impacts;
	
	public ImpactCalculationPojo(String productSystem, String targetAmount,
			String impactMethod, String nwMethod, String analysisType) {
		super();
		this.productSystem = productSystem;
		this.targetAmount = targetAmount;
		this.impactMethod = impactMethod;
		this.nwMethod = nwMethod;
		this.analysisType = analysisType;
	}
	/**
	 * @return the productSystem
	 */
	public String getProductSystem() {
		return productSystem;
	}
	/**
	 * @param productSystem the productSystem to set
	 */
	public void setProductSystem(String productSystem) {
		this.productSystem = productSystem;
	}
	/**
	 * @return the targetAmount
	 */
	public String getTargetAmount() {
		return targetAmount;
	}
	/**
	 * @param targetAmount the targetAmount to set
	 */
	public void setTargetAmount(String targetAmount) {
		this.targetAmount = targetAmount;
	}
	/**
	 * @return the impactMethod
	 */
	public String getImpactMethod() {
		return impactMethod;
	}
	/**
	 * @param impactMethod the impactMethod to set
	 */
	public void setImpactMethod(String impactMethod) {
		this.impactMethod = impactMethod;
	}
	/**
	 * @return the nwMethod
	 */
	public String getNwMethod() {
		return nwMethod;
	}
	/**
	 * @param nwMethod the nwMethod to set
	 */
	public void setNwMethod(String nwMethod) {
		this.nwMethod = nwMethod;
	}
	/**
	 * @return the analysisType
	 */
	public String getAnalysisType() {
		return analysisType;
	}
	/**
	 * @param analysisType the analysisType to set
	 */
	public void setAnalysisType(String analysisType) {
		this.analysisType = analysisType;
	}
	/**
	 * @return the impacts
	 */
	public ImpactCategoryPojo[] getImpacts() {
		return impacts;
	}
	/**
	 * @param impacts the impacts to set
	 */
	public void setImpacts(ImpactCategoryPojo[] impacts) {
		this.impacts = impacts;
	}
	
}
