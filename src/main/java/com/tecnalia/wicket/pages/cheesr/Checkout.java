package com.tecnalia.wicket.pages.cheesr;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

public class Checkout extends CheesrPage {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings({ "serial" })
	public Checkout() {
	    add(new FeedbackPanel("feedback"));
		Form<?> form = new Form<Void>("form");
		add(form);
		Address address = getCart().getBillingAddress();

		form.add(new TextField<String>("name", new PropertyModel<String>(address, "name")).add(StringValidator.lengthBetween(5, 32)).setRequired(true));

		form.add(new TextField<String>("street", new PropertyModel<String>(address, "street")).setRequired(true));

		form.add(new TextField<Integer>("zipcode", new PropertyModel<Integer>(address, "zipcode")).setRequired(true));

		form.add(new TextField<String>("city", new PropertyModel<String>(address, "city")).setRequired(true));

		form.add(new Link<Void>("cancel") {
			@Override
			public void onClick() {
				setResponsePage(Index.class);
			}
		});
	    form.add(new Button("order") {
	        @Override
	        public void onSubmit() {
	          Cart cart = getCart();

	          System.out.println("Customer name: " + getCart().getBillingAddress().getName());  
	          // charge customer's credit card
	          // ship cheeses to our customer
	          // clean out shopping cart
	          cart.getCheeses().clear();

	          // return to front page
	          setResponsePage(Index.class);
	        }
	      });
	    add(new ShoppingCartPanel("cart", getCart()));
	}
}
