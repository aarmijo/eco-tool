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
package com.tecnalia.wicket.pages.ecotool.processes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.ProcessDao;
import org.openlca.core.model.Process;
import org.wicketstuff.annotation.mount.MountPath;

import com.google.inject.Inject;
import com.tecnalia.lca.app.db.Cache;
import com.tecnalia.wicket.pages.ecotool.EcoToolBasePage;
import com.tecnalia.wicket.pages.ecotool.processes.wizard.CreateProcessPanel;
import com.tecnalia.wicket.pages.ecotool.systems.ProductSystemEditor;
import com.tecnalia.wicket.pages.ecotool.HomePage;

/**
 * Process editor
 * 
 * @author Alberto Armijo
 */
@SuppressWarnings("serial")
@MountPath(value = "/process-editor")
public class ProcessEditor extends EcoToolBasePage {

	@Inject
	private IDatabase database;
	
	// Get logger
	private static final Logger logger = Logger.getLogger(ProcessEditor.class);
	
	// Yes / no modal panel yes selected
	private boolean yesNoModalResult = false;
	
	// Form submitted in the create process panel
	private boolean processSubmitted = false;
	
	// Selected process
	protected ProcessDescriptor processDescriptor;
	
	// Saved process name
	private String savedProcessName;
	
	/**
	 * Constructor.
	 */
	
	public ProcessEditor() {
		
		// Navigation links
		add(new BookmarkablePageLink<Void>("homeLink", HomePage.class));
		add(new BookmarkablePageLink<Void>("processesLink", ProcessEditor.class));
		add(new BookmarkablePageLink<Void>("productSystemsLink", ProductSystemEditor.class));
		
		// Add the Create a new process modal window
		final ModalWindow createProcessModalWindow;
		add(createProcessModalWindow = new ModalWindow("createProcessModalWindow"));
		createProcessModalWindow.setTitle("New process");
		createProcessModalWindow.setContent(new CreateProcessPanel(createProcessModalWindow.getContentId(), createProcessModalWindow, getPageReference()));
		// Add Create a new Process button
		Form<?> createProcessForm;
		add(createProcessForm = new Form<Void>("createProcess"));
        // add a button that can be used to submit the form via ajax
		createProcessForm.add(new AjaxButton("createProcessButton", createProcessForm)
        {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
            	createProcessModalWindow.show(target);
            }
        }.setDefaultFormProcessing(false));		
						
		// Add the yes/no modal window
		final ModalWindow yesNoModalWindow;
		add(yesNoModalWindow = new ModalWindow("yesNoModalWindow"));		
		yesNoModalWindow.setTitle("Alert");
		yesNoModalWindow.setCookieName("yesNoModal");
		yesNoModalWindow.setResizable(false);
		yesNoModalWindow.setInitialWidth(30);
		yesNoModalWindow.setInitialHeight(15);
		yesNoModalWindow.setWidthUnit("em");
		yesNoModalWindow.setHeightUnit("em");
		yesNoModalWindow.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		
		// Add the columns for the AjaxFallbackDefaultDataTable		
		List<IColumn<ProcessDescriptor, String>> columns = new ArrayList<IColumn<ProcessDescriptor, String>>();
		
		columns.add(new PropertyColumn<ProcessDescriptor, String>(Model.of("Name"), "name", "name"));		
		columns.add(new PropertyColumn<ProcessDescriptor, String>(Model.of("Description"), "description", "description"));
		columns.add(new PropertyColumn<ProcessDescriptor, String>(Model.of("Category"), "category"));
		columns.add(new AbstractColumn<ProcessDescriptor, String>(new Model<String>("Edit"))
		{
			@Override
			public void populateItem(Item<ICellPopulator<ProcessDescriptor>> cellItem, String componentId,
				IModel<ProcessDescriptor> model)
			{
				cellItem.add(new EditActionPanel(componentId, model));
			}			
		});
		columns.add(new AbstractColumn<ProcessDescriptor, String>(new Model<String>("Delete"))
		{
			@Override
			public void populateItem(Item<ICellPopulator<ProcessDescriptor>> cellItem, String componentId,
				IModel<ProcessDescriptor> model)
			{
				cellItem.add(new DeleteActionPanel(componentId, model, yesNoModalWindow, getPageReference()));
			}			
		});
		
		// Ajax version of the DataTable
		AjaxFallbackDefaultDataTable<ProcessDescriptor, String> dataTable = new AjaxFallbackDefaultDataTable<ProcessDescriptor, String>("processDescriptorsTable", columns, new ProcessDescriptorsProvider(), 8);		
				
		// Add dataTable to a WebMarkupContainer so that the component can be refreshed
		final WebMarkupContainer dataTableContainer = new WebMarkupContainer("processDescriptors");
		dataTableContainer.setOutputMarkupId(true);
		dataTableContainer.add(dataTable);
		add(dataTableContainer);
		
		// ModalWindow callbacks
		// Yes/no ModalWindow callbacks
		yesNoModalWindow.setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
		{
			@Override
			public boolean onCloseButtonClicked(AjaxRequestTarget target)
			{
				// Set the yes / no result
				setYesNoModalResult(false);
				return true;
			}
		});

		yesNoModalWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
		{
			@Override
			public void onClose(AjaxRequestTarget target)
			{
				// Delete the Process element
				if (isYesNoModalResult()) {
					ProcessDao processDao = new ProcessDao(database);
					Process process = processDao.getForId(processDescriptor.getId());					
					// Delete the process from the database
					processDao.delete(process);
					Cache.evict(processDao.getDescriptor(processDescriptor.getId()));					
					logger.debug("Process deleted!");
					target.appendJavaScript("alert('The process " + processDescriptor.getName() + " was successfully deleted!');");					
					
					// Refresh the dataTableContainer component
					logger.debug("Refreshing dataTableContainer component!");				
					target.add(dataTableContainer);
					//setResponsePage(ProcessEditor.class);
					
					// Set the yes / no result
					setYesNoModalResult(false);
				}
			}
		});
		
		// CreateProcess ModalWindow callbacks
		createProcessModalWindow.setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
		{
			@Override
			public boolean onCloseButtonClicked(AjaxRequestTarget target)
			{
				logger.debug("Close button in the Create Process modal window clicked!");
				return true;
			}
		});
		
		createProcessModalWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
		{
			@Override
			public void onClose(AjaxRequestTarget target)
			{
				// Check if the form in the modal window is submitted to refresh the data table
				if (isProcessSubmitted()) {
					logger.debug("Refreshing dataTableContainer component!");				
					target.add(dataTableContainer);
					target.appendJavaScript("alert('The process " + savedProcessName + " was successfully saved!');");	
					// Reset the submission status
					setProcessSubmitted(false);					
				}
			}
		});
		
	}

	/**
	 * @return the yes/no modal panel result
	 */
	public boolean isYesNoModalResult() {
		return yesNoModalResult;
	}

	/**
	 * @param yesNoModalResult the yes/no modal panel result to set
	 */
	public void setYesNoModalResult(boolean yesNoModalResult) {
		this.yesNoModalResult = yesNoModalResult;
	}
	
	/**
	 * @param id set the selected Process
	 */
	public void setProcessDescriptor(ProcessDescriptor processDescriptor) {
		this.processDescriptor = processDescriptor;		
	}

	
	/**
	 * @return the submission status from the create process panel
	 */
	public boolean isProcessSubmitted() {
		return processSubmitted;
	}

	/**
	 * @param processSubmitted the submission status from the create process panel form
	 */
	public void setProcessSubmitted(boolean processSubmitted) {
		this.processSubmitted = processSubmitted;
	}
	
	/**
	 * @param savedProcessName the saved process name to set
	 */
	public void setSavedProcessName(String savedProcessName) {
		this.savedProcessName = savedProcessName;
	}

	private class ProcessDescriptorsProvider extends SortableDataProvider<ProcessDescriptor, String> {		
		
		public ProcessDescriptorsProvider() {			
			// set default sort			
			setSort("name", SortOrder.ASCENDING);
		}
		
		@Override
		public Iterator<? extends ProcessDescriptor> iterator(long first,
				long count) {	
			
			// Retrieve the process list
			ProcessList processList = new ProcessList(new ProcessDao(database));//new ProcessList(getProcessDao());
			List<ProcessDescriptor> processDescriptorList = processList.getProcessList();
			
			List<ProcessDescriptor> data = new ArrayList<ProcessDescriptor>(processDescriptorList);
			
			Collections.sort(data, new Comparator<ProcessDescriptor>() {

				public int compare(ProcessDescriptor o1, ProcessDescriptor o2) {
					
					int dir = getSort().isAscending() ? 1 : -1;

					if ("name".equals(getSort().getProperty())) {
						return dir * (o1.getName().compareTo(o2.getName()));
					} else if ("description".equals(getSort().getProperty())) {
						return dir * (o1.getDescription().compareTo(o2.getDescription()));
					}
					return 0;
				}
			});
			
			return data.subList((int)first, (int)Math.min(first + count, data.size())).iterator();
		}

		@Override
		public long size() {			
			return (new ProcessDao(database)).getAll().size();
		}

		@Override
		public IModel<ProcessDescriptor> model(ProcessDescriptor object) {			
			return Model.of(object);
		}

	}
}