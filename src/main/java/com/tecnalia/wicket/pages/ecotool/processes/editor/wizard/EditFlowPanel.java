package com.tecnalia.wicket.pages.ecotool.processes.editor.wizard;

import java.util.ArrayList;
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
import org.openlca.core.database.BaseDao;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.Exchange;
import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.Unit;
import org.openlca.core.model.UnitGroup;

import com.google.inject.Inject;
import com.tecnalia.wicket.pages.ecotool.processes.editor.ExchangeDescriptor;
import com.tecnalia.wicket.pages.ecotool.processes.editor.ProcessEdit;
import com.tecnalia.wicket.pages.ecotool.processes.wizard.UnitChoiceRenderer;
import com.tecnalia.wicket.pages.ecotool.processes.wizard.model.UnitDescriptor;

public class EditFlowPanel extends Panel {

	@Inject
	private IDatabase database;
	
	private static final long serialVersionUID = 1L;
	
	// Get logger
	private static final Logger logger = Logger.getLogger(EditFlowPanel.class);
	
	
	private FeedbackPanel feedbackPanel;
	private PageReference pageReference;
	private ModalWindow editFlowModalWindow;

	/**
	 * Form for collecting input.
	 */
	private class ExchangeForm extends Form<ExchangeFormModel>
	{

		private static final long serialVersionUID = 1L;

		public ExchangeForm(String name, ExchangeFormModel exchangeFormModel) {
			super(name, new CompoundPropertyModel<ExchangeFormModel>(exchangeFormModel));
			
			FormComponent<?> fc;			
			fc = new TextField<String>("name");
			fc.add(AttributeModifier.replace("readonly", "readonly"));		
			add(fc);
			
			fc = new TextField<String>("category");
			fc.add(AttributeModifier.replace("readonly", "readonly"));
			add(fc);
			
			fc = new TextField<String>("flowProperty");
			fc.add(AttributeModifier.replace("readonly", "readonly"));
			add(fc);
						
			DropDownChoice<UnitDescriptor> ddcUnit = new DropDownChoice<UnitDescriptor>(
					"unit",
					new LoadableDetachableModel<List<UnitDescriptor>>() {

						private static final long serialVersionUID = 1L;

						@Override
						protected List<UnitDescriptor> load() {

							long exchangeId = ((ProcessEdit) pageReference.getPage()).getExchangeDescriptor().getId();
							Exchange exchange = database.createDao(Exchange.class).getForId(exchangeId);
							FlowProperty property = exchange.getFlowPropertyFactor().getFlowProperty();
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
					editFlowModalWindow.close(target);
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
					editFlowModalWindow.close(target);
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
			// Save the exchange with the new value and unit
			ExchangeFormModel model = (ExchangeFormModel) getDefaultModelObject();
									
			long exchangeId = ((ProcessEdit) pageReference.getPage()).getExchangeDescriptor().getId();
			BaseDao<Exchange> exchangeDao = database.createDao(Exchange.class);			
			Exchange exchange = exchangeDao.getForId(exchangeId);
			exchange.setAmountValue(model.getAmountValue());
			UnitGroup unitGroup = exchange.getFlow().getReferenceFlowProperty().getUnitGroup();
			exchange.setUnit(unitGroup.getUnit(model.getUnit().getUnitName()));
			// Persist the exchange
			exchangeDao.update(exchange);
			logger.debug("Flow: " + exchange.getFlow().getName() + " was updated!");
		}
		
	}
	
	public EditFlowPanel(String id, ModalWindow editFlowModalWindow, PageReference pageReference) {
		super(id);
		Injector.get().inject(this);
		
		this.editFlowModalWindow = editFlowModalWindow;
		this.pageReference = pageReference;
		
		// Add a label to the panel
		add(new Label("instructionLabel", "Select the process flow unit and value"));
		
		// Construct form and feedback panel and hook them up
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		feedback.setOutputMarkupId(true);
		add(feedback);
		this.feedbackPanel = feedback;
		
		// Add the form to the panel
		ExchangeDescriptor exchangeDescriptor = ((ProcessEdit) pageReference.getPage()).getExchangeDescriptor();
		ExchangeFormModel exchangeFormModel = new ExchangeFormModel();
		exchangeFormModel.setName(exchangeDescriptor.getName());
		exchangeFormModel.setCategory(exchangeDescriptor.getCategory());
		exchangeFormModel.setFlowProperty(exchangeDescriptor.getFlowProperty());
		add(new ExchangeForm("exchangeForm", exchangeFormModel));
	}

}
