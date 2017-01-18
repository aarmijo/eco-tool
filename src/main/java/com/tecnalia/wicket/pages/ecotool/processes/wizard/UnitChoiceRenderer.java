package com.tecnalia.wicket.pages.ecotool.processes.wizard;

import java.util.List;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import com.tecnalia.wicket.pages.ecotool.processes.wizard.model.UnitDescriptor;

public class UnitChoiceRenderer implements
		IChoiceRenderer<UnitDescriptor> {

	private static final long serialVersionUID = 1L;

	@Override
	public Object getDisplayValue(UnitDescriptor object) {		
		return object.getUnitName();
	}

	@Override
	public String getIdValue(UnitDescriptor object, int index) {
		return String.valueOf(object.getId());		
	}

	@Override
	public UnitDescriptor getObject(String id, IModel<? extends List<? extends UnitDescriptor>> choices) {
		return choices.getObject().get(0);
	}

}
