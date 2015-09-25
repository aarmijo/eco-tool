package com.tecnalia.wicket.pages.ecotool.processes.editor;

import java.util.ArrayList;
import java.util.List;

import org.openlca.core.model.Exchange;
import org.openlca.core.model.FlowType;
import org.openlca.core.model.Process;

public class ExchangeList {

	private List<ExchangeDescriptor> exchangeList;


	public ExchangeList(Process process, boolean input) {
		List<ExchangeDescriptor> exchangeDescriptorList = new ArrayList<ExchangeDescriptor>();	
		
		List<Exchange> exchangeList = process.getExchanges();
		for (Exchange exchange : exchangeList) {
			if (exchange.isInput() == input && exchange.getFlow().getFlowType().equals(FlowType.PRODUCT_FLOW)) {
				ExchangeDescriptor exchangeDescriptor = new ExchangeDescriptor(exchange);			
				exchangeDescriptorList.add(exchangeDescriptor);
			}
		}
			this.exchangeList = exchangeDescriptorList;
	}

	public List<ExchangeDescriptor> getExchangeList() {
		return exchangeList;
	}
}