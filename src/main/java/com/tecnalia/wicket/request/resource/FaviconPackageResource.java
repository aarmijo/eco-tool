package com.tecnalia.wicket.request.resource;

import java.util.Locale;

import org.apache.wicket.request.resource.PackageResource;

/**
 * Package resource for FavIcon files.
 */
public class FaviconPackageResource extends PackageResource {
  private static final long serialVersionUID = 1L;

  /**
   * Construct.
   * 
   * @param scope
   * @param name
   * @param locale
   * @param style
   * @param variation
   */
  public FaviconPackageResource(Class<?> scope, String name, Locale locale, String style, String variation) {
    super(scope, name, locale, style, variation);
  }

}
