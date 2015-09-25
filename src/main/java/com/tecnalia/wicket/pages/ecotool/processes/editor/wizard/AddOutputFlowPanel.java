package com.tecnalia.wicket.pages.ecotool.processes.editor.wizard;

import org.apache.log4j.Logger;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.openlca.core.database.BaseDao;
import org.openlca.core.database.FlowDao;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.Exchange;
import org.openlca.core.model.Flow;
import org.openlca.core.model.Process;
import org.openlca.core.model.descriptors.FlowDescriptor;
import com.google.inject.Inject;
import com.tecnalia.lca.app.viewers.combo.FlowDescriptorsModel;
import com.tecnalia.wicket.pages.ecotool.processes.ProcessDescriptor;
import com.tecnalia.wicket.pages.ecotool.processes.editor.ProcessEdit;

public class AddOutputFlowPanel extends Panel {

	@Inject
	private IDatabase database;
	
	private static final long serialVersionUID = 1L;
	
	// Get logger
	private static final Logger logger = Logger.getLogger(AddOutputFlowPanel.class);
	
	private FeedbackPanel feedbackPanel;
	private ModalWindow addInputFlowModalWindow;
	private PageReference pageReference;

	/**
	 * Form for collecting input.
	 */
	private class FlowForm extends Form<FlowFormModel>
	{

		private static final long serialVersionUID = 1L;

		public FlowForm(String name) {
			super(name, new CompoundPropertyModel<FlowFormModel>(new FlowFormModel()));
			
			add(new DropDownChoice<FlowDescriptor>("flow", new FlowDescriptorsModel(), new FlowChoiceRenderer()).setRequired(true));
			
			AjaxButton saveButton = new AjaxButton("saveButton") {

				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {					
					addInputFlowModalWindow.close(target);
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
					addInputFlowModalWindow.close(target);
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
			
			ProcessDescriptor processDescriptor = ((ProcessEdit) pageReference.getPage()).getProcessDescriptor();
			long processId = processDescriptor.getId();
			BaseDao<Process> processBaseDao = database.createDao(Process.class);			
			Process process = processBaseDao.getForId(processId);
			
			Exchange exchange = new Exchange();
			FlowDao flowDao = new FlowDao(database);
			Flow flow = flowDao.getForId(getModelObject().getFlow().getId());
			exchange.setFlow(flow);
			exchange.setFlowPropertyFactor(flow.getReferenceFactor());
			exchange.setUnit(flow.getReferenceFactor().getFlowProperty()
					.getUnitGroup().getReferenceUnit());
			exchange.setAmountValue(1.0);
			exchange.setInput(false);
			process.getExchanges().add(exchange);
			// Persist the process with the added flow
			processBaseDao.update(process);
			logger.debug("Output flow added to product: " + processDescriptor.getName());	

		}
		
	}
	
	public AddOutputFlowPanel(String id, ModalWindow addInputFlowModalWindow, PageReference pageReference) {
		super(id);
		Injector.get().inject(this);
		
		this.addInputFlowModalWindow = addInputFlowModalWindow;
		this.pageReference = pageReference;
		
		// Add a label to the panel
		add(new Label("instructionLabel", "Select a process (flow) as input"));
		
		// Construct form and feedback panel and hook them up
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		feedback.setOutputMarkupId(true);
		add(feedback);
		this.feedbackPanel = feedback;
		
		// Add the form to the panel
		add(new FlowForm("flowForm"));
	}

}
