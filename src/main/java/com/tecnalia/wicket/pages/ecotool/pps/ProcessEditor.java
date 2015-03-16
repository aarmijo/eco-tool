/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tecnalia.wicket.pages.ecotool.pps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.openlca.core.database.derby.DerbyDatabase;
import org.wicketstuff.annotation.mount.MountPath;

import com.tecnalia.wicket.pages.ecotool.EcoToolBasePage;


/**
 * Product, Process, Service (PPS) editor
 * 
 * @author Alberto Armijo
 */
@SuppressWarnings("serial")
@MountPath(value = "/pps-editor")
public class ProcessEditor extends EcoToolBasePage {

	// Derby database connection
	protected static DerbyDatabase database;	
	
	List<ProcessDescriptor> processDescriptorList;
	
	/**
	 * Constructor.
	 */
	
	public ProcessEditor() {
		
		// Get the database connection from the session
		database = getDatabase();
		
		ProcessList processList = new ProcessList(database);
		this.processDescriptorList = processList.getProcessList();
				
		// Add the columns for the AjaxFallbackDefaultDataTable		
		List<IColumn<ProcessDescriptor, String>> columns = new ArrayList<IColumn<ProcessDescriptor, String>>();
		
		columns.add(new PropertyColumn<ProcessDescriptor, String>(Model.of("Name"), "name", "name"));
		
		columns.add(new PropertyColumn<ProcessDescriptor, String>(Model.of("Description"), "description", "description"));
		
		// Ajax version of the DataTable
		AjaxFallbackDefaultDataTable<ProcessDescriptor, String> dataTable = new AjaxFallbackDefaultDataTable<ProcessDescriptor, String>("processDescriptors", columns, new ProcessDescriptorsProvider(), 8);
		add(dataTable);
		
		// Add feedback panel		
		info("Panel info:" + processDescriptorList.toString());		
		FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
		add(feedbackPanel);		
		
	}
	
	private class ProcessDescriptorsProvider extends SortableDataProvider<ProcessDescriptor, String> {
		
		
		public ProcessDescriptorsProvider() {			
			// set default sort			
			setSort("name", SortOrder.ASCENDING);
		}
		
		@Override
		public Iterator<? extends ProcessDescriptor> iterator(long first,
				long count) {	
			
			List<ProcessDescriptor> data = new ArrayList<ProcessDescriptor>(processDescriptorList);
			
			Collections.sort(data, new Comparator<ProcessDescriptor>() {

				public int compare(ProcessDescriptor o1, ProcessDescriptor o2) {
					
					int dir = getSort().isAscending() ? 1 : -1;

					if ("name".equals(getSort().getProperty())) {
						return dir * (o1.getName().compareTo(o2.getName()));
					} else {
						return dir * (o1.getDescription().compareTo(o2.getDescription()));
					}
				}
			});
			
			return data.subList((int)first, (int)Math.min(first + count, data.size())).iterator();
		}

		@Override
		public long size() {			
			return processDescriptorList.size();
		}

		@Override
		public IModel<ProcessDescriptor> model(ProcessDescriptor object) {			
			return Model.of(object);
		}

	}
}
