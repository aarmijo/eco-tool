package com.tecnalia.wicket.pages.ecotool.processes;

import org.apache.log4j.Logger;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class DeleteActionPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	// Get logger
	private static final Logger logger = Logger.getLogger(DeleteActionPanel.class);

	/**
	 * @param id
	 *            component id
	 * @param model
	 *            model for ProcessDescriptor
	 * @param yesNoModalWindow
	 *            modal window
	 * @param pageReference 
	 *            reference to the calling page
	 */
	public DeleteActionPanel(String id, IModel<ProcessDescriptor> model, ModalWindow yesNoModalWindow, PageReference pageReference)
	{
		super(id, model);
		add(new AjaxLink<Void>("delete")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				((ProcessEditor) pageReference.getPage()).setProcessDescriptor(model.getObject());				
				yesNoModalWindow.setContent(new YesNoModalPanel(yesNoModalWindow.getContentId(), yesNoModalWindow, pageReference, model.getObject().getName()));
				yesNoModalWindow.show(target);
				logger.debug("Clicked: " + model.getObject().getName());				
			}
		});
	}

}
