package com.tecnalia.wicket.pages.ecotool.systems.wizard;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.wicket.PageReference;
import org.openlca.core.database.BaseDao;
import org.openlca.core.database.IDatabase;
import org.openlca.core.matrix.ProductSystemBuilder;
import org.openlca.core.matrix.cache.MatrixCache;
import org.openlca.core.model.Exchange;
import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.Process;
import org.openlca.core.model.ProductSystem;
import org.openlca.core.model.UnitGroup;
import org.openlca.core.model.descriptors.Descriptors;

import com.tecnalia.lca.app.db.Cache;
import com.tecnalia.wicket.pages.ecotool.systems.ProductSystemEditor;

public class ProductSystemController {

	// Get logger
	private static final Logger logger = Logger.getLogger(ProductSystemController.class);
	
	/**
	 * @param productSystemFormModel the form model for the create product system wizard
	 * @param database database
	 * @param cutoff cutoff value. A null value disables the cutoff calculation
	 */
	public static void create(ProductSystemFormModel productSystemFormModel,
			IDatabase database, Double cutoff) {		
		
		// Set product system name and description
		ProductSystem productSystem = new ProductSystem();
		productSystem.setRefId(UUID.randomUUID().toString());
		productSystem.setName(productSystemFormModel.getName());
		productSystem.setDescription(productSystemFormModel.getDescription());
		
		long refProcessId = productSystemFormModel.getProcess().getId();
		Process refProcess = database.createDao(Process.class).getForId(refProcessId);		
		
		try {
			// Set the reference process
			productSystem.getProcesses().add(refProcessId);
			productSystem.setReferenceProcess(refProcess);
			Exchange qRef = refProcess.getQuantitativeReference();
			productSystem.setReferenceExchange(qRef);
			
			// Set target unit
			FlowProperty property = qRef.getFlowPropertyFactor().getFlowProperty();
			UnitGroup unitGroup = property.getUnitGroup();
			productSystem.setTargetUnit(unitGroup.getUnit(productSystemFormModel.getUnit().getUnitName()));
			
			// Set target amount value
			productSystem.setTargetFlowPropertyFactor(qRef.getFlowPropertyFactor());
			productSystem.setTargetAmount(productSystemFormModel.getAmountValue());
		} catch (final Exception e) {
			logger.error("Loading reference process failed / no selected", e);
		}
		
		try {
			database.createDao(ProductSystem.class).insert(productSystem);
			Cache.registerNew(Descriptors.toDescriptor(productSystem));
		} catch (Exception e) {
			logger.error("Failed to create product system", e);
		}
		
		try {
			ProductSystemBuilder builder = null;
			MatrixCache cache = Cache.getMatrixCache();
			if (cutoff == null)
				builder = new ProductSystemBuilder(cache, true);
			else
				builder = new ProductSystemBuilder(cache, true);
				builder.setCutoff(cutoff);
			productSystem = builder.autoComplete(productSystem);
			logger.debug("Product system: " + productSystem.getName() + " Auto-completed!");
		} catch (Exception e) {
			logger.error("Failed to auto-complete product system", e);			
		}		
	}

	public static void update(ProductSystemFormModel productSystemFormModel,
			IDatabase database, PageReference pageReference) {
		
		// Update the product system with the new unit and amount
		long productSystemId = ((ProductSystemEditor) pageReference.getPage()).getProductSystemDescriptor().getId();
		BaseDao<ProductSystem> productSystemDao = database.createDao(ProductSystem.class);
		ProductSystem productSystem = productSystemDao.getForId(productSystemId);
		
		Exchange qRef = productSystem.getReferenceExchange();
		FlowProperty property = qRef.getFlowPropertyFactor().getFlowProperty();
		UnitGroup unitGroup = property.getUnitGroup();		
		productSystem.setTargetUnit(unitGroup.getUnit(productSystemFormModel.getUnit().getUnitName()));
		
		productSystem.setTargetAmount(productSystemFormModel.getAmountValue());
		
		// Persist the product system
		productSystemDao.update(productSystem);
		// TODO handle Cache in edits
		logger.debug("Product system: " + productSystem.getName() + " was updated!");		
	}	
}