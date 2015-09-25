package com.tecnalia.lca.app.viewers.combo;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.openlca.core.database.FlowPropertyDao;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.descriptors.FlowPropertyDescriptor;

import com.google.inject.Inject;

public class FlowPropertyDescriptorsModel extends LoadableDetachableModel<List<FlowPropertyDescriptor>> {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private IDatabase database;
	
	public FlowPropertyDescriptorsModel() {
		Injector.get().inject(this); 
	}

	@Override
	protected List<FlowPropertyDescriptor> load() {		
		List<FlowPropertyDescriptor> properties = new FlowPropertyDao(database).getDescriptors();
		Collections.sort(properties);
		return properties;
	}

}