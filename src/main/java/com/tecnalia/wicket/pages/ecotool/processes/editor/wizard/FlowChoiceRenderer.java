package com.tecnalia.wicket.pages.ecotool.processes.editor.wizard;

import java.util.List;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.openlca.core.model.descriptors.FlowDescriptor;

public class FlowChoiceRenderer implements IChoiceRenderer<FlowDescriptor> {

	private static final long serialVersionUID = 1L;

	@Override
	public Object getDisplayValue(FlowDescriptor object) {		
		return object.getName();
	}

	@Override
	public String getIdValue(FlowDescriptor object, int index) {
		return String.valueOf(index);		
	}

	@Override
	public FlowDescriptor getObject(String id, IModel<? extends List<? extends FlowDescriptor>> choices) {		
		return choices.getObject().get(Integer.parseInt(id));
	}

}
