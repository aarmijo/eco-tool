package com.tecnalia.lca.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openlca.core.database.ImpactMethodDao;
import org.openlca.core.database.NwSetDao;
import org.openlca.core.database.ProductSystemDao;
import org.openlca.core.database.derby.DerbyDatabase;
import org.openlca.core.math.CalculationSetup;
import org.openlca.core.math.SystemCalculator;
import org.openlca.core.matrix.NwSetTable;
import org.openlca.core.model.AllocationMethod;
import org.openlca.core.model.ProductSystem;
import org.openlca.core.model.descriptors.ImpactCategoryDescriptor;
import org.openlca.core.model.descriptors.ImpactMethodDescriptor;
import org.openlca.core.model.descriptors.NwSetDescriptor;
import org.openlca.core.results.ContributionItem;
import org.openlca.core.results.ContributionSet;
import org.openlca.core.results.Contributions;
import org.openlca.core.results.FullResult;
import org.openlca.core.results.FullResultProvider;
import org.openlca.core.results.ImpactResult;
import org.openlca.eigen.NativeLibrary;
import org.openlca.util.Strings;

import com.tecnalia.lca.app.db.Cache;
import com.tecnalia.lca.app.db.Database;
import com.tecnalia.lca.app.db.DatabaseList;
import com.tecnalia.lca.app.util.Numbers;

public class AppLoader {
	
	// Get logger
	private static final Logger logger = Logger.getLogger(AppLoader.class);
	
	public static void load() {
		// Load LCA application
		File workspace = Workspace.init();
		logger.debug("Workspace initialised at " + workspace);
		NativeLibrary.loadFromDir(workspace);
		logger.debug("olca-eigen loaded: " + NativeLibrary.isLoaded());
		Numbers.setDefaultAccuracy(5);
	
		DatabaseList dbList = Database.getConfigurations();
		DerbyDatabase db = null;
		try {
			db = (DerbyDatabase) Database.activate(dbList.getLocalDatabases().get(2));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		// Test some LCA calculations		
		ProductSystemDao productSystem = new ProductSystemDao(db);		
		List<ProductSystem> productSystemList = productSystem.getAll();		
		ProductSystem ps = productSystemList.get(0);		
		
		logger.debug("ProductSystemName: " + ps.getName());	
		
		CalculationSetup setup = new CalculationSetup(ps);		
		setup.allocationMethod = AllocationMethod.USE_DEFAULT;
				
		List<ImpactMethodDescriptor> imds = new ImpactMethodDao(db).getDescriptors();		
		ImpactMethodDescriptor imd = imds.get(11);
		logger.debug("Impact Method: " + imd.getName());
		setup.impactMethod = imd;
		
		List<NwSetDescriptor> nsds = new NwSetDao(db).getDescriptorsForMethod(imd.getId());
		NwSetDescriptor nsd = nsds.get(0);
		logger.debug("Normalization: " + nsd.getName());
		setup.nwSet = nsd;
		
		// Externalize this parameter. It is the functional unit target amount
		//setup.setAmount(100);
		ps.setTargetAmount(1.49338E7);
		
		SystemCalculator calculator = new SystemCalculator(Cache.getMatrixCache(), App.getSolver());
		
		FullResult fullResult = calculator.calculateFull(setup);
		
		FullResultProvider fullResultProvider = new FullResultProvider(fullResult, Cache.getEntityCache());
		
		Set<ImpactCategoryDescriptor> set = fullResultProvider.getImpactDescriptors();
		List<ImpactCategoryDescriptor> impacts = sortImpacts(set);
		
		// Characterization
		System.out.println("Characterization:");
		for (ImpactCategoryDescriptor impact : impacts) {
			System.out.println("Impact Category: "
					+ impact.getName()
					+ "; Value: "
					+ Numbers.format(fullResultProvider.getTotalImpactResult(impact)
							.value)
					+ " "
					+ fullResultProvider.getTotalImpactResult(impact)
							.impactCategory.getReferenceUnit());
		}
		
		// Normalization		
		List<ImpactResult> impactResults = fullResultProvider.getTotalImpactResults();		
		NwSetTable nwSetTable = NwSetTable.build(db, setup.nwSet.getId());
		List<ImpactResult> normalizedImpactResults = nwSetTable.applyNormalisation(impactResults);
		List<ContributionItem<ImpactResult>> orderedNormalizedImpactResults = makeContributions(normalizedImpactResults);
		
		System.out.println("Normalization:");
		for (ContributionItem<ImpactResult> result : orderedNormalizedImpactResults) {			
			System.out.println("Impact Category: " + result.item.impactCategory.getName() +
					"; Value: " + Numbers.format(result.amount));			
		}		
		
		logger.debug("close database");
		try {
			Database.close();
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}
	
	static List<ImpactCategoryDescriptor> sortImpacts(
			Collection<ImpactCategoryDescriptor> impacts) {
		List<ImpactCategoryDescriptor> list = new ArrayList<>(impacts);
		Collections.sort(list, new Comparator<ImpactCategoryDescriptor>() {
			@Override
			public int compare(ImpactCategoryDescriptor o1,
					ImpactCategoryDescriptor o2) {
				return Strings.compare(o1.getName(), o2.getName());
			}
		});
		return list;
	}
	
	private static List<ContributionItem<ImpactResult>> makeContributions(
			List<ImpactResult> impactResults) {
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
