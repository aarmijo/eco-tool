package com.tecnalia.wicket.pages.ecotool.systems.wizard;

import org.apache.wicket.util.io.IClusterable;
import org.openlca.core.model.descriptors.ImpactMethodDescriptor;
import org.openlca.core.model.descriptors.NwSetDescriptor;

public class ImpactFormModel implements IClusterable {

	private static final long serialVersionUID = 1L;
	
	private ImpactMethodDescriptor impactMethod;
	private NwSetDescriptor nwSet;
	
	public ImpactMethodDescriptor getImpactMethod() {
		return impactMethod;
	}
	
	public void setImpactMethod(ImpactMethodDescriptor impactMethod) {
		this.impactMethod = impactMethod;
	}
	
	public NwSetDescriptor getNwSet() {
		return nwSet;
	}
	
	public void setNwSet(NwSetDescriptor nwSet) {
		this.nwSet = nwSet;
	}
	
}