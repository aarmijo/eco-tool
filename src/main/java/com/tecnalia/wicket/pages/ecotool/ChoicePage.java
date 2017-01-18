/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tecnalia.wicket.pages.ecotool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.openlca.core.database.ImpactMethodDao;
import org.openlca.core.database.ProductSystemDao;
import org.openlca.core.database.derby.DerbyDatabase;
import org.openlca.core.model.descriptors.ImpactMethodDescriptor;
import org.openlca.core.model.descriptors.ProductSystemDescriptor;
import org.wicketstuff.annotation.mount.MountPath;

import com.tecnalia.lca.app.db.Database;
import com.tecnalia.lca.app.db.DatabaseList;
import com.tecnalia.lca.app.db.DerbyConfiguration;
import com.tecnalia.wicket.pages.ecotool.behavior.DisableComponentListener;

/**
 * Linked select boxes example
 * 
 * @author Igor Vaynberg (ivaynberg), Alberto Armijo
 */
@MountPath(value = "/choicepage")
public class ChoicePage extends EcoToolBasePage
{
	private String selectedDb;
	private ProductSystemDescriptor selectedPss;
	private ImpactMethodDescriptor selectedImpactMethod;
	private Integer selectedTargetAmount = 1;

	/**
	 * @param selectedTargetAmount
	 *            The target amount that is currently selected
	 */
	public void setSelectedTargetAmount(Integer selectedTargetAmount) {
		this.selectedTargetAmount = selectedTargetAmount;
	}

	/**
	 * @return Currently selected target amount
	 */
	public Integer getSelectedTargetAmount() {
		return selectedTargetAmount;
	}

	/**
	 * @return Currently selected impact method
	 */
	public ImpactMethodDescriptor getSelectedImpactMethod() {
		return selectedImpactMethod;
	}

	/**
	 * @param selectedImpactMethod
	 *            The impact method that is currently selected
	 */
	public void setSelectedImpactMethod(ImpactMethodDescriptor selectedImpactMethod) {
		this.selectedImpactMethod = selectedImpactMethod;
	}

	/**
	 * @return Currently selected PSS
	 */
	public ProductSystemDescriptor getSelectedPss() {
		return selectedPss;
	}

	/**
	 * @param selectedPss
	 *            The PSS that is currently selected
	 */
	public void setSelectedPss(ProductSystemDescriptor selectedPss) {
		this.selectedPss = selectedPss;
	}

	/**
	 * @return Currently selected database
	 */
	public String getSelectedDb()
	{
		return selectedDb;
	}

	/**
	 * @param selectedDb
	 *            The db that is currently selected
	 */
	public void setSelectedDb(String selectedDb)
	{
		this.selectedDb = selectedDb;
	}

	/**
	 * Constructor.
	 */
	public ChoicePage()
	{
		
		IModel<List<? extends String>> databaseChoices = new AbstractReadOnlyModel<List<? extends String>>()
		{
			@Override
			public List<String> getObject()
			{
				List<String> dbNames = new ArrayList<String>();
				DatabaseList dbList = Database.getConfigurations();		
				List<DerbyConfiguration> dbConfigs = dbList.getLocalDatabases();
				for (DerbyConfiguration dbConfig : dbConfigs) {
					dbNames.add(dbConfig.getName());
				}
				return dbNames;
			}
		};

		// Model for the product system choices
		IModel<List<ProductSystemDescriptor>> pssChoices = new AbstractReadOnlyModel<List<ProductSystemDescriptor>>()
		{
			@Override
			public List<ProductSystemDescriptor> getObject()
			{
				if (selectedDb == null) {
					return Collections.emptyList(); 
				}		
				DerbyDatabase database = null;
				try {
					DerbyConfiguration derbyConfiguration = new DerbyConfiguration();
					derbyConfiguration.setName(selectedDb);					
					database = derbyConfiguration.createInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
				List<ProductSystemDescriptor> productSystemDescriptors = new ProductSystemDao(database).getDescriptors();				
				return productSystemDescriptors;
			}
		};
		
		IChoiceRenderer<ProductSystemDescriptor> pssChoicesRenderer = new IChoiceRenderer<ProductSystemDescriptor>() {

			@Override
			public Object getDisplayValue(ProductSystemDescriptor object) {				
				return object.getName();
			}

			@Override
			public String getIdValue(ProductSystemDescriptor object, int index) {				
				return Integer.toString(index);
			}

			@Override
			public ProductSystemDescriptor getObject(String id,	IModel<? extends List<? extends ProductSystemDescriptor>> choices) {
				return choices.getObject().get(0);
			}
		};		
		
		// Model for the impact method choices
		IModel<List<ImpactMethodDescriptor>> impactMethodChoices = new AbstractReadOnlyModel<List<ImpactMethodDescriptor>>()
		{
			@Override
			public List<ImpactMethodDescriptor> getObject()
			{
				if (selectedPss == null) {
					return Collections.emptyList(); 
				}
				DerbyDatabase database = null;
				try {
					DerbyConfiguration derbyConfiguration = new DerbyConfiguration();
					derbyConfiguration.setName(selectedDb);					
					database = derbyConfiguration.createInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
				List<ImpactMethodDescriptor> impactMethodDescriptors = new ImpactMethodDao(database).getDescriptors();				
				return impactMethodDescriptors;
			}
		};
		
		IChoiceRenderer<ImpactMethodDescriptor> impactMethodChoicesRenderer = new IChoiceRenderer<ImpactMethodDescriptor>() {

			@Override
			public Object getDisplayValue(ImpactMethodDescriptor object) {				
				return object.getName();
			}

			@Override
			public String getIdValue(ImpactMethodDescriptor object, int index) {				
				return Integer.toString(index);
			}

			@Override
			public ImpactMethodDescriptor getObject(String id, IModel<? extends List<? extends ImpactMethodDescriptor>> choices) {				
				return choices.getObject().get(0);
			}
		};	
		
		Form<?> form = new Form("form");
		add(form);

		final DropDownChoice<String> databases = new DropDownChoice<String>("databases",
			new PropertyModel<String>(this, "selectedDb"), databaseChoices);
		databases.setRequired(true);
		
		final DropDownChoice<ProductSystemDescriptor> pss = new DropDownChoice<ProductSystemDescriptor>("pss",
			new PropertyModel<ProductSystemDescriptor>(this, "selectedPss"), pssChoices, pssChoicesRenderer);
		pss.setOutputMarkupId(true);
		pss.setRequired(true);
		
		final DropDownChoice<ImpactMethodDescriptor> impacts = new DropDownChoice<ImpactMethodDescriptor>("impacts",
			new PropertyModel<ImpactMethodDescriptor>(this, "selectedImpactMethod"), impactMethodChoices, impactMethodChoicesRenderer);
		impacts.setOutputMarkupId(true);
		impacts.setRequired(true);
		
		final TextField<Integer> targetAmount = new TextField<Integer>("targetamount", new PropertyModel<Integer>(this, "selectedTargetAmount"));
		targetAmount.setRequired(true);		
		
		form.add(databases);
		form.add(pss);
		form.add(impacts);
		form.add(targetAmount);

		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		feedback.setOutputMarkupId(true);
		add(feedback);

		form.add(new AjaxButton("go")
		{
			@Override
			protected void onAfterSubmit(AjaxRequestTarget target, Form<?> form)
			{
				super.onAfterSubmit(target, form);
				info("You have selected: " + databases.getModelObject() + " " + getSelectedPss() + " " + getSelectedImpactMethod() + " " + selectedTargetAmount);
				target.add(feedback);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {				
				super.onError(target, form);
				target.add(feedback);
			}
		});

		// Render the pss dropdown when the databases dropdown changes
		databases.add(new AjaxFormComponentUpdatingBehavior("change")
		{

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(new DisableComponentListener(pss));
				attributes.getAjaxCallListeners().add(new DisableComponentListener(impacts));
			}

			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				target.add(pss);
			}
		});
		
		// Render the impacts dropdown when the pss dropdown changes
		pss.add(new AjaxFormComponentUpdatingBehavior("change")
		{

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(new DisableComponentListener(impacts));
			}

			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				target.add(impacts);
			}
		});
	}
}
