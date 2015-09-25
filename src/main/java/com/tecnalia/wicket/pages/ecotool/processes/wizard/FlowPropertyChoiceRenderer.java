package com.tecnalia.wicket.pages.ecotool.processes.wizard;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.openlca.core.model.descriptors.FlowPropertyDescriptor;

public class FlowPropertyChoiceRenderer implements
		IChoiceRenderer<FlowPropertyDescriptor> {

	private static final long serialVersionUID = 1L;

	@Override
	public Object getDisplayValue(FlowPropertyDescriptor object) {		
		return object.getName();
	}

	@Override
	public String getIdValue(FlowPropertyDescriptor object, int index) {
		return String.valueOf(object.getId());		
	}

}
