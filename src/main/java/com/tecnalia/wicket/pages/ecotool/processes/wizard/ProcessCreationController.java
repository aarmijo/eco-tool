package com.tecnalia.wicket.pages.ecotool.processes.wizard;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.openlca.core.database.CategoryDao;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.Category;
import org.openlca.core.model.Exchange;
import org.openlca.core.model.Flow;
import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.FlowPropertyFactor;
import org.openlca.core.model.FlowType;
import org.openlca.core.model.Process;
import org.openlca.core.model.ProcessDocumentation;
import org.openlca.core.model.UnitGroup;
import org.openlca.core.model.descriptors.Descriptors;

import com.ibm.icu.util.Calendar;
import com.tecnalia.lca.app.db.Cache;

public class ProcessCreationController {

	// Get logger
	private static final Logger logger = Logger.getLogger(ProcessCreationController.class);
	
	public static void create(ProcessFormModel processFormModel,
			IDatabase database) {		
		try {
			
			Process process = new Process();
			process.setRefId(UUID.randomUUID().toString());
			process.setName(processFormModel.getName());
			process.setDescription(processFormModel.getDescription());
			process.setLastChange(System.currentTimeMillis());
						
			// Create the product flow
			Flow flow;
			flow = new Flow();
			flow.setRefId(UUID.randomUUID().toString());
			flow.setName(processFormModel.getName());
			flow.setDescription(processFormModel.getDescription());
			flow.setFlowType(FlowType.PRODUCT_FLOW);
			FlowProperty property = database.createDao(FlowProperty.class)
					.getForId(processFormModel.getFlowProperty().getId());
			flow.setReferenceFlowProperty(property);
			FlowPropertyFactor factor = new FlowPropertyFactor();
			factor.setConversionFactor(1);
			factor.setFlowProperty(property);
			flow.getFlowPropertyFactors().add(factor);
			database.createDao(Flow.class).insert(flow);
			
			// Add the quantitative reference			
			addQuantitativeReference(process, flow, processFormModel.getUnit().getUnitName(), 
					processFormModel.getAmountValue());
			ProcessDocumentation doc = new ProcessDocumentation();
			doc.setCreationDate(Calendar.getInstance().getTime());
			doc.setId(process.getId());
			process.setDocumentation(doc);
			
			// Save the process			
			CategoryDao categoryDao = new CategoryDao(database);
			Category category = categoryDao.getForName("ProSEco-" + processFormModel.getCategory()).get(0);
			process.setCategory(category);
			process.setLastChange(System.currentTimeMillis());
			database.createDao(Process.class).insert(process);
			Cache.registerNew(Descriptors.toDescriptor(process));
						
			logger.debug("The process " + process.getName() + " was created!");			
			
		} catch (Exception e) {
			throw new RuntimeException("Could not create process", e);
		}
		
	}
	
	private static void addQuantitativeReference(Process process, Flow flow, 
			String unitName, double amountValue) {
		Exchange qRef = new Exchange();
		qRef.setAmountValue(amountValue);
		qRef.setFlow(flow);
		FlowProperty refProp = flow.getReferenceFlowProperty();
		qRef.setFlowPropertyFactor(flow.getReferenceFactor());
		UnitGroup unitGroup = refProp.getUnitGroup();
		if (unitGroup != null)
			qRef.setUnit(unitGroup.getUnit(unitName));
		qRef.setInput(false);
		process.getExchanges().add(qRef);
		process.setQuantitativeReference(qRef);
	}
	
}