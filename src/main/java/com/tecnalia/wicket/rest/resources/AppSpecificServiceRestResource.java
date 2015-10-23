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

import org.apache.log4j.Logger;
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

public class AppSpecificServiceRestResource extends AbstractRestResource<JsonWebSerialDeserial> {

	private static final long serialVersionUID = 1L;
	
	// Get logger
	private static final Logger logger = Logger.getLogger(AppSpecificServiceRestResource.class);
	
	@Inject
	private IDatabase database;

	public AppSpecificServiceRestResource() {		
		super(new JsonWebSerialDeserial(new GsonObjectSerialDeserial()));
		Injector.get().inject(this);
		// Remove the next line once the cache management is properly handled
		Cache.create(database);
	}
	
    /**
     * Application Specific Service that calculates a product system single score for Alberdi
     * 
     * Example of use:
     * http://localhost:8080/app-specific-service/calculate-alberdi/{systemId}/{targetValue}
     *  
     * @param systemId product system id
     * @param targetValue target value
     * @return impact calculation single score
     */
    @MethodMapping(value = "/calculate-alberdi/{systemId}/{targetValue}", httpMethod = HttpMethod.GET)
    public ImpactCalculationPojo calculateAlberdiSingleScore(long systemId, double targetValue) {
    	// Retrieve the product system
    	ProductSystem system = database.createDao(ProductSystem.class).getForId(systemId);  	
    	logger.debug("Core Service Calculation - ProductSystemName: " + system.getName());
    	
    	// LCA Calculation
		// Calculation setup
		CalculationSetup setup = new CalculationSetup(system);		
		setup.setAllocationMethod(AllocationMethod.USE_DEFAULT);	
		
		ImpactMethod impactMethod = new ImpactMethodDao(database).getForName("ReCiPe Endpoint (E)").get(0);		
		ImpactMethodDescriptor impactMethodDescriptor = Descriptors.toDescriptor(impactMethod);		
		setup.setImpactMethod(impactMethodDescriptor);
				
		NwSet nwSet = new NwSetDao(database).getForName("Europe ReCiPe E/A, 2000").get(0);
		NwSetDescriptor nwSetDescriptor = Descriptors.toDescriptor(nwSet);
		setup.setNwSet(nwSetDescriptor);
				
		// TODO Create the whole cache. Remove the next line once the cache management is properly handled
		//Cache.create(database);
		
		// Set the target amount
		system.setTargetAmount(targetValue);
		
		SystemCalculator calculator = new SystemCalculator(Cache.getMatrixCache(), App.getSolver());
		FullResult result = calculator.calculateFull(setup);		
		FullResultProvider resultProvider = new FullResultProvider(result, Cache.getEntityCache());			
		
		logger.debug("Single Score - ProductSystemName: " + system.getName());

		List<ImpactResult> impactResults = resultProvider.getTotalImpactResults();
		NwSetTable nwSetTable = NwSetTable.build(database, setup.getNwSet().getId());
		List<ImpactResult> normalizedImpactResults = nwSetTable.applyBoth(impactResults);
		List<ContributionItem<ImpactResult>> orderedNormalizedImpactResults = makeContributions(normalizedImpactResults);

		String targetAmount = system.getTargetAmount() + " " + system.getTargetUnit().getName();

		ImpactCalculationPojo impactCalculation = new ImpactCalculationPojo(system.getName(), targetAmount,
				impactMethodDescriptor.getName(), null, "Single Score");

		List<ImpactCategoryPojo> impactCategoriesList = new ArrayList<>();
		double singleValue = 0;
		for (ContributionItem<ImpactResult> impactResult : orderedNormalizedImpactResults) {
			String name = impactResult.getItem().getImpactCategory().getName();
			double dValue = impactResult.getAmount();
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
    
	private static List<ContributionItem<ImpactResult>> makeContributions(List<ImpactResult> impactResults) 
	{
		ContributionSet<ImpactResult> set = Contributions.calculate(
				impactResults, new Contributions.Function<ImpactResult>() {
					@Override
					public double value(ImpactResult impactResult) {
						return impactResult.getValue();
					}
				});
		List<ContributionItem<ImpactResult>> items = set.getContributions();
		Contributions.sortDescending(items);
		return items;
	}

}