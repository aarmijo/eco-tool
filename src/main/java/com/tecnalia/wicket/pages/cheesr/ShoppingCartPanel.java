package com.tecnalia.wicket.pages.cheesr;

import java.text.NumberFormat;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class ShoppingCartPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private Cart cart;

	public ShoppingCartPanel(String id, Cart cart) {
		super(id);
		this.cart = cart;
		add(new ListView<Cheese>("cart", new PropertyModel<List<Cheese>>(this,
				"cart.cheeses")) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Cheese> item) {
				Cheese cheese = item.getModelObject();
				item.add(new Label("name", cheese.getName()));
				item.add(new Label("price", "$" + cheese.getPrice()));

				item.add(removeLink("remove", item));
			}
		});
		add(new Label("total", new Model<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				NumberFormat nf = NumberFormat.getCurrencyInstance();
				return nf.format(getCart().getTotal());
			}
		}));
	}

	private Cart getCart() {
		return cart;
	}
}