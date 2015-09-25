package com.tecnalia.wicket.pages.ecotool.processes.editor;

import java.io.Serializable;

import org.openlca.core.model.Exchange;

public class ExchangeDescriptor implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String name;
	private String category;
	private String flowProperty;
	private String unit;
	private double amountValue;	

	public ExchangeDescriptor(Exchange exchange) {		
		setId(exchange.getId());
		setName(exchange.getFlow().getName());
				
		if (exchange.getFlow().getCategory() == null) {
			setCategory("N/A");			
		} else {
			setCategory(exchange.getFlow().getCategory().getName());
		}		
		
		if (exchange.getFlowPropertyFactor() == null) {
			setFlowProperty("N/A");			
		} else {
			setFlowProperty(exchange.getFlowPropertyFactor().getFlowProperty().getName());
		}		
		
		setUnit(exchange.getUnit().getName());
		setAmountValue(exchange.getAmountValue());
	}	

	
	@Override
	public String toString() {
		String toString = "[Id: " + getId() + ", Name: " + getName() + ", Category: " + getCategory() + "]";
		return toString;
	}

	public void setName(String name) {
		this.name = name;		
	}

	public void setId(long id) {
		this.id = id;		
	}

	public void setCategory(String category) {		
		this.category = category;		
	}

	public void setFlowProperty(String flowProperty) {
		this.flowProperty = flowProperty;
		
	}

	public void setUnit(String unit) {
		this.unit = unit;
		
	}

	public void setAmountValue(double amountValue) {
		this.amountValue = amountValue;		
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
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