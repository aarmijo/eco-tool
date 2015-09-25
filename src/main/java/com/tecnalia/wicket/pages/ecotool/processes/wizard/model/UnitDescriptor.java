package com.tecnalia.wicket.pages.ecotool.processes.wizard.model;

import java.io.Serializable;

public class UnitDescriptor implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String unitName;

	public UnitDescriptor(long id, String unitName) {
		this.id = id;
		this.unitName = unitName;
	}	
	
	@Override
	public String toString() {		
		return unitName;
	}

	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	
}