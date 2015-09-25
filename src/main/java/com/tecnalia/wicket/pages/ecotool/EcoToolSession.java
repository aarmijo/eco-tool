package com.tecnalia.wicket.pages.ecotool;

import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.openlca.core.database.ProcessDao;
import org.openlca.core.database.derby.DerbyDatabase;

import com.tecnalia.lca.app.db.DerbyConfiguration;

public class EcoToolSession extends WebSession {
	
	private static final long serialVersionUID = 1L;

	// Process DAO
	private ProcessDao processDao;	

	public EcoToolSession(Request request) {
		super(request);
		// Set the DAOs in the session scope
		try {
			EcoToolApplication application = (EcoToolApplication) getApplication();			
			DerbyDatabase database = application.getDerbyDatabase();
			processDao = new ProcessDao(database);			
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
	}

	public ProcessDao getProcessDao() {
		return processDao;
	}
	
}
