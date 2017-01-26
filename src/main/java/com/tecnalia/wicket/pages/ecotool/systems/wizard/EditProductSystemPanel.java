package com.tecnalia.wicket.pages.ecotool.systems.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.validation.validator.StringValidator;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.ProductSystemDao;
import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.Process;
import org.openlca.core.model.ProductSystem;
import org.openlca.core.model.Unit;
import org.openlca.core.model.UnitGroup;
import org.openlca.core.model.descriptors.Descriptors;
import org.openlca.core.model.descriptors.ProcessDescriptor;

import com.google.inject.Inject;
import com.tecnalia.lca.app.viewers.combo.ProcessDescriptorsModel;
import com.tecnalia.wicket.pages.ecotool.processes.wizard.UnitChoiceRenderer;
import com.tecnalia.wicket.pages.ecotool.processes.wizard.model.UnitDescriptor;
import com.tecnalia.wicket.pages.ecotool.systems.ProductSystemDescriptor;
import com.tecnalia.wicket.pages.ecotool.systems.ProductSystemEditor;

public class EditProductSystemPanel extends Panel {

	@Inject
	private IDatabase database;
	
	private static final long serialVersionUID = 1L;
	
	// Get logger
	private static final Logger logger = Logger.getLogger(EditProductSystemPanel.class);
	
	
	private FeedbackPanel feedbackPanel;
	private PageReference pageReference;
	private ModalWindow editProductSystemModalWindow;

	/**
	 * Form for collecting input.
	 */
	private class EditProductSystemForm extends Form<ProductSystemFormModel>
	{

		private static final long serialVersionUID = 1L;

		public EditProductSystemForm(String name, ProductSystemFormModel productSystemFormModel) {
			super(name, new CompoundPropertyModel<ProductSystemFormModel>(productSystemFormModel));
			
			FormComponent<?> fc;
			
			fc = new TextField<String>("name").setRequired(true);
			fc.add(new StringValidator(4, null));
			fc.add(AttributeModifier.replace("readonly", "readonly"));	
			add(fc);
			
			DropDownChoice<ProcessDescriptor> ddcProcess = new DropDownChoice<ProcessDescriptor>(
					"process", new ProcessDescriptorsModel(), new ProcessChoiceRenderer());
			ddcProcess.setRequired(false);
			ddcProcess.add(AttributeModifier.replace("disabled", "disabled"));	
			add(ddcProcess);
			
			DropDownChoice<UnitDescriptor> ddcUnit = new DropDownChoice<UnitDescriptor>(
					"unit",
					new LoadableDetachableModel<List<UnitDescriptor>>() {

						private static final long serialVersionUID = 1L;

						@Override
						protected List<UnitDescriptor> load() {

							if (getModelObject().getProcess() == null)
							{
								return Collections.emptyList();
							}
							
							long processId = getModelObject().getProcess().getId();
							Process process = database.createDao(Process.class).getForId(processId);
							FlowProperty property = process.getQuantitativeReference().getFlowPropertyFactor().getFlowProperty();
							UnitGroup unitGroup = property.getUnitGroup();
							List<Unit> unitList = unitGroup.getUnits();

							List<UnitDescriptor> unitDescriptorList = new ArrayList<>();
							for (Unit unit : unitList) {
								unitDescriptorList.add(new UnitDescriptor(unit
										.getId(), unit.getName()));
							}
							return unitDescriptorList;
						}
					}, new UnitChoiceRenderer());
			ddcUnit.setRequired(true);
			ddcUnit.setOutputMarkupId(true);
			add(ddcUnit);
			
			fc = new TextField<Double>("amountValue", Double.class).setRequired(true);
			add(fc);
			
			AjaxButton saveButton = new AjaxButton("saveButton") {

				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {					
					editProductSystemModalWindow.close(target);
				}
				
	            @Override
	            protected void onError(AjaxRequestTarget target, Form<?> form)
	            {
	                // repaint the feedback panel so errors are shown
	                target.add(feedbackPanel);
	            }
				
			};
			add(saveButton);
			
			AjaxButton cancelButton = new AjaxButton("cancelButton") {

				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					editProductSystemModalWindow.close(target);
				}				

			};
			cancelButton.setDefaultFormProcessing(false);
			add(cancelButton);
			
		}


		/**
		 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
		 */
		@Override
		public void onSubmit()
		{
			// Form validation successful. Display message showing edited model.
			logger.debug("Saved model " + ((ProductSystemFormModel) getDefaultModelObject()).getName());
			// Invoke the product system creation controller and save the process
			ProductSystemController.update(getModelObject(), database, pageReference);
			// Set the product system saved name in the process editor page
			if (pageReference != null)
				((ProductSystemEditor) pageReference.getPage())
						.setSavedProductSystemName(((ProductSystemFormModel) getDefaultModelObject())
								.getName());
			// Reset the form model			
			ProductSystemFormModel productSystemFormModel = getModelObject();
			productSystemFormModel.setName("");
			productSystemFormModel.setAmountValue(1.0d);
			
			// Set the product system submitted field in the product system editor page
			if (pageReference != null) ((ProductSystemEditor) pageReference.getPage()).setProductSystemSubmitted(true);
		}
		
	}
	
	public EditProductSystemPanel(String id, ModalWindow editProductSystemModalWindow, PageReference pageReference) {
		super(id);
		Injector.get().inject(this);
		
		this.editProductSystemModalWindow = editProductSystemModalWindow;
		this.pageReference = pageReference;
		
		// Add a label to the panel
		add(new Label("instructionLabel", "Select the product system unit and value"));
		
		// Construct form and feedback panel and hook them up
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		feedback.setOutputMarkupId(true);
		add(feedback);
		this.feedbackPanel = feedback;
		
		// Add the form to the panel
		ProductSystemDescriptor ProductSystemDescriptor = ((ProductSystemEditor) pageReference.getPage()).getProductSystemDescriptor();
		ProductSystemFormModel productSystemFormModel = new ProductSystemFormModel();
		productSystemFormModel.setName(ProductSystemDescriptor.getName());
		
		long productSystemId = ProductSystemDescriptor.getId();
		ProductSystemDao productSystemDao = new ProductSystemDao(database);
		ProductSystem productSystem = productSystemDao.getForId(productSystemId);
		Process process = productSystem.getReferenceProcess();
		ProcessDescriptor processDescriptor = Descriptors.toDescriptor(process);		
		productSystemFormModel.setProcess(processDescriptor);
		add(new EditProductSystemForm("editProductSystemForm", productSystemFormModel));
	}

}
