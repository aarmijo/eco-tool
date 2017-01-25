/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tecnalia.wicket.pages.ecotool;

import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.wicketstuff.annotation.mount.MountPath;

import com.tecnalia.wicket.pages.ecotool.EcoToolBasePage;
import com.tecnalia.wicket.pages.ecotool.processes.ProcessEditor;
import com.tecnalia.wicket.pages.ecotool.systems.ProductSystemEditor;
import com.tecnalia.wicket.security.CookieUtils;

/**
 * Eco-tool HomePage
 * 
 * @author Alberto Armijo
 */
@SuppressWarnings("serial")
@MountPath(value = "/proseco/eco-tool")
public class HomePage extends EcoToolBasePage {

	// Get logger
	private static final Logger logger = Logger.getLogger(HomePage.class);
	
	/**
	 * Constructor.
	 */	
	public HomePage() {	
		
		// Navigation links
		add(new BookmarkablePageLink<Void>("homeLink", HomePage.class));
		add(new BookmarkablePageLink<Void>("processesLink", ProcessEditor.class));
		add(new BookmarkablePageLink<Void>("productSystemsLink", ProductSystemEditor.class));

		// Retrieve the PES_ID from the URL 
		// /proseco/eco-tool/init_Eco('SESSION_ID', 'PES_12345')
		String url = RequestCycle.get().getRequest().getUrl().toString();		
		String pesId = StringUtils.substringBetween(url, "%20%27", "%27%29");
		// Bind the session to the application's session store
		getEcoToolSession().bind();
		// Set the PES_ID in the session		
		getEcoToolSession().setPesId(pesId);
		logger.debug("The PES_ID: " + pesId + " has been added to the current session");
		
		// Add the pesIdLabel
        Label pesIdLabel = new Label("pesIdLabel", pesId);
        pesIdLabel.setEscapeModelStrings(false);
        add(pesIdLabel);
        
        // Add the username label
    	add(new Label("username", new PropertyModel(this, "session.username")){
    		@Override
    		protected void onConfigure() {
    			super.onConfigure();
    			setVisible(getDefaultModelObject() != null);    				
    		}
    	});
    	
    	// Add logout link
    	add(new Link("logOut") {
			@Override
			public void onClick() {
				AuthenticatedWebSession.get().invalidate();
				
				CookieUtils.removeCookieIfPresent(getRequest(), getResponse(), CookieUtils.REMEMBER_ME_LOGIN_COOKIE);
				CookieUtils.removeCookieIfPresent(getRequest(), getResponse(), CookieUtils.REMEMBER_ME_PASSWORD_COOKIE);				
				setResponsePage(getApplication().getHomePage());
			}
		});
	}
}