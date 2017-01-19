package com.tecnalia.wicket.pages.ecotool.results.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.openlca.core.database.IDatabase;
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
import org.openlca.util.Strings;

import com.google.inject.Inject;
import com.googlecode.wickedcharts.highcharts.options.Axis;
import com.googlecode.wickedcharts.highcharts.options.ChartOptions;
import com.googlecode.wickedcharts.highcharts.options.CreditOptions;
import com.googlecode.wickedcharts.highcharts.options.CssStyle;
import com.googlecode.wickedcharts.highcharts.options.Function;
import com.googlecode.wickedcharts.highcharts.options.HorizontalAlignment;
import com.googlecode.wickedcharts.highcharts.options.Labels;
import com.googlecode.wickedcharts.highcharts.options.Legend;
import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.highcharts.options.SeriesType;
import com.googlecode.wickedcharts.highcharts.options.Title;
import com.googlecode.wickedcharts.highcharts.options.Tooltip;
import com.googlecode.wickedcharts.highcharts.options.series.SimpleSeries;
import com.googlecode.wickedcharts.wicket7.highcharts.Chart;
import com.tecnalia.lca.app.App;
import com.tecnalia.lca.app.db.Cache;
import com.tecnalia.wicket.pages.ecotool.EcoToolBasePage;
import com.tecnalia.wicket.pages.ecotool.processes.ProcessEditor;
import com.tecnalia.wicket.pages.ecotool.systems.ProductSystemDescriptor;
import com.tecnalia.wicket.pages.ecotool.systems.ProductSystemEditor;
import com.tecnalia.wicket.pages.ecotool.systems.wizard.ImpactFormModel;
import com.tecnalia.wicket.pages.ecotool.HomePage;

@SuppressWarnings("serial")
public class AnalyzeEditor extends EcoToolBasePage{

	@Inject
	private IDatabase database;
	
	public AnalyzeEditor(ProductSystemDescriptor productSystemDescriptor,
			ImpactFormModel impactFormModel) {

		// Navigation links
		add(new BookmarkablePageLink<Void>("homeLink", HomePage.class));
		add(new BookmarkablePageLink<Void>("processesLink", ProcessEditor.class));
		add(new BookmarkablePageLink<Void>("productSystemsLink", ProductSystemEditor.class));
		
		ImpactMethodDescriptor impactMethodDescriptor = impactFormModel.getImpactMethod();
		NwSetDescriptor nwSetDescriptor = impactFormModel.getNwSet();
		
		ProductSystem productSystem = database.createDao(ProductSystem.class).getForId(productSystemDescriptor.getId());
		//ImpactMethod impactMethod = database.createDao(ImpactMethod.class).getForId(impactMethodDescriptor.getId());
		
		// Calculation setup
		CalculationSetup setup = new CalculationSetup(productSystem);		
		setup.allocationMethod = AllocationMethod.USE_DEFAULT;
		setup.impactMethod = impactMethodDescriptor;
		NwSetDescriptor set = nwSetDescriptor;
		setup.nwSet = set;
		setup.parameterRedefs.addAll(productSystem.getParameterRedefs());		
		
		// Calculation: default calculation type is Analysis 
		// TODO Create the whole cache. Remove the next line once the cache management is properly handled
		Cache.create(database);		
		
		SystemCalculator calculator = new SystemCalculator(Cache.getMatrixCache(), App.getSolver());
		FullResult result = calculator.calculateFull(setup);		
		FullResultProvider resultProvider = new FullResultProvider(result, Cache.getEntityCache());
		
		// Cache the objects required by the tabbed panels 
		String setupKey = Cache.getAppCache().put(setup);
		String resultProviderKey = Cache.getAppCache().put(resultProvider);
		

		// Add a label for the body title
        Label processNameLabel = new Label("productSystemNameLabel", productSystemDescriptor.getName());
        processNameLabel.setEscapeModelStrings(false);
        add(processNameLabel);
		
        // Create a list of ITab objects used to feed the tabbed panel
		List<ITab> tabs = new ArrayList<ITab>();
		tabs.add(new AbstractTab(new Model<String>("General Information"))
		{
			@Override
			public Panel getPanel(String panelId)
			{
				return new AnalyzeInfoPanel(panelId, setupKey);
			}
		});

		tabs.add(new AbstractTab(new Model<String>("LCIA - Characterization"))
		{
			@Override
			public Panel getPanel(String panelId)
			{
				return new CharacterizationPanel(panelId, setupKey, resultProviderKey);
			}
		});

		
		if (nwSetDescriptor != null) {
			tabs.add(new AbstractTab(new Model<String>("Normalization")) 
			{			
				@Override
				public Panel getPanel(String panelId) 
				{
					return new NormalizationPanel(panelId, setupKey, resultProviderKey);
				}
			});
		}
		
		if (nwSetDescriptor != null) {
			tabs.add(new AbstractTab(new Model<String>("Weighting"))
			{
				@Override
				public Panel getPanel(String panelId)
				{
					return new WeightingPanel(panelId);
				}
			});
		}

		add(new AjaxTabbedPanel<>("tabs", tabs));
		
	}
	
	/**
	 * Panel representing the content panel for the first tab.
	 */
	private static class AnalyzeInfoPanel extends Panel
	{
		/**
		 * Constructor
		 * 
		 * @param id
		 *            component id
		 */
		public AnalyzeInfoPanel(String id, String setupKey)
		{
			super(id);
			
			// Obtain the cached objects
			CalculationSetup setup = Cache.getAppCache().get(setupKey, CalculationSetup.class);
			
			ProductSystem productSystem = setup.productSystem;
			ImpactMethodDescriptor impactMethodDescriptor = setup.impactMethod;
			NwSetDescriptor nwSetDescriptor = setup.nwSet;
			
			// Add the labels to the panel
	        Label system = new Label("system", productSystem.getName());
	        system.setEscapeModelStrings(false);
	        add(system);
	        
	        Label amount = new Label("amount", setup.getAmount() + " " + setup.getUnit().getName());
	        amount.setEscapeModelStrings(false);
	        add(amount);
	        
	        Label method = new Label("method", impactMethodDescriptor.getName());
	        method.setEscapeModelStrings(false);
	        add(method);
	        
	        Label nwSet = new Label("nwSet", nwSetDescriptor == null ? "" : nwSetDescriptor.getName());
	        nwSet.setEscapeModelStrings(false);
	        add(nwSet);
		}
	}
	
	/**
	 * Panel representing the content panel for the first tab.
	 */
	private static class CharacterizationPanel extends Panel
	{
		/**
		 * Constructor
		 * 
		 * @param id component id
		 * @param resultProviderKey 
		 * @param setupKey 
		 * @param setup 
		 */
		public CharacterizationPanel(String id, String setupKey, String resultProviderKey)
		{
			super(id);
			
			// Obtain the cached objects
			CalculationSetup setup = Cache.getAppCache().get(setupKey, CalculationSetup.class);		
			FullResultProvider resultProvider = Cache.getAppCache().get(resultProviderKey, FullResultProvider.class);
			
			ProductSystem productSystem = setup.productSystem;
			ImpactMethodDescriptor impactMethodDescriptor = setup.impactMethod;
			List<ImpactCategoryDescriptor> impacts = sortImpactsByAmount(resultProvider.getImpactDescriptors(), resultProvider);			
			
	        // Add the characterization chart
	        Options options = new Options();
	        options.setChartOptions(new ChartOptions().setType(SeriesType.COLUMN));
	        options.setTitle(new Title("Characterization of: " + productSystem.getName() + ", " + " Impact Method: " + impactMethodDescriptor.getName()));
	        String flowProperty = productSystem.getTargetFlowPropertyFactor().getFlowProperty().getName();
	        options.setSubtitle(new Title("Target value: " + productSystem.getTargetAmount() + " " + productSystem.getTargetUnit().getName() + " (" + flowProperty + ")"));
	        options.setCredits(new CreditOptions().setEnabled(false));
	        
	        for (ImpactCategoryDescriptor impact : impacts) {
	        	double val = resultProvider.getTotalImpactResult(impact).value;    	
	        	options.addSeries(new SimpleSeries().setName(impact.getName() + " " + "(" + impact.getReferenceUnit() + ")").setData(val));
	        }        
	        
	        options.setxAxis(new Axis().setCategories("Characterization"));
	        options.setyAxis(new Axis().setMin(0).setTitle(new Title("Results")));	
			options.setTooltip(new Tooltip().setFormatter(new Function()
					.setFunction(" return ''+ this.x +': '+ Highcharts.numberFormat(this.y, 2) +' units';")));
				
	        add(new Chart("characterizationChart", options));
		}
		
		public List<ImpactCategoryDescriptor> sortImpactsByAmount(Collection<ImpactCategoryDescriptor> impacts, FullResultProvider resultProvider) {
			List<ImpactCategoryDescriptor> list = new ArrayList<>(impacts);
			Collections.sort(list, new Comparator<ImpactCategoryDescriptor>() {
				@Override
				public int compare(ImpactCategoryDescriptor o1, ImpactCategoryDescriptor o2) {
					double val1 = resultProvider.getTotalImpactResult(o1).value;
					double val2 = resultProvider.getTotalImpactResult(o2).value;
					return Double.compare(val2, val1);
				}
			});
			return list;
		}
		
		public List<ImpactCategoryDescriptor> sortImpactsByName(Collection<ImpactCategoryDescriptor> impacts) {
			List<ImpactCategoryDescriptor> list = new ArrayList<>(impacts);
			Collections.sort(list, new Comparator<ImpactCategoryDescriptor>() {
				@Override
				public int compare(ImpactCategoryDescriptor o1, ImpactCategoryDescriptor o2) {
					return Strings.compare(o1.getName(), o2.getName());
				}
			});
			return list;
		}
		
	}
	
	/**
	 * Panel representing the content panel for the first tab.
	 */
	private static class NormalizationPanel extends Panel
	{
		
		@Inject
		private IDatabase database;
		
		/**
		 * Constructor
		 * 
		 * @param id component id
		 * @param resultProviderKey 
		 * @param setupKey 
		 * @param setup
		 */
		public NormalizationPanel(String id, String setupKey, String resultProviderKey)
		{
			super(id);
			
			// Obtain the cached objects
			CalculationSetup setup = Cache.getAppCache().get(setupKey, CalculationSetup.class);		
			FullResultProvider resultProvider = Cache.getAppCache().get(resultProviderKey, FullResultProvider.class);
			
			ProductSystem productSystem = setup.productSystem;
			ImpactMethodDescriptor impactMethodDescriptor = setup.impactMethod;
			List<ImpactResult> impactResults = resultProvider.getTotalImpactResults();
			NwSetTable nwSetTable = NwSetTable.build(database, setup.nwSet.getId());
			List<ImpactResult> normalizedImpactResults = nwSetTable.applyNormalisation(impactResults);
			List<ContributionItem<ImpactResult>> orderedNormalizedImpactResults = makeContributions(normalizedImpactResults);
			
	        // Add the normalization chart
	        Options options = new Options();
	        options.setChartOptions(new ChartOptions().setType(SeriesType.COLUMN));
	        options.setTitle(new Title("Normalization of: " + productSystem.getName() + ", " + " Impact Method: " + impactMethodDescriptor.getName()));
	        String flowProperty = productSystem.getTargetFlowPropertyFactor().getFlowProperty().getName();
	        options.setSubtitle(new Title("Target value: " + productSystem.getTargetAmount() + " " + productSystem.getTargetUnit().getName() + " (" + flowProperty + ")"));
	        options.setCredits(new CreditOptions().setEnabled(false));
		
	        List<String> categories = new ArrayList<>();
	        List<Number> data = new ArrayList<>();
			for (ContributionItem<ImpactResult> result : orderedNormalizedImpactResults) {			
				categories.add(result.item.impactCategory.getName());
				data.add(result.amount);		
			}
	        
	        options.setxAxis(new Axis().setCategories(categories)
	            .setLabels(new Labels()
	                .setRotation(-45)
	                .setAlign(HorizontalAlignment.RIGHT)
	                .setStyle(new CssStyle()
	                    .setProperty("font-size", "13px")
	                    .setProperty("font-family", "Verdana, sans-serif"))));
	        
	        options.setyAxis(new Axis()
	            .setMin(0)
	            .setTitle(new Title("Normalized impact")));
	        
	        options.setLegend(new Legend(Boolean.FALSE));
	        
	        options.setTooltip(new Tooltip()
	            .setFormatter(new Function()
	                .setFunction("return '<b>'+ this.x +'</b><br/>'+ 'Normalization: '+ Highcharts.numberFormat(this.y, 2) + ' units';")));
	        
	        options.addSeries(new SimpleSeries()
	            .setData(data));
	            
	            /*
	            .setDataLabels(new DataLabels()
	                .setEnabled(Boolean.TRUE)
	                .setRotation(-90)
	                .setColor(new HexColor("#ffffff"))
	                .setAlign(HorizontalAlignment.RIGHT)
	                .setX(-3)
	                .setY(10)
	                .setFormatter(new Function()
	                    .setFunction(" return Highcharts.numberFormat(this.y, 2);"))
	                .setStyle(new CssStyle()
	                    .setProperty("font-size", "13px")
	                    .setProperty("font-family", "Verdana, sans-serif"))));
	        	*/
	            
	        add(new Chart("normalizationChart", options));
	        
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
	
	/**
	 * Panel representing the content panel for the first tab.
	 */
	private static class WeightingPanel extends Panel
	{
		/**
		 * Constructor
		 * 
		 * @param id
		 *            component id
		 */
		public WeightingPanel(String id)
		{
			super(id);
		}
	}
	
}