package com.tecnalia.wicket.pages.ecotool.processes.wizard;

import java.util.Arrays;
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
import org.apache.wicket.validation.validator.StringValidator;
import org.openlca.core.database.FlowPropertyDao;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.Unit;
import org.openlca.core.model.UnitGroup;
import org.openlca.core.model.descriptors.FlowPropertyDescriptor;

import com.google.inject.Inject;
import com.tecnalia.lca.app.viewers.combo.FlowPropertyDescriptorsModel;
import com.tecnalia.lca.app.viewers.combo.UnitDescriptorsModel;
import com.tecnalia.wicket.pages.ecotool.processes.ProcessEditor;
import com.tecnalia.wicket.pages.ecotool.processes.wizard.model.UnitDescriptor;

public class CreateProcessPanel extends Panel {

	@Inject
	private IDatabase database;
	
	private static final long serialVersionUID = 1L;
	
	// Get logger
	private static final Logger logger = Logger.getLogger(CreateProcessPanel.class);
	
	// Available categories for selection
	private static final List<String> CATEGORIES = Arrays.asList("MANUFACTURE", "USE", "DISTRIBUTION", "EoL", "LC", "OTHER");
	
	private FeedbackPanel feedbackPanel;
	private ModalWindow createProcessModalWindow;
	private PageReference pageReference;

	/**
	 * Form for collecting input.
	 */
	private class ProcessForm extends Form<ProcessFormModel>
	{

		private static final long serialVersionUID = 1L;

		public ProcessForm(String name) {
			super(name, new CompoundPropertyModel<ProcessFormModel>(new ProcessFormModel()));
			
			FormComponent<?> fc;
			
			fc = new TextField<String>("name").setRequired(true);
			fc.add(new StringValidator(4, null));			
			add(fc);
			
			add(new TextArea<String>("description"));
			
			add(new DropDownChoice<String>("category", CATEGORIES).setRequired(true));
			
			DropDownChoice<FlowPropertyDescriptor> ddcFlowProperty = new DropDownChoice<FlowPropertyDescriptor>(
					"flowProperty", new FlowPropertyDescriptorsModel(), new FlowPropertyChoiceRenderer());
			ddcFlowProperty.setRequired(true);
			add(ddcFlowProperty);
			
			DropDownChoice<UnitDescriptor> ddcUnit = new DropDownChoice<UnitDescriptor>(
					"unit", new UnitDescriptorsModel((ProcessFormModel)getDefaultModelObject()), 
					new UnitChoiceRenderer());
			ddcUnit.setRequired(true);
			ddcUnit.setOutputMarkupId(true);
			add(ddcUnit);
			
			fc = new TextField<Double>("amountValue", Double.class).setRequired(true);
			add(fc);
			
			// Add a behavior to the flow property combo to update the unit combo
			ddcFlowProperty.add(new AjaxFormComponentUpdatingBehavior("change")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					
					// Update the form model object to set the default unit
					ProcessFormModel processFormModel = (ProcessFormModel) getDefaultModelObject();
					long flowPropertyId = processFormModel.getFlowProperty().getId();
					FlowProperty property = new FlowPropertyDao(database).getForId(flowPropertyId);		
					UnitGroup unitGroup = property.getUnitGroup();					
					Unit referenceUnit = unitGroup.getReferenceUnit();	
					processFormModel.setUnit(new UnitDescriptor(referenceUnit.getId(), referenceUnit.getName()));
										
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
			logger.debug("Saved model " + ((ProcessFormModel) getDefaultModelObject()).getName());
			// Invoke the process creation controller and save the process
			ProcessCreationController.create((ProcessFormModel) getDefaultModelObject(), database);
			// Set the process saved name in the process editor page
			if (pageReference != null)
				((ProcessEditor) pageReference.getPage())
						.setSavedProcessName(((ProcessFormModel) getDefaultModelObject())
								.getName());
			// Reset the form model			
			ProcessFormModel processFormModel = getModelObject();
			processFormModel.setName("");
			processFormModel.setDescription("");
			processFormModel.setAmountValue(1.0d);
			
			// Set the process submitted field in the process editor page
			if (pageReference != null) ((ProcessEditor) pageReference.getPage()).setProcessSubmitted(true);
		}
		
	}
	
	public CreateProcessPanel(String id, ModalWindow createProcessModalWindow, PageReference pageReference) {
		super(id);
		Injector.get().inject(this);
		
		this.createProcessModalWindow = createProcessModalWindow;
		this.pageReference = pageReference;
		
		// Add a label to the panel
		add(new Label("instructionLabel", "Fill in the form to create a new process"));
		
		// Construct form and feedback panel and hook them up
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		feedback.setOutputMarkupId(true);
		add(feedback);
		this.feedbackPanel = feedback;
		
		// Add the form to the panel
		add(new ProcessForm("processForm"));
	}

}
