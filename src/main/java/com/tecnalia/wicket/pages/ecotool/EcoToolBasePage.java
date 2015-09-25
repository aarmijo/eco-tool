package com.tecnalia.wicket.pages.ecotool;

import org.apache.wicket.markup.html.WebPage;
import org.openlca.core.database.ProcessDao;

public abstract class EcoToolBasePage extends WebPage {

	private static final long serialVersionUID = 1L;

	public EcoToolSession getEcoToolSession() {
		return (EcoToolSession) getSession();
	}

	public ProcessDao getProcessDao() {
		return getEcoToolSession().getProcessDao();
	}	

}
