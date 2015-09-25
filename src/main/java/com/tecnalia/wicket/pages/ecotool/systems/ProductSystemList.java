package com.tecnalia.wicket.pages.ecotool.systems;

import java.util.ArrayList;
import java.util.List;

import org.openlca.core.database.ProductSystemDao;
import org.openlca.core.model.ProductSystem;

public class ProductSystemList {

	private List<ProductSystemDescriptor> productSystemList;

	public ProductSystemList () {		
	}

	public ProductSystemList(ProductSystemDao productSystemDao) {
		List<ProductSystemDescriptor> productSystemDescriptorList = new ArrayList<ProductSystemDescriptor>();	
		
		List<ProductSystem> productSystemList = productSystemDao.getAll();
		for (ProductSystem productSystem : productSystemList) {
			ProductSystemDescriptor productSystemDescriptor = new ProductSystemDescriptor(productSystem);			
			productSystemDescriptorList.add(productSystemDescriptor);
		}
			this.productSystemList = productSystemDescriptorList;
	}

	public List<ProductSystemDescriptor> getProductSystemList() {
		return productSystemList;
	}
}