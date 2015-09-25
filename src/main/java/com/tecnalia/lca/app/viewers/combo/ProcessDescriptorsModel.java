package com.tecnalia.lca.app.viewers.combo;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.ProcessDao;
import org.openlca.core.model.descriptors.ProcessDescriptor;

import com.google.inject.Inject;

public class ProcessDescriptorsModel extends LoadableDetachableModel<List<ProcessDescriptor>> {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private IDatabase database;
	
	public ProcessDescriptorsModel() {
		Injector.get().inject(this); 
	}

	@Override
	protected List<ProcessDescriptor> load() {		
		List<ProcessDescriptor> processes = new ProcessDao(database).getDescriptors();
		Collections.sort(processes);
		return processes;
	}

}