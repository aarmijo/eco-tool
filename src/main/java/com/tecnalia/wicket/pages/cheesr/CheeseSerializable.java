package com.tecnalia.wicket.pages.cheesr;

import java.io.Serializable;

public class CheeseSerializable extends Cheese implements Serializable {

	private static final long serialVersionUID = 1L;

	public CheeseSerializable(String name, String description, double price) {
		super(name, description, price);
	}
}
