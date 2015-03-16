package com.tecnalia.wicket;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebPage;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;

public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;

	public HomePage(final PageParameters parameters) {
		super(parameters);

		add(new Label("version", getApplication().getFrameworkSettings().getVersion()));
		
		add(new Label("label_1", getApplication().getName()));
		add(new DateTextField("default"));

		// TODO Add your page's components here

    }
}
