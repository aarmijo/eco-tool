package com.tecnalia.wicket.pages.ecotool.systems.wizard;

import java.util.List;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.openlca.core.model.descriptors.NwSetDescriptor;

public class NwSetChoiceRenderer implements IChoiceRenderer<NwSetDescriptor> {

	private static final long serialVersionUID = 1L;

	@Override
	public Object getDisplayValue(NwSetDescriptor object) {		
		return object.getName();
	}

	@Override
	public String getIdValue(NwSetDescriptor object, int index) {		
		return String.valueOf(index);
	}

	@Override
	public NwSetDescriptor getObject(String id, IModel<? extends List<? extends NwSetDescriptor>> choices) {
		return choices.getObject().get(Integer.parseInt(id));
	}

}
