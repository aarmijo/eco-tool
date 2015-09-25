package com.tecnalia.lca.app.viewers.combo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.openlca.core.database.FlowPropertyDao;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.Unit;
import org.openlca.core.model.UnitGroup;

import com.google.inject.Inject;
import com.tecnalia.wicket.pages.ecotool.processes.wizard.ProcessFormModel;
import com.tecnalia.wicket.pages.ecotool.processes.wizard.model.UnitDescriptor;

public class UnitDescriptorsModel extends LoadableDetachableModel<List<UnitDescriptor>> {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private IDatabase database;

	private ProcessFormModel processFormModel;
	
	public UnitDescriptorsModel(ProcessFormModel processFormModel) {
		Injector.get().inject(this);
		
		this.processFormModel = processFormModel;
	}

	@Override
	protected List<UnitDescriptor> load() {
		
		if (processFormModel.getFlowProperty() == null)
		{
			return Collections.emptyList();
		}
		
		long flowPropertyId = processFormModel.getFlowProperty().getId();		
		FlowProperty property = new FlowPropertyDao(database).getForId(flowPropertyId);		
		UnitGroup unitGroup = property.getUnitGroup();
		List<Unit> unitList = unitGroup.getUnits();
				
		List<UnitDescriptor> unitDescriptorList = new ArrayList<>();
		for (Unit unit : unitList){
			unitDescriptorList.add(new UnitDescriptor(unit.getId(), unit.getName()));
		}		
		return unitDescriptorList;
	}
}