package com.tecnalia.wicket.pages.cheesr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import com.tecnalia.wicket.WicketApplication;

public class CheesrApplication extends WicketApplication {
	private List<Cheese> cheeses = new ArrayList<Cheese>();

	/**
	 * Constructor
	 */
	public CheesrApplication() {
	}

	@Override
	public void init() {
		super.init();

		//new AnnotatedMountScanner().scanPackage("com.tecnalia.wicket.pages.cheesr").mount(this);

		// read the list of cheeses from a properties file
		readCheeses();
	}

	public static CheesrApplication get() {
		return (CheesrApplication) Application.get();
	}

	@Override
	public Session newSession(Request request, Response response) {
		return new CheesrSession(request);
	}

	/*
	 * Removed the getHomePage() override, as this application does not match
	 * the cheese store 100% to fit the overall examples.
	 */

	public List<Cheese> getCheeses() {
		return Collections.unmodifiableList(cheeses);
	}
	
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return com.tecnalia.wicket.pages.cheesr.Index.class;
	}

	/**
	 * Reads the list of cheeses from a properties file.
	 */
	private void readCheeses() {
		Properties props = new Properties();
		try {
			props.load(CheesrApplication.class
					.getResourceAsStream("cheeses.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Object obj : props.keySet()) {
			String key = obj.toString();

			// only process a cheese once (identified by its name)
			if (!key.endsWith(".name"))
				continue;
			key = key.substring(0, key.indexOf("."));

			// retrieve each property value
			String name = props.getProperty(key + ".name");
			String description = props.getProperty(key + ".description");
			double price = Double.valueOf(props.getProperty(key + ".price"));

			cheeses.add(new Cheese(name, description, price));
		}
	}
}
