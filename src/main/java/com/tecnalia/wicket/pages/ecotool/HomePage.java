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

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.wicketstuff.annotation.mount.MountPath;

import com.tecnalia.wicket.pages.ecotool.EcoToolBasePage;
import com.tecnalia.wicket.pages.ecotool.processes.ProcessEditor;
import com.tecnalia.wicket.pages.ecotool.systems.ProductSystemEditor;

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

	}
}