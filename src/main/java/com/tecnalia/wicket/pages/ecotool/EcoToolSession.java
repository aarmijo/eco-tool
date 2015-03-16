package com.tecnalia.wicket.pages.ecotool;

import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.openlca.core.database.derby.DerbyDatabase;

import com.tecnalia.lca.app.db.DerbyConfiguration;

public class EcoToolSession extends WebSession {
	
	private static final long serialVersionUID = 1L;

	private Cart cart = new Cart();
	
	public Cart getCart() {
		return cart;
	}
	
	// Eco-tool database
	private DerbyDatabase derbyDatabase = null;

	public DerbyDatabase getDerbyDatabase() {
		return derbyDatabase;
	}

	public EcoToolSession(Request request) {
		super(request);
		// Set the database in the session scope
		try {
			DerbyConfiguration derbyConfiguration = new DerbyConfiguration();
			derbyConfiguration.setName("ProSEco_LCA");					
			this.derbyDatabase = derbyConfiguration.createInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
