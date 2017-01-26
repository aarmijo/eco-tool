package com.tecnalia.lca.app.viewers.combo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.openlca.core.database.FlowDao;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.FlowType;
import org.openlca.core.model.descriptors.FlowDescriptor;

import com.google.inject.Inject;

public class FlowDescriptorsModel extends LoadableDetachableModel<List<FlowDescriptor>> {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private IDatabase database;
	
	public FlowDescriptorsModel() {
		Injector.get().inject(this); 
	}

	@Override
	protected List<FlowDescriptor> load() {		
		List<FlowDescriptor> flowDescriptors = new FlowDao(database).getDescriptors();
		List<FlowDescriptor> filteredFlowDescriptors = new ArrayList<>();
		for (FlowDescriptor flowDescriptor : flowDescriptors) {
			if (flowDescriptor.getFlowType().equals(FlowType.PRODUCT_FLOW)) filteredFlowDescriptors.add(flowDescriptor);
		}
		Collections.sort(filteredFlowDescriptors);
		//flowDescriptors = flowDescriptors.stream().filter(f -> f.getFlowType().equals(FlowType.PRODUCT_FLOW)).sorted().collect(Collectors.toList());		
		return filteredFlowDescriptors;
	}

}