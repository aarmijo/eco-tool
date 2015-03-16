package com.tecnalia.wicket.pages.ecotool;

import java.util.List;

import org.apache.wicket.markup.html.WebPage;

public abstract class CheesrPage extends WebPage {
  public EcoToolSession getCheesrSession() {
    return (EcoToolSession) getSession();
  }

  public Cart getCart() {
    return getCheesrSession().getCart();
  }

  public List<Cheese> getCheeses() {
    return EcoToolApplication.get().getCheeses();
  }
}
