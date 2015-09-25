package com.tecnalia.wicket.pages.ecotool.systems.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.validation.validator.StringValidator;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.Unit;
import org.openlca.core.model.Process;
import org.openlca.core.model.UnitGroup;
import org.openlca.core.model.descriptors.ProcessDescriptor;

import com.google.inject.Inject;
import com.tecnalia.lca.app.viewers.combo.ProcessDescriptorsModel;
import com.tecnalia.wicket.pages.ecotool.processes.wizard.UnitChoiceRenderer;
import com.tecnalia.wicket.pages.ecotool.processes.wizard.model.UnitDescriptor;
import com.tecnalia.wicket.pages.ecotool.systems.ProductSystemEditor;

public class CreateProductSystemPanel extends Panel {

	@Inject
	private IDatabase database;
	
	private static final long serialVersionUID = 1L;
	
	// Get logger
	private static final Logger logger = Logger.getLogger(CreateProductSystemPanel.class);
	
	private FeedbackPanel feedbackPanel;
	private ModalWindow createProcessModalWindow;
	private PageReference pageReference;

	/**
	 * Form for collecting input.
	 */
	private class ProductSystemForm extends Form<ProductSystemFormModel>
	{

		private static final long serialVersionUID = 1L;

		public ProductSystemForm(String name) {
			super(name, new CompoundPropertyModel<ProductSystemFormModel>(new ProductSystemFormModel()));
			
			FormComponent<?> fc;
			
			fc = new TextField<String>("name").setRequired(true);
			fc.add(new StringValidator(4, null));			
			add(fc);
			
			add(new TextArea<String>("description"));
			
			DropDownChoice<ProcessDescriptor> ddcProcess = new DropDownChoice<ProcessDescriptor>(
					"process", new ProcessDescriptorsModel(), new ProcessChoiceRenderer());
			ddcProcess.setRequired(true);
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
			
			// Add a behavior to the process selection combo to update the unit combo
			ddcProcess.add(new AjaxFormComponentUpdatingBehavior("change")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					// Update the form model object to set the default unit
					ProductSystemFormModel productSystemFormModel = getModelObject();
					long processId = productSystemFormModel.getProcess().getId();
					Process process = database.createDao(Process.class).getForId(processId);
					FlowProperty property = process.getQuantitativeReference().getFlowPropertyFactor().getFlowProperty();
					UnitGroup unitGroup = property.getUnitGroup();
					Unit referenceUnit = unitGroup.getReferenceUnit();
					productSystemFormModel.setUnit(new UnitDescriptor(referenceUnit.getId(), referenceUnit.getName()));
					
					// Render the ddcUnit combo					
					target.add(ddcUnit);					
				}
			});
			
			AjaxButton saveButton = new AjaxButton("saveButton") {

				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {					
					createProcessModalWindow.close(target);
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
					createProcessModalWindow.close(target);
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
			ProductSystemController.create(getModelObject(), database, null);
			// Set the product system saved name in the process editor page
			if (pageReference != null)
				((ProductSystemEditor) pageReference.getPage())
						.setSavedProductSystemName(((ProductSystemFormModel) getDefaultModelObject())
								.getName());
			// Reset the form model			
			ProductSystemFormModel productSystemFormModel = getModelObject();
			productSystemFormModel.setName("");
			productSystemFormModel.setDescription("");
			productSystemFormModel.setAmountValue(1.0d);
			
			// Set the process submitted field in the process editor page
			if (pageReference != null) ((ProductSystemEditor) pageReference.getPage()).setProductSystemSubmitted(true);
		}
		
	}
	
	public CreateProductSystemPanel(String id, ModalWindow createProcessModalWindow, PageReference pageReference) {
		super(id);
		Injector.get().inject(this);
		
		this.createProcessModalWindow = createProcessModalWindow;
		this.pageReference = pageReference;
		
		// Add a label to the panel
		add(new Label("instructionLabel", "Fill in the form to create a new product system"));
		
		// Construct form and feedback panel and hook them up
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		feedback.setOutputMarkupId(true);
		add(feedback);
		this.feedbackPanel = feedback;
		
		// Add the form to the panel
		add(new ProductSystemForm("productSystemForm"));
	}

}
