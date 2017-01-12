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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.injection.Injector;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.ImpactMethodDao;
import org.openlca.core.database.NwSetDao;
import org.openlca.core.database.ProcessDao;
import org.openlca.core.database.ProductSystemDao;
import org.openlca.core.math.CalculationSetup;
import org.openlca.core.math.SystemCalculator;
import org.openlca.core.matrix.NwSetTable;
import org.openlca.core.model.AllocationMethod;
import org.openlca.core.model.ImpactMethod;
import org.openlca.core.model.NwSet;
import org.openlca.core.model.ProductSystem;
import org.openlca.core.model.descriptors.Descriptors;
import org.openlca.core.model.descriptors.ImpactCategoryDescriptor;
import org.openlca.core.model.descriptors.ImpactMethodDescriptor;
import org.openlca.core.model.descriptors.NwSetDescriptor;
import org.openlca.core.model.descriptors.ProcessDescriptor;
import org.openlca.core.results.ContributionItem;
import org.openlca.core.results.ContributionSet;
import org.openlca.core.results.Contributions;
import org.openlca.core.results.FullResult;
import org.openlca.core.results.FullResultProvider;
import org.openlca.core.results.ImpactResult;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.parameters.RequestBody;
import org.wicketstuff.rest.contenthandling.json.objserialdeserial.GsonObjectSerialDeserial;
import org.wicketstuff.rest.contenthandling.json.webserialdeserial.JsonWebSerialDeserial;
import org.wicketstuff.rest.resource.AbstractRestResource;
import org.wicketstuff.rest.utils.http.HttpMethod;

import com.google.inject.Inject;
import com.tecnalia.lca.app.App;
import com.tecnalia.lca.app.db.Cache;
import com.tecnalia.lca.app.util.Numbers;
import com.tecnalia.wicket.pages.ecotool.EcoToolApplication;
import com.tecnalia.wicket.pages.ecotool.systems.ProductSystemDescriptor;
import com.tecnalia.wicket.rest.domain.impacts.ImpactCalculationPojo;
import com.tecnalia.wicket.rest.domain.impacts.ImpactCategoryPojo;

public class CoreServiceRestResource extends AbstractRestResource<JsonWebSerialDeserial> {

	private static final long serialVersionUID = 1L;
	
	// Get logger
	private static final Logger logger = Logger.getLogger(CoreServiceRestResource.class);
	
	@Inject
	private IDatabase database;

	public CoreServiceRestResource() {		
		super(new JsonWebSerialDeserial(new GsonObjectSerialDeserial()));
		Injector.get().inject(this);
		// Remove the next line once the cache management is properly handled
		Cache.create(database);
	}

    @MethodMapping(value = "/processes", httpMethod = HttpMethod.GET)
    public List<ProcessDescriptor> getProcesses() {
    	List<ProcessDescriptor> processDescriptors = (new ProcessDao(database)).getDescriptors();
    	return processDescriptors;
    }
    
    /**
     * Service that retrieves the list of product systems
     * 
     * Example of use:
     * http://localhost:8080/core-service/product-systems
     *
     * @return product system descriptors
     */    
    @MethodMapping(value = "/product-systems", httpMethod = HttpMethod.GET)
    public List<ProductSystemDescriptor> getProductSystems() {
    	List<ProductSystem> productSystems = (new ProductSystemDao(database)).getAll();
    	List<ProductSystemDescriptor> productSystemDescriptors = new ArrayList<>();  	
    	for (ProductSystem productSystem : productSystems) {    		
    		productSystemDescriptors.add(new ProductSystemDescriptor(productSystem));
    	}
    	return productSystemDescriptors;
    }
    
    /**
     * Configures the Core Service
     * 
     * @param productSystemDescriptor the configuration object
     */
    @MethodMapping(value = "/configure", httpMethod = HttpMethod.POST)
    public void configureCoreService(@RequestBody ProductSystemDescriptor[] productSystemDescriptor) {
    	// Configure the application    	
    	EcoToolApplication application = (EcoToolApplication) Application.get();
    	application.setCoreServiceConfiguration(Arrays.asList(productSystemDescriptor));
    }
    

    
    /**
     * Core Service that calculates a product system
     * 
     * Examples of use:
     * http://localhost:8080/core-service/calculate/1/characterization
     * http://localhost:8080/core-service/calculate/1/normalization
     * 
     * @param systemIndex product system index from the product system list
     * @param analysisType characterization or normalization
     * @return impact calculation
     */
    @MethodMapping(value = "/calculate/{systemIndex}/{analysisType}", httpMethod = HttpMethod.GET)
    public ImpactCalculationPojo calculateImpact(int systemIndex, String analysisType) {
    	// Retrieve the configuration
    	EcoToolApplication application = (EcoToolApplication) Application.get();
    	ProductSystemDescriptor systemDescriptor = application.getCoreServiceConfiguration().get(systemIndex-1);
    	ProductSystem system = database.createDao(ProductSystem.class).getForId(systemDescriptor.getId());    	
    	logger.debug("Core Service Calculation - ProductSystemName: " + system.getName());
    	
    	// LCA Calculation
		// Calculation setup
		CalculationSetup setup = new CalculationSetup(system);		
		setup.setAllocationMethod(AllocationMethod.USE_DEFAULT);	
		
		ImpactMethod impactMethod = new ImpactMethodDao(database).getForName("ReCiPe Midpoint (E)").get(0);		
		ImpactMethodDescriptor impactMethodDescriptor = Descriptors.toDescriptor(impactMethod);		
		setup.setImpactMethod(impactMethodDescriptor);
				
		NwSet nwSet = new NwSetDao(database).getForName("Europe ReCiPe E, 2000").get(0);
		NwSetDescriptor nwSetDescriptor = Descriptors.toDescriptor(nwSet);
		setup.setNwSet(nwSetDescriptor);
				
		// TODO Create the whole cache. Remove the next line once the cache management is properly handled
		//Cache.create(database);
		
		SystemCalculator calculator = new SystemCalculator(Cache.getMatrixCache(), App.getSolver());
		FullResult result = calculator.calculateFull(setup);		
		FullResultProvider resultProvider = new FullResultProvider(result, Cache.getEntityCache());
		
		if (analysisType.equalsIgnoreCase("characterization")) {
			
			logger.debug("Characterization - ProductSystemName: " + system.getName());
			
			List<ImpactCategoryDescriptor> impacts = sortImpactsByAmount(resultProvider.getImpactDescriptors(), resultProvider);
			
			String targetAmount = system.getTargetAmount() + " " + system.getTargetUnit().getName();
			
			ImpactCalculationPojo impactCalculation = new ImpactCalculationPojo(system.getName(), 
					targetAmount, impactMethodDescriptor.getName(), null, "Characterization");
			
			List<ImpactCategoryPojo> impactCategoriesList = new ArrayList<>();
			for (ImpactCategoryDescriptor impact : impacts) {
				String name = impact.getName() + " " + "(" + impact.getReferenceUnit() + ")";
				String value = Numbers.format(resultProvider.getTotalImpactResult(impact).getValue());
				impactCategoriesList.add(new ImpactCategoryPojo(name, value));
			}
			
			ImpactCategoryPojo [] impactCategoriesArray = new ImpactCategoryPojo[impactCategoriesList.size()];
			impactCalculation.setImpacts(impactCategoriesList.toArray(impactCategoriesArray));

			return impactCalculation;
			
		} else if (analysisType.equalsIgnoreCase("normalization")) {
			
			logger.debug("Normalization - ProductSystemName: " + system.getName());
			
			List<ImpactResult> impactResults = resultProvider.getTotalImpactResults();
			NwSetTable nwSetTable = NwSetTable.build(database, setup.getNwSet().getId());
			List<ImpactResult> normalizedImpactResults = nwSetTable.applyNormalisation(impactResults);
			List<ContributionItem<ImpactResult>> orderedNormalizedImpactResults = makeContributions(normalizedImpactResults);
			
			String targetAmount = system.getTargetAmount() + " " + system.getTargetUnit().getName();
			
			ImpactCalculationPojo impactCalculation = new ImpactCalculationPojo(system.getName(), 
					targetAmount, impactMethodDescriptor.getName(), null, "Normalization");
			
			List<ImpactCategoryPojo> impactCategoriesList = new ArrayList<>();
			for (ContributionItem<ImpactResult> impactResult : orderedNormalizedImpactResults) {	
				String name = impactResult.getItem().getImpactCategory().getName();
				String value = Numbers.format(impactResult.getAmount());
				impactCategoriesList.add(new ImpactCategoryPojo(name, value));
			}
			
			ImpactCategoryPojo [] impactCategoriesArray = new ImpactCategoryPojo[impactCategoriesList.size()];
			impactCalculation.setImpacts(impactCategoriesList.toArray(impactCategoriesArray));

			return impactCalculation;
			
		}
		
		return null;		
    }
    
    /**
     * Core Service that calculates a product system single score
     * 
     * Example of use:
     * http://localhost:8080/core-service/calculate/1
     *  
     * @param systemIndex product system index from the product system list     * 
     * @return impact calculation single score
     */
    @MethodMapping(value = "/calculate/{systemIndex}", httpMethod = HttpMethod.GET)
    public ImpactCalculationPojo calculateSingleScore(int systemIndex) {
    	// Retrieve the configuration
    	EcoToolApplication application = (EcoToolApplication) Application.get();
    	ProductSystemDescriptor systemDescriptor = application.getCoreServiceConfiguration().get(systemIndex-1);
    	ProductSystem system = database.createDao(ProductSystem.class).getForId(systemDescriptor.getId());    	
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
    
	public List<ImpactCategoryDescriptor> sortImpactsByAmount(Collection<ImpactCategoryDescriptor> impacts, FullResultProvider resultProvider) {
		List<ImpactCategoryDescriptor> list = new ArrayList<>(impacts);
		Collections.sort(list, new Comparator<ImpactCategoryDescriptor>() {
			@Override
			public int compare(ImpactCategoryDescriptor o1, ImpactCategoryDescriptor o2) {
				double val1 = resultProvider.getTotalImpactResult(o1).getValue();
				double val2 = resultProvider.getTotalImpactResult(o2).getValue();
				return Double.compare(val2, val1);
			}
		});
		return list;
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