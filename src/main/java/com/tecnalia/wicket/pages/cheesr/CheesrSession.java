package com.tecnalia.wicket.pages.cheesr;

import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;

public class CheesrSession extends WebSession {
  private Cart cart = new Cart();

  public CheesrSession(Request request) {
    super(request);
  }

  public Cart getCart() {
    return cart;
  }
}
