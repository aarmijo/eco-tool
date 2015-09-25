package com.tecnalia.wicket.pages.ecotool.processes;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.tecnalia.wicket.pages.ecotool.processes.editor.ProcessEdit;

public class EditActionPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	// Get logger
	private static final Logger logger = Logger.getLogger(EditActionPanel.class);

	/**
	 * @param id
	 *            component id
	 * @param model
	 *            model for ProcessDescriptor
	 */
	public EditActionPanel(String id, IModel<ProcessDescriptor> model)
	{
		super(id, model);
		
		add(new AjaxLink<Void>("edit")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				setResponsePage(new ProcessEdit(model.getObject()));
				logger.debug("Clicked: " + model.getObject().getName());				
			}
		});
	}

}
