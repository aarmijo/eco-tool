/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.tecnalia.wicket.pages.ecotool;

import javax.servlet.http.Cookie;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.annotation.mount.MountPath;

import com.tecnalia.wicket.security.CookieUtils;

@RequireHttps
public class SignInPage extends WebPage {
	private String username;
	private String password;
	private Boolean rememberMe;
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		StatelessForm form = new StatelessForm("form"){
			@Override
			protected void onSubmit() {				
				if(Strings.isEmpty(username) || Strings.isEmpty(password))	
					return;					
				boolean authResult = AuthenticatedWebSession.get().signIn(username, password);					
				if(authResult) 
					if (rememberMe) {
						// Save cookies
						CookieUtils.saveCookie(getResponse(), CookieUtils.REMEMBER_ME_LOGIN_COOKIE, 
								username, CookieUtils.REMEMBER_ME_DURATION_IN_DAYS);
						CookieUtils.saveCookie(getResponse(), CookieUtils.REMEMBER_ME_PASSWORD_COOKIE, 
								password, CookieUtils.REMEMBER_ME_DURATION_IN_DAYS);
					}						
					continueToOriginalDestination();					
			}					
		};
		
		form.setDefaultModel(new CompoundPropertyModel(this));
		
		form.add(new TextField("username"));
		form.add(new PasswordTextField("password"));
		form.add(new CheckBox("rememberMe"));
		form.add(new FeedbackPanel("feedbackPanel"));
		
		add(form);
	}
}
