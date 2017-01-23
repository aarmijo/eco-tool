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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.ImpactMethodDao;
import org.openlca.core.database.NwSetDao;
import org.openlca.core.math.CalculationSetup;
import org.openlca.core.math.SystemCalculator;
import org.openlca.core.matrix.NwSetTable;
import org.openlca.core.model.AllocationMethod;
import org.openlca.core.model.ImpactMethod;
import org.openlca.core.model.NwSet;
import org.openlca.core.model.ProductSystem;
import org.openlca.core.model.descriptors.Descriptors;
import org.openlca.core.model.descriptors.ImpactMethodDescriptor;
import org.openlca.core.model.descriptors.NwSetDescriptor;
import org.openlca.core.results.ContributionItem;
import org.openlca.core.results.ContributionSet;
import org.openlca.core.results.Contributions;
import org.openlca.core.results.FullResult;
import org.openlca.core.results.FullResultProvider;
import org.openlca.core.results.ImpactResult;
import org.wicketstuff.rest.annotations.AuthorizeInvocation;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.contenthandling.json.objserialdeserial.GsonObjectSerialDeserial;
import org.wicketstuff.rest.contenthandling.json.webserialdeserial.JsonWebSerialDeserial;
import org.wicketstuff.rest.resource.AbstractRestResource;
import org.wicketstuff.rest.utils.http.HttpMethod;

import com.google.inject.Inject;
import com.tecnalia.lca.app.App;
import com.tecnalia.lca.app.db.Cache;
import com.tecnalia.lca.app.util.Numbers;
import com.tecnalia.wicket.rest.domain.impacts.ImpactCalculationPojo;
import com.tecnalia.wicket.rest.domain.impacts.ImpactCategoryPojo;
import com.tecnalia.wicket.rest.resources.utils.AlberdiProcessEnum;

public class AppSpecificServiceRestResource extends AbstractRestResource<JsonWebSerialDeserial> {

	private static final long serialVersionUID = 1L;
	
	// Get logger
	private static final Logger logger = Logger.getLogger(AppSpecificServiceRestResource.class);
	
	@Inject
	private IDatabase database;

	public AppSpecificServiceRestResource(IRoleCheckingStrategy roleCheckingStrategy) {		
		super(new JsonWebSerialDeserial(new GsonObjectSerialDeserial()), roleCheckingStrategy);
		Injector.get().inject(this);
		// Remove the next line once the cache management is properly handled
		Cache.create(database);		
	}
	
    /**
     * Application Specific Service that calculates a product system single score</p>
     * 
     * Example of use:
     * http://localhost:8080/app-specific-service/calculate-system/{systemId}/{targetValue}
     *  
     * @param systemId product system id
     * @param targetValue target value
     * @return impact calculation single score
     */
    @MethodMapping(value = "/calculate-system/{systemId}/{targetValue}", httpMethod = HttpMethod.GET)
    public ImpactCalculationPojo calculateSystemSingleScore(long systemId, double targetValue) {
    	// Retrieve the product system
    	ProductSystem system = database.createDao(ProductSystem.class).getForId(systemId);  	
    	logger.debug("Core Service Calculation - ProductSystemName: " + system.getName());
    	
    	// LCA Calculation
		// Calculation setup
		CalculationSetup setup = new CalculationSetup(system);		
		setup.allocationMethod = AllocationMethod.USE_DEFAULT;	
		
		ImpactMethod impactMethod = new ImpactMethodDao(database).getForName("ReCiPe Endpoint (E)").get(0);		
		ImpactMethodDescriptor impactMethodDescriptor = Descriptors.toDescriptor(impactMethod);		
		setup.impactMethod = impactMethodDescriptor;
				
		NwSet nwSet = new NwSetDao(database).getForName("Europe ReCiPe E/A, 2000").get(0);
		NwSetDescriptor nwSetDescriptor = Descriptors.toDescriptor(nwSet);
		setup.nwSet = nwSetDescriptor;
				
		// TODO Create the whole cache. Remove the next line once the cache management is properly handled
		//Cache.create(database);
		
		// Set the target amount
		system.setTargetAmount(targetValue);
		
		SystemCalculator calculator = new SystemCalculator(Cache.getMatrixCache(), App.getSolver());
		FullResult result = calculator.calculateFull(setup);		
		FullResultProvider resultProvider = new FullResultProvider(result, Cache.getEntityCache());			
		
		logger.debug("Single Score - ProductSystemName: " + system.getName());

		List<ImpactResult> impactResults = resultProvider.getTotalImpactResults();
		NwSetTable nwSetTable = NwSetTable.build(database, setup.nwSet.getId());
		List<ImpactResult> normalizedImpactResults = nwSetTable.applyBoth(impactResults);
		List<ContributionItem<ImpactResult>> orderedNormalizedImpactResults = makeContributions(normalizedImpactResults);

		String targetAmount = system.getTargetAmount() + " " + system.getTargetUnit().getName();

		ImpactCalculationPojo impactCalculation = new ImpactCalculationPojo(system.getName(), targetAmount,
				impactMethodDescriptor.getName(), null, "Single Score");

		List<ImpactCategoryPojo> impactCategoriesList = new ArrayList<>();
		double singleValue = 0;
		for (ContributionItem<ImpactResult> impactResult : orderedNormalizedImpactResults) {
			String name = impactResult.item.impactCategory.getName();
			double dValue = impactResult.amount;
			String value = Numbers.format(dValue);
			
			
			if (name.equalsIgnoreCase("Human Health-total") || name.equalsIgnoreCase("Resources-total") || name.equalsIgnoreCase("Ecosystems-total")) {
				impactCategoriesList.add(new ImpactCategoryPojo(name, value));
				singleValue += dValue;
			}		
			
		}
		impactCategoriesList.add(new ImpactCategoryPojo("Single Score", Numbers.format(singleValue)));

		ImpactCategoryPojo[] impactCategoriesArray = new ImpactCategoryPojo[impactCategoriesList.size()];
		impactCalculation.setImpacts(impactCategoriesList.toArray(impactCategoriesArray));

		return impactCalculation;		

    }

    /**
     * Application Specific Service that calculates a product system single score for Alberdi.
     * <p>     
     * <b>Example of use:</b>
     * http://localhost:8080/app-specific-service/calculate-alberdi/{process}/{targetValue}
     * <p>
     * A manufacturing process is built combining an operation with a material from the lists below. For example, <b>turning-steel</b> or <b>default_process-default_material</b>.
     * <p>
     * <b>Available operations:</b>
     * <br>
     * Horizontal saw cut - default_process
     * <br>
     * Central drill drilling - drilling
     * <br>
     * Stop bar operation (only when starting with a bar) - N/A
     * <br>
     * Facing - turning
     * <br>
     * Bloom lathe operation (can be repeated and can be interior and exterior) - turning
     * <br>
     * Finishing lathe operation (can be repeated and can be interior and exterior) - turning
     * <br>
     * Central lathe drilling (similar to central drill drilling but in lathe) - turning
     * <br>
     * External slotting operation - default_process
     * <br>
     * Internal slotting operation - default_process
     * <br>
     * External screwing - N/A
     * <br>
     * Internal screwing blade tip - N/A
     * <br>
     * Internal screwing with male - N/A
     * <br>
     * Parting (only when starting with a bar) - turning
     * <br>
     * Vertical slotted sawing - default_process
     * <br>
     * Flattened milling - milling
     * <br>
     * Extracting keyway - default_process
     * <br>
     * Plucking with drill - drilling
     * <br>
     * Regular drill - drilling	
     * <br>
     * Little plaques drill - drilling	
     * <br>
     * Male screwing - N/A	
     * <br>
     * Friction screwing - N/A
     * <br>
     * Rectification - N/A
     * <p>
     * <b>Available materials:</b>
     * <br>
     * Easy mechanization steel (CLASS A) - cast_iron
     * <br>
     * Aluminium (CLASS A) - aluminium
     * <br>
     * Brass (CLASS A) - brass
     * <br>
     * Bronze (CLASS A) - default_material
     * <br>
     * Plastic (CLASS A) - default_material
     * <br>
     * Carbon steel (CLASS B) - steel
     * <br>
     * Alloy steel for quenching and tempering (CLASS B) - steel
     * <br>
     * Hardening steel (CLASS B) - steel
     * <br>
     * Doped steel (CLASS C) - steel
     * <br>
     * Tool steel (CLASS C) - steel
     * <br>
     * Martensitic steel (CLASS D) - chromium_steel
     * <br>
     * Ferritic steel (CLASS D) - chromium_steel
     * <br>
     * Austenitic steel (CLASS E) - chromium_steel
     * <br>
     * Duplex steel (CLASS F) - chromium_steel
     * <br>
     * Precipitation hardened (CLASS F) - chromium_steel
     * <br>
     * Nickel alloys (CLASS G) - chromium_steel
     * <br>
     * Titanium (CLASS H) - chromium_steel
     *  
     * @param process machining process
     * @param targetValue target value
     * @return impact calculation single score 
     */    
    @MethodMapping(value = "/calculate-alberdi/{process}/{targetValue}", httpMethod = HttpMethod.GET)
    @AuthorizeInvocation(Roles.ADMIN)
    public ImpactCalculationPojo calculateAlberdiSingleScore(String process, double targetValue) {
    	int productSystemId;   	
    	try {
    		productSystemId = AlberdiProcessEnum.valueOf(AlberdiProcessEnum.getValueOfArg(process)).getProductSystemId();
    	} catch (Exception e) {
    		return new ImpactCalculationPojo("The process " + process + " is not valid", null, null, null, null);
    	}
    	ImpactCalculationPojo impact = calculateSystemSingleScore(productSystemId, targetValue);
    	return impact;
    }
    
    @MethodMapping(value = "/calculate-alberdi/login/{username}/{password}", httpMethod = HttpMethod.GET)
    public Map<String, String> login(String username, String password) {    	
    	boolean authResult = AuthenticatedWebSession.get().signIn(username, password);
    	String loginResult = null;
    	if (authResult && username.equals("proseco")) {
    		loginResult = "Login successful";
    	} else loginResult = "Login not successful";
    	return Collections.singletonMap("Login:", loginResult);
    }
    
	private static List<ContributionItem<ImpactResult>> makeContributions(List<ImpactResult> impactResults) 
	{
		ContributionSet<ImpactResult> set = Contributions.calculate(
				impactResults, new Contributions.Function<ImpactResult>() {
					@Override
					public double value(ImpactResult impactResult) {
						return impactResult.value;
					}
				});
		List<ContributionItem<ImpactResult>> items = set.contributions;
		Contributions.sortDescending(items);
		return items;
	}

}