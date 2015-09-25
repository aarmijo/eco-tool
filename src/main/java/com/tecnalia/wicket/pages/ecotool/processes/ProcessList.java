package com.tecnalia.wicket.pages.ecotool.processes;

import java.util.ArrayList;
import java.util.List;

import org.openlca.core.database.ProcessDao;
import org.openlca.core.model.Process;

public class ProcessList {

	private List<ProcessDescriptor> processList;

	public ProcessList () {		
	}

	public ProcessList(ProcessDao processDao) {
		List<ProcessDescriptor> processDescriptorList = new ArrayList<ProcessDescriptor>();	
		
		List<Process> processList = processDao.getAll();
		for (Process process : processList) {
			ProcessDescriptor processDescriptor = new ProcessDescriptor(process);			
			processDescriptorList.add(processDescriptor);
		}
			this.processList = processDescriptorList;
	}

	public List<ProcessDescriptor> getProcessList() {
		return processList;
	}
}
