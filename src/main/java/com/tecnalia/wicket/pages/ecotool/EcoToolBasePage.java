package com.tecnalia.wicket.pages.ecotool;

import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.openlca.core.database.derby.DerbyDatabase;

public abstract class EcoToolBasePage extends WebPage {

	private static final long serialVersionUID = 1L;

	public EcoToolSession getEcoToolSession() {
		return (EcoToolSession) getSession();
	}

	public Cart getCart() {
		return getEcoToolSession().getCart();
	}

	public List<Cheese> getCheeses() {
		return EcoToolApplication.get().getCheeses();
	}

	public DerbyDatabase getDatabase() {
		return getEcoToolSession().getDerbyDatabase();
	}

}
