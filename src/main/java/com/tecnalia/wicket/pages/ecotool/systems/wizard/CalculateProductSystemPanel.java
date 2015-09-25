package com.tecnalia.wicket.pages.ecotool.systems.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
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
import org.openlca.core.database.BaseDao;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.ProductSystemDao;
import org.openlca.core.model.Exchange;
import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.Process;
import org.openlca.core.model.ProductSystem;
import org.openlca.core.model.Unit;
import org.openlca.core.model.UnitGroup;
import org.openlca.core.model.descriptors.Descriptors;
import org.openlca.core.model.descriptors.ImpactMethodDescriptor;
import org.openlca.core.model.descriptors.NwSetDescriptor;
import org.openlca.core.model.descriptors.ProcessDescriptor;

import com.google.inject.Inject;
import com.tecnalia.lca.app.viewers.combo.ImpactMethodDescriptorsModel;
import com.tecnalia.lca.app.viewers.combo.NwSetDescriptorsModel;
import com.tecnalia.lca.app.viewers.combo.ProcessDescriptorsModel;
import com.tecnalia.wicket.pages.ecotool.processes.editor.ExchangeDescriptor;
import com.tecnalia.wicket.pages.ecotool.processes.editor.ProcessEdit;
import com.tecnalia.wicket.pages.ecotool.processes.wizard.UnitChoiceRenderer;
import com.tecnalia.wicket.pages.ecotool.processes.wizard.model.UnitDescriptor;
import com.tecnalia.wicket.pages.ecotool.results.analysis.AnalyzeEditor;
import com.tecnalia.wicket.pages.ecotool.systems.ProductSystemDescriptor;
import com.tecnalia.wicket.pages.ecotool.systems.ProductSystemEditor;

public class CalculateProductSystemPanel extends Panel {

	@Inject
	private IDatabase database;
	
	private static final long serialVersionUID = 1L;
	
	// Get logger
	private static final Logger logger = Logger.getLogger(CalculateProductSystemPanel.class);
	
	
	private FeedbackPanel feedbackPanel;
	private PageReference pageReference;
	private ModalWindow calculateProductSystemModalWindow;

	/**
	 * Form for collecting input.
	 */
	private class CalculateProductSystemForm extends Form<ImpactFormModel>
	{

		private static final long serialVersionUID = 1L;

		public CalculateProductSystemForm(String name) {
			super(name, new CompoundPropertyModel<ImpactFormModel>(new ImpactFormModel()));
			
			DropDownChoice<ImpactMethodDescriptor> ddcImpactMethod = new DropDownChoice<ImpactMethodDescriptor>(
					"impactMethod", new ImpactMethodDescriptorsModel(), new ImpactMethodChoiceRenderer());
			ddcImpactMethod.setRequired(true);				
			add(ddcImpactMethod);
			
			DropDownChoice<NwSetDescriptor> ddcNwSet = new DropDownChoice<NwSetDescriptor>(
					"nwSet", new NwSetDescriptorsModel(getModelObject()), new NwSetChoiceRenderer());
			ddcNwSet.setRequired(false);
			ddcNwSet.setOutputMarkupId(true);
			add(ddcNwSet);
			
			// Add a behavior to the impact method selection combo to update the nwSet combo
			ddcImpactMethod.add(new AjaxFormComponentUpdatingBehavior("change")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{										
					// Render the ddcNwSet combo					
					target.add(ddcNwSet);					
				}
			});
			
			AjaxButton saveButton = new AjaxButton("saveButton") {

				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {					
					calculateProductSystemModalWindow.close(target);				
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
					calculateProductSystemModalWindow.close(target);				
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
			logger.debug("Impact method selected: " + getModelObject().getImpactMethod().getName());
			
			// TODO Open a new page with the descriptors and calculate the product system

			ProductSystemDescriptor ProductSystemDescriptor = ((ProductSystemEditor) pageReference.getPage()).getProductSystemDescriptor();
			
			setResponsePage(new AnalyzeEditor(ProductSystemDescriptor, getModelObject()));
			
			//ProductSystem productSystem = database.createDao(ProductSystem.class).getForId(ProductSystemDescriptor.getId());
			//ImpactMethodDescriptor impactMethodDescriptor = getModelObject().getImpactMethod();
			//NwSetDescriptor nwSetDescriptor = getModelObject().getNwSet();
			
			// Set the product system submitted field in the product system editor page
			//if (pageReference != null) ((ProductSystemEditor) pageReference.getPage()).setProductSystemSubmitted(true);			
		}
		
	}
	
	public CalculateProductSystemPanel(String id, ModalWindow calculateProductSystemModalWindow, PageReference pageReference) {
		super(id);
		Injector.get().inject(this);
		
		this.calculateProductSystemModalWindow = calculateProductSystemModalWindow;
		this.pageReference = pageReference;
		
		// Add a label to the panel
		add(new Label("instructionLabel", "Select the impact calculation method and normalization/weighting options"));
		
		// Construct form and feedback panel and hook them up
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		feedback.setOutputMarkupId(true);
		add(feedback);
		this.feedbackPanel = feedback;
		
		// Add the form to the panel
		add(new CalculateProductSystemForm("calculateProductSystemForm"));
	}

}
