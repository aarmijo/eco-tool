package com.tecnalia.wicket.pages.ecotool.systems;

import org.apache.log4j.Logger;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.tecnalia.wicket.pages.ecotool.systems.wizard.EditProductSystemPanel;

public class EditActionPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	// Get logger
	private static final Logger logger = Logger.getLogger(EditActionPanel.class);

	/**
	 * @param id
	 *            component id
	 * @param model
	 *            model for ProductSystemDescriptor
	 * @param editProductSystemModalWindow 
	 *            modal window
	 * @param pageReference
	 *            page reference to the calling page	
	 */
	public EditActionPanel(String id, IModel<ProductSystemDescriptor> model, ModalWindow editProductSystemModalWindow, PageReference pageReference)
	{
		super(id, model);
		
		add(new AjaxLink<Void>("edit")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				((ProductSystemEditor) pageReference.getPage()).setProductSystemDescriptor(model.getObject());
				editProductSystemModalWindow.setTitle("Edit product system: " + model.getObject().getName());
				editProductSystemModalWindow.setContent(new EditProductSystemPanel(editProductSystemModalWindow.getContentId(), editProductSystemModalWindow, pageReference));
				editProductSystemModalWindow.show(target);
				logger.debug("Clicked: " + model.getObject().getName());				
			}
		});
	}

}
