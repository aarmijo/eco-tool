package com.tecnalia.lca.app.viewers.combo;

import java.util.Collections;
import java.util.List;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.ImpactMethodDao;
import org.openlca.core.model.descriptors.ImpactMethodDescriptor;

import com.google.inject.Inject;

public class ImpactMethodDescriptorsModel extends LoadableDetachableModel<List<ImpactMethodDescriptor>> {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private IDatabase database;
	
	public ImpactMethodDescriptorsModel() {
		Injector.get().inject(this); 
	}

	@Override
	protected List<ImpactMethodDescriptor> load() {
		ImpactMethodDao impactMethodDao = new ImpactMethodDao(database);
		List<ImpactMethodDescriptor> impactMethodDescriptors = impactMethodDao.getDescriptors();
		Collections.sort(impactMethodDescriptors);
		return impactMethodDescriptors;
	}

}