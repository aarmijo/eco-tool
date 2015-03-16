package com.tecnalia.wicket.assets.base;

import org.apache.wicket.request.resource.CssResourceReference;

/**
 * TODO: document
 *
 * @author miha
 * @version 1.0
 */
public class HomeCssResourceReference extends CssResourceReference {

    public static final HomeCssResourceReference INSTANCE = new HomeCssResourceReference("home.css");

    private HomeCssResourceReference(String name) {
        super(HomeCssResourceReference.class, name);
    }
}
