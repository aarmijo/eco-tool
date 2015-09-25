package com.tecnalia.wicket.pages.ecotool.systems;

import org.apache.log4j.Logger;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.tecnalia.wicket.pages.ecotool.systems.wizard.CalculateProductSystemPanel;

public class CalculateActionPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	// Get logger
	private static final Logger logger = Logger.getLogger(CalculateActionPanel.class);

	/**
	 * @param id
	 *            component id
	 * @param model
	 *            model for ProductSystemDescriptor
	 * @param calculateProductSystemModalWindow 
	 *            modal window
	 * @param pageReference
	 *            page reference to the calling page	
	 */
	public CalculateActionPanel(String id, IModel<ProductSystemDescriptor> model, ModalWindow calculateProductSystemModalWindow, PageReference pageReference)
	{
		super(id, model);
		
		add(new AjaxLink<Void>("calculate")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				((ProductSystemEditor) pageReference.getPage()).setProductSystemDescriptor(model.getObject());
				calculateProductSystemModalWindow.setTitle("Calculate product system: " + model.getObject().getName());
				calculateProductSystemModalWindow.setContent(new CalculateProductSystemPanel(calculateProductSystemModalWindow.getContentId(), calculateProductSystemModalWindow, pageReference));
				calculateProductSystemModalWindow.show(target);
				logger.debug("Clicked: " + model.getObject().getName());				
			}
		});
	}

}
