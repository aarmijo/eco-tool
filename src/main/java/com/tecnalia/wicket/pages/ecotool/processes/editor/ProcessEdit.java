package com.tecnalia.wicket.pages.ecotool.processes.editor;

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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.StringValidator;
import org.openlca.core.database.BaseDao;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.Exchange;
import org.openlca.core.model.FlowType;
import org.openlca.core.model.Process;

import com.google.inject.Inject;
import com.tecnalia.wicket.pages.ecotool.EcoToolBasePage;
import com.tecnalia.wicket.pages.ecotool.processes.ProcessDescriptor;
import com.tecnalia.wicket.pages.ecotool.processes.ProcessEditor;
import com.tecnalia.wicket.pages.ecotool.processes.editor.wizard.AddInputFlowPanel;
import com.tecnalia.wicket.pages.ecotool.processes.editor.wizard.AddOutputFlowPanel;
import com.tecnalia.wicket.pages.ecotool.processes.wizard.ProcessFormModel;
import com.tecnalia.wicket.pages.ecotool.systems.ProductSystemEditor;
import com.tecnalia.wicket.pages.ecotool.HomePage;

/**
 * Process edit
 * 
 * @author Alberto Armijo
 */
@SuppressWarnings("serial")
public class ProcessEdit extends EcoToolBasePage {

	@Inject
	private IDatabase database;

	// Get logger
	private static final Logger logger = Logger.getLogger(ProcessEdit.class);
	
	private static final long serialVersionUID = 1L;
	private FeedbackPanel feedbackPanel;

	// Selected process
	private ProcessDescriptor processDescriptor;
	// Selected Exchange and flow
	private ExchangeDescriptor exchangeDescriptor;

	/**
	 * Constructor.
	 * @param processDescriptor 
	 */
	
	public ProcessEdit(ProcessDescriptor processDescriptor) {
		
		this.processDescriptor = processDescriptor;
		
		// Navigation links
		add(new BookmarkablePageLink<Void>("homeLink", HomePage.class));
		add(new BookmarkablePageLink<Void>("processesLink", ProcessEditor.class));
		add(new BookmarkablePageLink<Void>("productSystemsLink", ProductSystemEditor.class));
		
		// Add a new input flow modal window
		final ModalWindow addInputFlowModalWindow;
		add(addInputFlowModalWindow = new ModalWindow("addInputFlowModalWindow"));
		addInputFlowModalWindow.setTitle("Add input process flow");
		addInputFlowModalWindow.setContent(new AddInputFlowPanel(addInputFlowModalWindow.getContentId(), addInputFlowModalWindow, getPageReference()));
		// Add Create a new Process button
		Form<?> createInputFlowForm;
		add(createInputFlowForm = new Form<Void>("addInputFlow"));
        // add a button that can be used to submit the form via ajax
		createInputFlowForm.add(new AjaxButton("addInputFlowButton", createInputFlowForm)
        {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
            	addInputFlowModalWindow.show(target);
            }
        }.setDefaultFormProcessing(false));
		
		// Add a new output flow modal window
		final ModalWindow addOutputFlowModalWindow;
		add(addOutputFlowModalWindow = new ModalWindow("addOutputFlowModalWindow"));
		addOutputFlowModalWindow.setTitle("Add output process flow");
		addOutputFlowModalWindow.setContent(new AddOutputFlowPanel(addOutputFlowModalWindow.getContentId(), addOutputFlowModalWindow, getPageReference()));
		// Add Create a new Process button
		Form<?> createOutputFlowForm;
		add(createOutputFlowForm = new Form<Void>("addOutputFlow"));
        // add a button that can be used to submit the form via ajax
		createOutputFlowForm.add(new AjaxButton("addOutputFlowButton", createOutputFlowForm)
        {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
            	addOutputFlowModalWindow.show(target);
            }
        }.setDefaultFormProcessing(false));	
		
		
		// Add the edit process flow modal window
		final ModalWindow editFlowModalWindow;
		add(editFlowModalWindow = new ModalWindow("editFlowModalWindow"));		
		
		// Add a label for the body title
        Label processNameLabel = new Label("processNameLabel", processDescriptor.getName());
        processNameLabel.setEscapeModelStrings(false);
        add(processNameLabel);
        
		// Construct form and feedback panel and hook them up
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		feedback.setOutputMarkupId(true);
		add(feedback);
		this.feedbackPanel = feedback;
		
		// Add the form to the page
		ProcessFormModel processFormModel = new ProcessFormModel();
		processFormModel.setName(processDescriptor.getName());
		processFormModel.setDescription(processDescriptor.getDescription());
		add(new ProcessForm("processForm", processFormModel));
		
		// Add the process input flows			
		List<IColumn<ExchangeDescriptor, String>> inputExchangeTableColumns = new ArrayList<IColumn<ExchangeDescriptor, String>>();		
		inputExchangeTableColumns.add(new PropertyColumn<ExchangeDescriptor, String>(Model.of("Name"), "name", "name"));
		inputExchangeTableColumns.add(new PropertyColumn<ExchangeDescriptor, String>(Model.of("Category"), "category", "category"));
		inputExchangeTableColumns.add(new PropertyColumn<ExchangeDescriptor, String>(Model.of("Flow Property"), "flowProperty"));
		inputExchangeTableColumns.add(new PropertyColumn<ExchangeDescriptor, String>(Model.of("Unit"), "unit"));
		inputExchangeTableColumns.add(new PropertyColumn<ExchangeDescriptor, String>(Model.of("Amount"), "amountValue"));
		inputExchangeTableColumns.add(new AbstractColumn<ExchangeDescriptor, String>(new Model<String>("Edit"))
		{
			@Override
			public void populateItem(Item<ICellPopulator<ExchangeDescriptor>> cellItem, String componentId,
				IModel<ExchangeDescriptor> model)
			{
				cellItem.add(new InputsEditActionPanel(componentId, model, editFlowModalWindow, getPageReference()));
			}			
		});
		
		// Ajax version of the DataTable
		AjaxFallbackDefaultDataTable<ExchangeDescriptor, String> inputsDataTable = new AjaxFallbackDefaultDataTable<ExchangeDescriptor, String>("inputsTable", inputExchangeTableColumns, new InputExchangeDescriptorsProvider(), 8);
		// Add dataTable to a WebMarkupContainer so that the component can be refreshed
		final WebMarkupContainer inputsDataTableContainer = new WebMarkupContainer("inputs");
		inputsDataTableContainer.setOutputMarkupId(true);
		inputsDataTableContainer.add(inputsDataTable);
		add(inputsDataTableContainer);
		
		// Add the process output flows
		List<IColumn<ExchangeDescriptor, String>> outputExchangeTableColumns = new ArrayList<IColumn<ExchangeDescriptor, String>>();		
		outputExchangeTableColumns.add(new PropertyColumn<ExchangeDescriptor, String>(Model.of("Name"), "name", "name"));
		outputExchangeTableColumns.add(new PropertyColumn<ExchangeDescriptor, String>(Model.of("Category"), "category", "category"));
		outputExchangeTableColumns.add(new PropertyColumn<ExchangeDescriptor, String>(Model.of("Flow Property"), "flowProperty"));
		outputExchangeTableColumns.add(new PropertyColumn<ExchangeDescriptor, String>(Model.of("Unit"), "unit"));
		outputExchangeTableColumns.add(new PropertyColumn<ExchangeDescriptor, String>(Model.of("Amount"), "amountValue"));
		outputExchangeTableColumns.add(new AbstractColumn<ExchangeDescriptor, String>(new Model<String>("Edit"))
		{
			@Override
			public void populateItem(Item<ICellPopulator<ExchangeDescriptor>> cellItem, String componentId,
				IModel<ExchangeDescriptor> model)
			{
				cellItem.add(new OutputsEditActionPanel(componentId, model, editFlowModalWindow, getPageReference()));
			}			
		});
		
		
		// Ajax version of the DataTable
		AjaxFallbackDefaultDataTable<ExchangeDescriptor, String> outputsDataTable = new AjaxFallbackDefaultDataTable<ExchangeDescriptor, String>("outputsTable", outputExchangeTableColumns, new OutputExchangeDescriptorsProvider(), 8);
		// Add dataTable to a WebMarkupContainer so that the component can be refreshed
		final WebMarkupContainer outputsDataTableContainer = new WebMarkupContainer("outputs");
		outputsDataTableContainer.setOutputMarkupId(true);
		outputsDataTableContainer.add(outputsDataTable);
		add(outputsDataTableContainer);
		
		// Edit flow ModalWindow callbacks
		editFlowModalWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
		{
			@Override
			public void onClose(AjaxRequestTarget target)
			{
				target.add(inputsDataTableContainer);
				target.add(outputsDataTableContainer);	
			}
		});
		
		//Add input flow ModalWindow callbacks
		addInputFlowModalWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
		{
			@Override
			public void onClose(AjaxRequestTarget target)
			{
				target.add(inputsDataTableContainer);
			}
		});
		
		//Add output flow ModalWindow callbacks
		addOutputFlowModalWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
		{
			@Override
			public void onClose(AjaxRequestTarget target)
			{
				target.add(outputsDataTableContainer);
			}
		});
		
	}
	
	private class InputExchangeDescriptorsProvider extends SortableDataProvider<ExchangeDescriptor, String> {		
				
		public InputExchangeDescriptorsProvider() {
			// set default sort			
			setSort("name", SortOrder.ASCENDING);
		}
		
		@Override
		public Iterator<? extends ExchangeDescriptor> iterator(long first,
				long count) {	
			
			// Retrieve the exchange list
			Process process = database.createDao(Process.class).getForId(processDescriptor.getId());
			ExchangeList exchangeList = new ExchangeList(process, true);
			List<ExchangeDescriptor> exchangeDescriptorList = exchangeList.getExchangeList();
			
			List<ExchangeDescriptor> data = new ArrayList<ExchangeDescriptor>(exchangeDescriptorList);
			
			Collections.sort(data, new Comparator<ExchangeDescriptor>() {

				public int compare(ExchangeDescriptor o1, ExchangeDescriptor o2) {
					
					int dir = getSort().isAscending() ? 1 : -1;

					if ("name".equals(getSort().getProperty())) {
						return dir * (o1.getName().compareTo(o2.getName()));
					} else if ("category".equals(getSort().getProperty())) {
						return dir * (o1.getCategory().compareTo(o2.getCategory()));
					}
					return 0;
				}
			});
			
			return data.subList((int)first, (int)Math.min(first + count, data.size())).iterator();
		}

		@Override
		public long size() {			
			long size = 0;
			Process process = database.createDao(Process.class).getForId(processDescriptor.getId());
			for (Exchange exchange : process.getExchanges()) {
				if (exchange.isInput() && exchange.getFlow().getFlowType().equals(FlowType.PRODUCT_FLOW)) size++;
			}			
			return size;
		}

		@Override
		public IModel<ExchangeDescriptor> model(ExchangeDescriptor object) {			
			return Model.of(object);
		}

	}
	
	private class OutputExchangeDescriptorsProvider extends SortableDataProvider<ExchangeDescriptor, String> {		
		
		public OutputExchangeDescriptorsProvider() {			
			// set default sort			
			setSort("name", SortOrder.ASCENDING);
		}
		
		@Override
		public Iterator<? extends ExchangeDescriptor> iterator(long first,
				long count) {	
			
			// Retrieve the exchange list
			Process process = database.createDao(Process.class).getForId(processDescriptor.getId());
			ExchangeList exchangeList = new ExchangeList(process, false);
			List<ExchangeDescriptor> exchangeDescriptorList = exchangeList.getExchangeList();
			
			List<ExchangeDescriptor> data = new ArrayList<ExchangeDescriptor>(exchangeDescriptorList);
			
			Collections.sort(data, new Comparator<ExchangeDescriptor>() {

				public int compare(ExchangeDescriptor o1, ExchangeDescriptor o2) {
					
					int dir = getSort().isAscending() ? 1 : -1;

					if ("name".equals(getSort().getProperty())) {
						return dir * (o1.getName().compareTo(o2.getName()));
					} else if ("category".equals(getSort().getProperty())) {
						return dir * (o1.getCategory().compareTo(o2.getCategory()));
					}
					return 0;
				}
			});
			
			return data.subList((int)first, (int)Math.min(first + count, data.size())).iterator();
		}

		@Override
		public long size() {			
			long size = 0;
			Process process = database.createDao(Process.class).getForId(processDescriptor.getId());
			for (Exchange exchange : process.getExchanges()) {
				if (!exchange.isInput() && exchange.getFlow().getFlowType().equals(FlowType.PRODUCT_FLOW)) size++;
			}			
			return size;
		}

		@Override
		public IModel<ExchangeDescriptor> model(ExchangeDescriptor object) {			
			return Model.of(object);
		}

	}
	
	/**
	 * Form for collecting input.
	 */
	private class ProcessForm extends Form<ProcessFormModel>
	{

		private static final long serialVersionUID = 1L;

		public ProcessForm(String name, ProcessFormModel processFormModel) {
			super(name, new CompoundPropertyModel<ProcessFormModel>(processFormModel));
			
			FormComponent<String> fc = new TextField<String>("name").setRequired(true);
			fc.add(new StringValidator(4, null));			
			add(fc);
			
			add(new TextArea<String>("description"));			
			
			AjaxButton saveButton = new AjaxButton("saveButton") {

				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					// update the process					
					BaseDao<Process> processBaseDao = database.createDao(Process.class);
					Process process = processBaseDao.getForId(processDescriptor.getId());
					process.setName(((ProcessFormModel) form.getModelObject()).getName());
					process.setDescription(((ProcessFormModel) form.getModelObject()).getDescription());
					processBaseDao.update(process);
					logger.debug("Process " + process.getName() + " was updated!");
					target.appendJavaScript("alert('The process " + ((ProcessFormModel) form.getModelObject()).getName() + " was successfully updated!');");
				}
				
	            @Override
	            protected void onError(AjaxRequestTarget target, Form<?> form)
	            {
	                // repaint the feedback panel so errors are shown
	                target.add(feedbackPanel);
	            }
				
			};
			add(saveButton);
			
		}	
		
	}

		
	/**
	 * @return the exchangeDescriptor
	 */
	public ExchangeDescriptor getExchangeDescriptor() {
		return exchangeDescriptor;
	}

	/**
	 * @param exchangeDescriptor the exchangeDescriptor to set
	 */
	public void setExchangeDescriptor(ExchangeDescriptor exchangeDescriptor) {
		this.exchangeDescriptor = exchangeDescriptor;
	}

	/**
	 * @return the processDescriptor
	 */
	public ProcessDescriptor getProcessDescriptor() {
		return processDescriptor;
	}

	/**
	 * @param processDescriptor the processDescriptor to set
	 */
	public void setProcessDescriptor(ProcessDescriptor processDescriptor) {
		this.processDescriptor = processDescriptor;
	}
	
}