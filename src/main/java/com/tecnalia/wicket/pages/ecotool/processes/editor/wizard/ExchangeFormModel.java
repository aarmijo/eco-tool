package com.tecnalia.wicket.pages.ecotool.processes.editor.wizard;

import org.apache.wicket.util.io.IClusterable;
import com.tecnalia.wicket.pages.ecotool.processes.wizard.model.UnitDescriptor;

public class ExchangeFormModel implements IClusterable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String category;
	private String flowProperty;
	private UnitDescriptor unit;
	private Double amountValue = 1.0d;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the flowProperty
	 */
	public String getFlowProperty() {
		return flowProperty;
	}
	/**
	 * @param flowProperty the flowProperty to set
	 */
	public void setFlowProperty(String flowProperty) {
		this.flowProperty = flowProperty;
	}
	/**
	 * @return the unit
	 */
	public UnitDescriptor getUnit() {
		return unit;
	}
	/**
	 * @param unit the unit to set
	 */
	public void setUnit(UnitDescriptor unit) {
		this.unit = unit;
	}
	/**
	 * @return the amountValue
	 */
	public Double getAmountValue() {
		return amountValue;
	}
	/**
	 * @param amountValue the amountValue to set
	 */
	public void setAmountValue(Double amountValue) {
		this.amountValue = amountValue;
	}	
}