package com.tecnalia.wicket.pages.ecotool.processes.editor;

import org.apache.log4j.Logger;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.tecnalia.wicket.pages.ecotool.processes.editor.wizard.EditFlowPanel;

public class InputsEditActionPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	// Get logger
	private static final Logger logger = Logger.getLogger(InputsEditActionPanel.class);

	/**
	 * @param id
	 *            component id
	 * @param model
	 *            model for ExchangeDescriptor
	 * @param editFlowModalWindow 
	 *            modal window
	 * @param pageReference
	 *            page reference to the calling page	
	 */
	public InputsEditActionPanel(String id, IModel<ExchangeDescriptor> model, ModalWindow editFlowModalWindow, PageReference pageReference)
	{
		super(id, model);
		
		add(new AjaxLink<Void>("edit")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				((ProcessEdit) pageReference.getPage()).setExchangeDescriptor(model.getObject());
				editFlowModalWindow.setTitle("Edit process flow: " + model.getObject().getName());
				editFlowModalWindow.setContent(new EditFlowPanel(editFlowModalWindow.getContentId(), editFlowModalWindow, pageReference));
				editFlowModalWindow.show(target);
				logger.debug("Clicked: " + model.getObject().getName());				
			}
		});
	}

}
