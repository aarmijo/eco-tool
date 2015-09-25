package com.tecnalia.wicket.pages.ecotool.processes.editor.wizard;

import org.apache.wicket.util.io.IClusterable;
import org.openlca.core.model.descriptors.FlowDescriptor;

public class FlowFormModel implements IClusterable {

	private static final long serialVersionUID = 1L;
	
	private FlowDescriptor flow;

	/**
	 * @return the flow
	 */
	public FlowDescriptor getFlow() {
		return flow;
	}

	/**
	 * @param flow the flow to set
	 */
	public void setFlow(FlowDescriptor flow) {
		this.flow = flow;
	}
		
}