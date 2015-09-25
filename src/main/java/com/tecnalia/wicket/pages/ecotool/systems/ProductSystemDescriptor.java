package com.tecnalia.wicket.pages.ecotool.systems;

import java.io.Serializable;

import org.openlca.core.model.Process;
import org.openlca.core.model.ProductSystem;

public class ProductSystemDescriptor implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String name;
	private String description;
	private String referenceProductName;
	private String flowProperty;
	private String unit;
	private double amountValue;	

	public ProductSystemDescriptor(ProductSystem system) {
		setId(system.getId());
		setName(system.getName());		
		if (system.getDescription() == null) {
			setDescription(system.getName());
		} else {
			setDescription(system.getDescription());
		}		
		Process referenceProcess = system.getReferenceProcess();
		setReferenceProductName(referenceProcess.getName());
		setFlowProperty(system.getTargetFlowPropertyFactor().getFlowProperty().getName());
		setUnit(system.getTargetUnit().getName());
		setAmount(system.getTargetAmount());		
	}
	
	@Override
	public String toString() {
		String toString = "[Id" + getId() + ", Name: " + getName() + "]";
		return toString;
	}

	public void setName(String name) {
		this.name = name;		
	}

	public void setDescription(String description) {
		this.description = description;		
	}

	public void setId(long id) {
		this.id = id;		
	}

	public void setFlowProperty(String flowProperty) {
		this.flowProperty = flowProperty;
		
	}

	public void setUnit(String unit) {
		this.unit = unit;
		
	}

	public void setAmount(double amountValue) {
		this.amountValue = amountValue;		
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		if (description.length() > 41) {
			return description.substring(0, 40).concat(" ...");
		} else return description;			
	}

	public String getFlowProperty() {
		return flowProperty;
	}

	public String getUnit() {
		return unit;
	}

	public double getAmountValue() {
		return amountValue;
	}

	public String getReferenceProductName() {
		return referenceProductName;
	}

	public void setReferenceProductName(String referenceProduct) {
		this.referenceProductName = referenceProduct;
	}

}