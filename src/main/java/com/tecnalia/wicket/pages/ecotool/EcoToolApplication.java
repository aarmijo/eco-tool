package com.tecnalia.wicket.pages.ecotool;

import java.io.File;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;
import org.apache.wicket.guice.GuiceComponentInjector;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.derby.DerbyDatabase;
import org.openlca.eigen.NativeLibrary;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.tecnalia.lca.app.AppLoader;
import com.tecnalia.lca.app.Workspace;
import com.tecnalia.lca.app.db.Cache;
import com.tecnalia.lca.app.db.Database;
import com.tecnalia.lca.app.db.DerbyConfiguration;
import com.tecnalia.lca.app.util.Numbers;
import com.tecnalia.wicket.pages.ecotool.systems.ProductSystemDescriptor;
import com.tecnalia.wicket.rest.resources.AppSpecificServiceRestResource;
import com.tecnalia.wicket.rest.resources.CoreServiceRestResource;
import com.tecnalia.wicket.rest.resources.DatabaseRestResource;
import com.tecnalia.wicket.security.CookieUtils;

public class EcoToolApplication extends AuthenticatedWebApplication { 
	
	private DerbyDatabase derbyDatabase = null;

	// Core Service Configuration
	private List<ProductSystemDescriptor> coreServiceConfiguration;

	// Get logger
	private static final Logger logger = Logger.getLogger(EcoToolApplication.class);

	
	/**
	 * Constructor
	 */
	public EcoToolApplication() {
	}

	@Override
	public void init() {
		super.init();

		// Mount scanner for the eco-tool application
		new AnnotatedMountScanner().scanPackage("com.tecnalia.wicket.pages.ecotool").mount(this);
		
		// Test LCA application
		//AppLoader.load();
		
		// Load LCA application
		File workspace = Workspace.init();
		logger.debug("Workspace initialised at " + workspace);
		NativeLibrary.loadFromDir(workspace);
		logger.debug("olca-eigen loaded: " + NativeLibrary.isLoaded());
		Numbers.setDefaultAccuracy(5);
		// Derby database connection
		DerbyConfiguration derbyConfiguration = new DerbyConfiguration();
		derbyConfiguration.setName("ProSEco_LCA");					
		try {
			this.derbyDatabase = (DerbyDatabase) Database.activate(derbyConfiguration);
			//this.derbyDatabase = derbyConfiguration.createInstance();
			//Cache.create(this.derbyDatabase);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Guice Dependency injection
		GuiceComponentInjector injector = new GuiceComponentInjector(this, new Module()
		{
			@Override
			public void configure(final Binder binder)
			{
				binder.bind(IDatabase.class).toInstance(derbyDatabase);
		   }

		});
		getComponentInstantiationListeners().add(injector);
		
		// RESTful resources
		mountResource("/database", new ResourceReference("restReference") {
			
			private static final long serialVersionUID = 1L;
			
			DatabaseRestResource resource = new DatabaseRestResource();
			@Override
			public IResource getResource() {
				return resource;
			}

		});		
		mountResource("/core-service", new ResourceReference("restReference") {
			
			private static final long serialVersionUID = 1L;
			
			CoreServiceRestResource resource = new CoreServiceRestResource();
			@Override
			public IResource getResource() {
				return resource;
			}

		});
		mountResource("/app-specific-service", new ResourceReference("restReference") {
			
			private static final long serialVersionUID = 1L;
			
			AppSpecificServiceRestResource resource = new AppSpecificServiceRestResource();
			@Override
			public IResource getResource() {
				return resource;
			}

		});
		
		// Set annotations role authorization strategy
		getSecuritySettings().setAuthorizationStrategy(new AnnotationsRoleAuthorizationStrategy(this));	
	}

	public static EcoToolApplication get() {
		return (EcoToolApplication) Application.get();
	}

	@Override
	public Session newSession(Request request, Response response) {
		AuthenticatedWebSession ecoToolSession = new EcoToolSession(request);
		// Retrieve cookies
		Cookie loginCookie = CookieUtils.loadCookie(request, CookieUtils.REMEMBER_ME_LOGIN_COOKIE);
		Cookie passwordCookie = CookieUtils.loadCookie(request, CookieUtils.REMEMBER_ME_PASSWORD_COOKIE);
		if (loginCookie != null && passwordCookie != null) {
			if (loginCookie.getValue() != null && passwordCookie.getValue() != null) {
				ecoToolSession.signIn(loginCookie.getValue(), passwordCookie.getValue());
			}
		}		
		return ecoToolSession;
	}

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return com.tecnalia.wicket.pages.ecotool.HomePage.class;
	}
	
	
	/**
	 * Gets the database connection
	 * 
	 * @return database instance for the application
	 */
	public DerbyDatabase getDerbyDatabase() {
		return derbyDatabase;
	}

	/**
	 * Sets the configuration of the core service
	 * 
	 * @param coreServiceConfiguration product system descriptor list
	 */
	public void setCoreServiceConfiguration(List<ProductSystemDescriptor> coreServiceConfiguration) {
		this.coreServiceConfiguration = coreServiceConfiguration;
		
	}
	
	/**
	 * Gets the Core Service Configuration
	 * 
	 * @return the coreServiceConfiguration
	 */
	public List<ProductSystemDescriptor> getCoreServiceConfiguration() {
		return coreServiceConfiguration;
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {		
		return SignInPage.class;
	}

	@Override
	protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
		return EcoToolSession.class;
	}
	
}
