package com.tecnalia.lca.app.viewers.combo;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.NwSetDao;
import org.openlca.core.model.descriptors.NwSetDescriptor;

import com.google.inject.Inject;
import com.tecnalia.wicket.pages.ecotool.systems.wizard.ImpactFormModel;

public class NwSetDescriptorsModel extends LoadableDetachableModel<List<NwSetDescriptor>> {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private IDatabase database;

	private ImpactFormModel impactFormModel;
	
	public NwSetDescriptorsModel(ImpactFormModel impactFormModel) {
		Injector.get().inject(this);
		
		this.impactFormModel = impactFormModel;
	}

	@Override
	protected List<NwSetDescriptor> load() {
		if (impactFormModel.getImpactMethod() == null) {
			return Collections.emptyList();
		}
		NwSetDao nwSetDao = new NwSetDao(database);
		List<NwSetDescriptor> nwSetDescriptors = nwSetDao.getDescriptorsForMethod(impactFormModel.getImpactMethod().getId());
		Collections.sort(nwSetDescriptors);		
		return nwSetDescriptors;
	}

}