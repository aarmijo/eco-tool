package com.tecnalia.wicket.pages.ecotool;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.openlca.core.database.ProcessDao;
import org.openlca.core.database.derby.DerbyDatabase;

public class EcoToolSession extends AuthenticatedWebSession {
	
	private static final long serialVersionUID = 1L;

	// Session user
	private String username;
	
	// Process DAO
	private ProcessDao processDao;
	
	// PES_ID
	private String pesId;
	
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
	
	public String getPesId() {
		return pesId;
	}

	public void setPesId(String pesId) {
		this.pesId = pesId;
	}

	@Override
	protected boolean authenticate(String username, String password) {
		boolean authResult = username.equals(password);
		
		if(authResult)
			this.username = username;
		
		return authResult;
	}

	@Override
	public Roles getRoles() {
		Roles resultRoles = new Roles();
		
		if(isSignedIn())
			resultRoles.add(Roles.USER);
		
		if(username!= null && username.equals("proseco"))
			resultRoles.add(Roles.ADMIN);
		
		return resultRoles;
	}
	
	@Override
	public void signOut() {
		super.signOut();
		username = null;
	}
	
}
