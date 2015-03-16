package com.tecnalia.wicket.pages.ecotool.pps;

import java.util.ArrayList;
import java.util.List;

import org.openlca.core.database.ProcessDao;
import org.openlca.core.database.derby.DerbyDatabase;
import org.openlca.core.model.Process;

public class ProcessList {

	private List<ProcessDescriptor> processList;

	public ProcessList () {
		
	}

	public ProcessList(DerbyDatabase database) {
		
		List<ProcessDescriptor> processDescriptorList = new ArrayList<ProcessDescriptor>();	
		
		List<Process> ppsList = new ProcessDao(database).getAll();
		for (Process pps : ppsList) {
			ProcessDescriptor processDescriptor = new ProcessDescriptor(pps);			
			processDescriptorList.add(processDescriptor);
		}
			this.processList = processDescriptorList;
	}

	public List<ProcessDescriptor> getProcessList() {
		return processList;
	}
}
