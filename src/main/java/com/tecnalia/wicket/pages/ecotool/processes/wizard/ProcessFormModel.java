package com.tecnalia.wicket.pages.ecotool.processes.wizard;

import org.apache.wicket.util.io.IClusterable;
import org.openlca.core.model.descriptors.FlowPropertyDescriptor;

import com.tecnalia.wicket.pages.ecotool.processes.wizard.model.UnitDescriptor;

public class ProcessFormModel implements IClusterable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;
	private String category;
	private FlowPropertyDescriptor flowProperty;
	private UnitDescriptor unit;
	private Double amountValue = 1.0d; 
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public FlowPropertyDescriptor getFlowProperty() {
		return flowProperty;
	}
	public void setFlowProperty(FlowPropertyDescriptor flowProperty) {
		this.flowProperty = flowProperty;
	}
	public UnitDescriptor getUnit() {
		return unit;
	}
	public void setUnit(UnitDescriptor unit) {
		this.unit = unit;
	}
	public Double getAmountValue() {
		return amountValue;
	}
	public void setAmountValue(Double amountValue) {
		this.amountValue = amountValue;
	}	
}