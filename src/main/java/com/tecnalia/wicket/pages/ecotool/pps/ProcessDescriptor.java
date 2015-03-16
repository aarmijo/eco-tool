package com.tecnalia.wicket.pages.ecotool.pps;

import java.io.Serializable;

import org.openlca.core.model.Process;

public class ProcessDescriptor implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String name;
	private String description;
	private String category;
	private String quantitativeReference;
	private String flowProperty;
	private String unit;
	private double amountValue;	

	public ProcessDescriptor(Process pps) {
		setId(pps.getId());
		setName(pps.getName());
		setDescription(pps.getDescription());
		setCategory(pps.getCategory().getName());
		setQuantitativeReference(pps.getQuantitativeReference().getFlow().getName());
		setFlowProperty(pps.getQuantitativeReference().getFlow().getReferenceFlowProperty().getName());
		setUnit(pps.getQuantitativeReference().getUnit().getName());
		setAmount(pps.getQuantitativeReference().getAmountValue());
	}	

	
	@Override
	public String toString() {
		String toString = "[Id: " + getId() + ", Name: " + getName() + ", Category: " + getCategory() + "]";
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

	public void setCategory(String category) {		
		this.category = category;		
	}

	public void setQuantitativeReference(String quantitativeReference) {
		this.quantitativeReference = quantitativeReference;		
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

	public String getCategory() {
		return category;
	}

	public String getQuantitativeReference() {
		return quantitativeReference;
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

}
