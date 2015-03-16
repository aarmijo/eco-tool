package com.tecnalia.wicket.request.resource;

/**
 * TODO: document
 *
 * @author miha
 * @version 1.0
 */
public class HomeFaviconResourceReference extends FaviconResourceReference {

    public static final HomeFaviconResourceReference INSTANCE = new HomeFaviconResourceReference("favicon.ico");

    private HomeFaviconResourceReference(String name) {
        super(HomeFaviconResourceReference.class, name);
    }
}
