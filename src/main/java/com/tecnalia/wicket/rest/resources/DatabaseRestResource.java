package com.tecnalia.wicket.rest.resources;

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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.ProcessDao;
import org.openlca.core.database.ProductSystemDao;
import org.openlca.core.model.ProductSystem;
import org.openlca.core.model.descriptors.ProcessDescriptor;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.contenthandling.json.objserialdeserial.GsonObjectSerialDeserial;
import org.wicketstuff.rest.contenthandling.json.webserialdeserial.JsonWebSerialDeserial;
import org.wicketstuff.rest.resource.AbstractRestResource;
import org.wicketstuff.rest.utils.http.HttpMethod;

import com.google.inject.Inject;
import com.tecnalia.wicket.pages.ecotool.systems.ProductSystemDescriptor;

public class DatabaseRestResource extends AbstractRestResource<JsonWebSerialDeserial> {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private IDatabase database;

	public DatabaseRestResource() {		
		super(new JsonWebSerialDeserial(new GsonObjectSerialDeserial()));
		Injector.get().inject(this);
	}

    @MethodMapping(value = "/processes", httpMethod = HttpMethod.GET)
    public List<ProcessDescriptor> getProcesses() {
    	List<ProcessDescriptor> processDescriptors = (new ProcessDao(database)).getDescriptors();
    	return processDescriptors;
    }
    
    @MethodMapping(value = "/product-systems", httpMethod = HttpMethod.GET)
    public List<ProductSystemDescriptor> getProductSystems() {
    	List<ProductSystem> productSystems = (new ProductSystemDao(database)).getAll();
    	List<ProductSystemDescriptor> productSystemDescriptors = new ArrayList<>();  	
    	for (ProductSystem productSystem : productSystems) {    		
    		productSystemDescriptors.add(new ProductSystemDescriptor(productSystem));
    	}	
    	return productSystemDescriptors;
    }

}

