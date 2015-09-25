package com.tecnalia.wicket.pages.ecotool.systems.wizard;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.openlca.core.model.descriptors.ImpactMethodDescriptor;

public class ImpactMethodChoiceRenderer implements
		IChoiceRenderer<ImpactMethodDescriptor> {

	private static final long serialVersionUID = 1L;

	@Override
	public Object getDisplayValue(ImpactMethodDescriptor object) {		
		return object.getName();
	}

	@Override
	public String getIdValue(ImpactMethodDescriptor object, int index) {		
		return String.valueOf(object.getId());
	}

}
