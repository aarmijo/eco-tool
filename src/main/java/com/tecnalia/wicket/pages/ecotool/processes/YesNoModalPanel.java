package com.tecnalia.wicket.pages.ecotool.processes;

import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

public class YesNoModalPanel extends Panel {

	private static final long serialVersionUID = 1L;

	public YesNoModalPanel(String id, ModalWindow yesNoModal, PageReference pageReference, String processName) {
		super(id);
		
		Form<?> yesNoForm = new Form<Void>("yesNoForm");
		
        MultiLineLabel messageLabel = new MultiLineLabel("message", "The process " + processName + " will be deleted!");
        yesNoForm.add(messageLabel);
        
		AjaxButton yesButton = new AjaxButton("yesButton", yesNoForm) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (pageReference != null) ((ProcessEditor) pageReference.getPage()).setYesNoModalResult(true);
				yesNoModal.close(target);
			}
		};
		yesButton.setDefaultFormProcessing(false);
		
        AjaxButton noButton = new AjaxButton("noButton", yesNoForm) {

			private static final long serialVersionUID = 1L;

			@Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (pageReference != null) ((ProcessEditor) pageReference.getPage()).setYesNoModalResult(false);
				yesNoModal.close(target);
            }
        };
        noButton.setDefaultFormProcessing(false);
        
        yesNoForm.add(yesButton);
        yesNoForm.add(noButton);

        add(yesNoForm);        
	}


}
