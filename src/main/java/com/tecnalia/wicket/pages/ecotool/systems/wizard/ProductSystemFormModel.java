package com.tecnalia.wicket.pages.ecotool.systems.wizard;

import org.apache.wicket.util.io.IClusterable;
import org.openlca.core.model.descriptors.ProcessDescriptor;

import com.tecnalia.wicket.pages.ecotool.processes.wizard.model.UnitDescriptor;

public class ProductSystemFormModel implements IClusterable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;
	private ProcessDescriptor process;
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
	public ProcessDescriptor getProcess() {
		return process;
	}
	public void setProcess(ProcessDescriptor process) {
		this.process = process;
	}
}