package com.tecnalia.wicket.pages.ecotool.systems.wizard;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.openlca.core.model.descriptors.ProcessDescriptor;

public class ProcessChoiceRenderer implements
		IChoiceRenderer<ProcessDescriptor> {

	private static final long serialVersionUID = 1L;

	@Override
	public Object getDisplayValue(ProcessDescriptor object) {		
		return object.getName();
	}

	@Override
	public String getIdValue(ProcessDescriptor object, int index) {
		return String.valueOf(object.getId());		
	}

}
