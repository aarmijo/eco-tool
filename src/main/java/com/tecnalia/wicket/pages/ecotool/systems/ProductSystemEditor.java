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
package com.tecnalia.wicket.pages.ecotool.systems;

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
import org.openlca.core.database.ProductSystemDao;
import org.openlca.core.model.ProductSystem;
import org.wicketstuff.annotation.mount.MountPath;

import pt.uninova.proseco.tools.pes.ontology.EcoAndOptimisationConfiguration;
import pt.uninova.proseco.tools.pes.ontology.utils.ConfigurationSerializer;
import pt.uninova.proseco.tools.pes.ontology.utils.ConfigurationsUtilities;
import pt.uninova.proseco.tools.pes.ontology.utils.KMBConfigsVocabulary;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.tecnalia.lca.app.db.Cache;
import com.tecnalia.wicket.pages.ecotool.EcoToolBasePage;
import com.tecnalia.wicket.pages.ecotool.processes.ProcessEditor;
import com.tecnalia.wicket.pages.ecotool.systems.wizard.CreateProductSystemPanel;
import com.tecnalia.wicket.pages.ecotool.HomePage;

/**
 * Product system editor
 * 
 * @author Alberto Armijo
 */
@SuppressWarnings("serial")
@MountPath(value = "/product-system-editor")
public class ProductSystemEditor extends EcoToolBasePage {

	@Inject
	private IDatabase database;
	
	// Get logger
	private static final Logger logger = Logger.getLogger(ProductSystemEditor.class);
	
	// Yes / no modal panel yes selected
	private boolean yesNoModalResult = false;
	
	// Form submitted in the create product system panel
	private boolean productSystemSubmitted = false;
	
	// Selected product system
	protected ProductSystemDescriptor productSystemDescriptor;
	
	// Saved product system name
	private String savedProductSystemName;
	
	/**
	 * Constructor.
	 */
	
	public ProductSystemEditor() {
		
		// Navigation links
		add(new BookmarkablePageLink<Void>("homeLink", HomePage.class));
		add(new BookmarkablePageLink<Void>("processesLink", ProcessEditor.class));
		add(new BookmarkablePageLink<Void>("productSystemsLink", ProductSystemEditor.class));
		
		// Add the Create a new product system modal window
		final ModalWindow createProductSystemModalWindow;
		add(createProductSystemModalWindow = new ModalWindow("createProductSystemModalWindow"));
		createProductSystemModalWindow.setTitle("New product system");
		createProductSystemModalWindow.setContent(new CreateProductSystemPanel(createProductSystemModalWindow.getContentId(), createProductSystemModalWindow, getPageReference()));
		// Add Create a new product system button
		Form<?> createProductSystemForm;
		add(createProductSystemForm = new Form<Void>("createProductSystem"));
        // add a button that can be used to submit the form via ajax
		createProductSystemForm.add(new AjaxButton("createProductSystemButton", createProductSystemForm)
        {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
            	createProductSystemModalWindow.show(target);
            }
        }.setDefaultFormProcessing(false));
		
		
		// Add Save PES configuration button
		Form<?> saveConfigurationForm;
		add(saveConfigurationForm = new Form<Void>("saveConfiguration"));
        // add a button that can be used to submit the form via ajax
		saveConfigurationForm.add(new AjaxButton("saveConfigurationButton", saveConfigurationForm)
        {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {            	
            	
            	// Retrieve the PES_ID from the session
            	String pesId = getEcoToolSession().getPesId();
            	
            	// Create the configuration content in JSON format
            	List<ProductSystem> productSystems = (new ProductSystemDao(database)).getAll();
            	List<ProductSystemDescriptor> productSystemDescriptors = new ArrayList<>();  	
            	for (ProductSystem productSystem : productSystems) {    		
            		productSystemDescriptors.add(new ProductSystemDescriptor(productSystem));
            	}
            	Gson gson = new Gson();
            	String jsonConfiguration = gson.toJson(productSystemDescriptors);
            	
            	// Create the configuration
            	EcoAndOptimisationConfiguration ecoConfig = new EcoAndOptimisationConfiguration();
            	ecoConfig.setId(ConfigurationsUtilities.CreateConfigurationIdForDeployableService());
                // Set the configuration text
            	ecoConfig.setJsonConfiguration(jsonConfiguration);
                String ecoConfigSerialized = ConfigurationSerializer.setConfigurationToJson(ecoConfig);
                String quotesDoubled = ecoConfigSerialized.replace("\\\"", "\\\\\\\\\"");
                quotesDoubled = quotesDoubled.replace("\"","\"\"");
                quotesDoubled = quotesDoubled.replace("\\r","");
                quotesDoubled = quotesDoubled.replace("\\n","");    
                
                // Create the configuration object and link JSon. You need to generate a unique ID,
                // or reuse previous if you are overwriting the conf.
                String dmId = pesId + System.currentTimeMillis();
                
                ConfigurationsUtilities.WriteDeployableConfiguration(pesId, dmId, quotesDoubled,
                KMBConfigsVocabulary.EcoAndOptimisation, EcoAndOptimisationConfiguration.class.getSimpleName());               
                
                target.appendJavaScript("alert('The configuration object " + pesId + " was saved in the KMB!');");
            	
            	// Test reading from the KMB
//                KMBApi api = new KMBApi();
//                String configIdFromKMB = api.readElementPropertyValue(pesId, KMBConfigsVocabulary.EcoAndOptimisation.getSearchName());
//                JsonParser jsonParser = new JsonParser();
//                JsonObject jsonElement = (JsonObject) jsonParser.parse(configIdFromKMB);
//                String configIdFromJson = jsonElement.get(KMBConfigsVocabulary.EcoAndOptimisation.getSearchName()).getAsString();                
//                String ConfigStringfromkmb = api.readElementConfiguration(configIdFromJson);
//
//                PESConfiguration configFromKmb = ConfigurationSerializer.newPESConfigurationFromJson(ConfigStringfromkmb);
//                EcoAndOptimisationConfiguration finalConfig = ConfigurationSerializer.getConfigurationFromJson(configFromKmb);
//                jsonConfiguration = finalConfig.getJsonConfiguration();
//                gson = new Gson();
//                List<ProductSystemDescriptor> list = gson.fromJson(jsonConfiguration, new TypeToken<List<ProductSystemDescriptor>>(){}.getType());
//                System.out.println(list);

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
		
		// Add the edit product system modal window
		final ModalWindow editProductSystemModalWindow;
		add(editProductSystemModalWindow = new ModalWindow("editProductSystemModalWindow"));
		
		// Add the calculate product system modal window
		final ModalWindow calculateProductSystemModalWindow;		
		add(calculateProductSystemModalWindow = new ModalWindow("calculateProductSystemModalWindow"));
		calculateProductSystemModalWindow.showUnloadConfirmation(false);
		
		// Add the columns for the AjaxFallbackDefaultDataTable		
		List<IColumn<ProductSystemDescriptor, String>> columns = new ArrayList<IColumn<ProductSystemDescriptor, String>>();
		
		columns.add(new PropertyColumn<ProductSystemDescriptor, String>(Model.of("Name"), "name", "name"));		
		columns.add(new PropertyColumn<ProductSystemDescriptor, String>(Model.of("Description"), "description", "description"));
		columns.add(new PropertyColumn<ProductSystemDescriptor, String>(Model.of("Reference Product"), "referenceProductName"));
		columns.add(new PropertyColumn<ProductSystemDescriptor, String>(Model.of("Flow property"), "flowProperty"));
		columns.add(new PropertyColumn<ProductSystemDescriptor, String>(Model.of("Unit"), "unit"));
		columns.add(new PropertyColumn<ProductSystemDescriptor, String>(Model.of("Target amount"), "amountValue"));
		
		columns.add(new AbstractColumn<ProductSystemDescriptor, String>(new Model<String>("Edit"))
		{
			@Override
			public void populateItem(Item<ICellPopulator<ProductSystemDescriptor>> cellItem, String componentId,
				IModel<ProductSystemDescriptor> model)
			{
				cellItem.add(new EditActionPanel(componentId, model, editProductSystemModalWindow, getPageReference()));
			}			
		});
						
		columns.add(new AbstractColumn<ProductSystemDescriptor, String>(new Model<String>("Delete"))
		{
			@Override
			public void populateItem(Item<ICellPopulator<ProductSystemDescriptor>> cellItem, String componentId,
				IModel<ProductSystemDescriptor> model)
			{
				cellItem.add(new DeleteActionPanel(componentId, model, yesNoModalWindow, getPageReference()));
			}			
		});
		
		columns.add(new AbstractColumn<ProductSystemDescriptor, String>(new Model<String>("Calculate"))
		{
			@Override
			public void populateItem(Item<ICellPopulator<ProductSystemDescriptor>> cellItem, String componentId,
				IModel<ProductSystemDescriptor> model)
			{
				cellItem.add(new CalculateActionPanel(componentId, model, calculateProductSystemModalWindow, getPageReference()));
			}			
		});
		
		// Ajax version of the DataTable
		AjaxFallbackDefaultDataTable<ProductSystemDescriptor, String> dataTable = new AjaxFallbackDefaultDataTable<ProductSystemDescriptor, String>("productSystemDescriptorsTable", columns, new ProductSystemDescriptorsProvider(), 8);		
				
		// Add dataTable to a WebMarkupContainer so that the component can be refreshed
		final WebMarkupContainer dataTableContainer = new WebMarkupContainer("productSystemDescriptors");
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
					
					ProductSystemDao productSystemDao = new ProductSystemDao(database);
					ProductSystem productSystem = productSystemDao.getForId(productSystemDescriptor.getId());					
					// Delete the product system from the database
					productSystemDao.delete(productSystem);
					Cache.evict(productSystemDao.getDescriptor(productSystemDescriptor.getId()));					
					logger.debug("Product system deleted!");
					target.appendJavaScript("alert('The product system " + productSystemDescriptor.getName() + " was successfully deleted!');");					
					
					// Refresh the dataTableContainer component
					logger.debug("Refreshing dataTableContainer component!");				
					target.add(dataTableContainer);
					
					// Set the yes / no result
					setYesNoModalResult(false);
				}
			}
		});
				
		// Create Product System ModalWindow callbacks
		createProductSystemModalWindow.setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
		{
			@Override
			public boolean onCloseButtonClicked(AjaxRequestTarget target)
			{
				logger.debug("Close button in the Create product system modal window clicked!");
				return true;
			}
		});
		
		createProductSystemModalWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
		{
			@Override
			public void onClose(AjaxRequestTarget target)
			{
				// Check if the form in the modal window is submitted to refresh the data table
				if (isProductSystemSubmitted()) {
					logger.debug("Refreshing dataTableContainer component!");				
					target.add(dataTableContainer);
					target.appendJavaScript("alert('The product system " + savedProductSystemName + " was successfully saved!');");	
					// Reset the submission status
					setProductSystemSubmitted(false);					
				}
			}
		});
		
		// Edit Product System ModalWindow callbacks
		editProductSystemModalWindow.setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
		{
			@Override
			public boolean onCloseButtonClicked(AjaxRequestTarget target)
			{
				logger.debug("Close button in the Edit product system modal window clicked!");
				return true;
			}
		});
		
		editProductSystemModalWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
		{
			@Override
			public void onClose(AjaxRequestTarget target)
			{
				// Check if the form in the modal window is submitted to refresh the data table
				if (isProductSystemSubmitted()) {
					logger.debug("Refreshing dataTableContainer component!");				
					target.add(dataTableContainer);
					target.appendJavaScript("alert('The product system " + savedProductSystemName + " was successfully updated!');");	
					// Reset the submission status
					setProductSystemSubmitted(false);					
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
	 * @param id set the selected Product System
	 */
	public void setProductSystemDescriptor(ProductSystemDescriptor productSystemDescriptor) {
		this.productSystemDescriptor = productSystemDescriptor;		
	}
	
	/**
	 * @return the selected productSystemDescriptor
	 */
	public ProductSystemDescriptor getProductSystemDescriptor() {
		return productSystemDescriptor;
	}

	
	/**
	 * @return the submission status from the create a product system panel
	 */
	public boolean isProductSystemSubmitted() {
		return productSystemSubmitted;
	}

	/**
	 * @param productSystemSubmitted the submission status from the create a product system panel form
	 */
	public void setProductSystemSubmitted(boolean productSystemSubmitted) {
		this.productSystemSubmitted = productSystemSubmitted;
	}
	
	/**
	 * @param savedProductSystemName the saved process name to set
	 */
	public void setSavedProductSystemName(String savedProductSystemName) {
		this.savedProductSystemName = savedProductSystemName;
	}

	private class ProductSystemDescriptorsProvider extends SortableDataProvider<ProductSystemDescriptor, String> {		
		
		public ProductSystemDescriptorsProvider() {			
			// set default sort			
			setSort("name", SortOrder.ASCENDING);
		}
		
		@Override
		public Iterator<? extends ProductSystemDescriptor> iterator(long first,
				long count) {	
			
			// Retrieve the process list
			ProductSystemList productSystemList = new ProductSystemList(new ProductSystemDao(database));
			List<ProductSystemDescriptor> productSystemDescriptorList = productSystemList.getProductSystemList();
			
			List<ProductSystemDescriptor> data = new ArrayList<ProductSystemDescriptor>(productSystemDescriptorList);
			
			Collections.sort(data, new Comparator<ProductSystemDescriptor>() {

				public int compare(ProductSystemDescriptor o1, ProductSystemDescriptor o2) {
					
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
			return (new ProductSystemDao(database)).getAll().size();
		}

		@Override
		public IModel<ProductSystemDescriptor> model(ProductSystemDescriptor object) {			
			return Model.of(object);
		}

	}
}