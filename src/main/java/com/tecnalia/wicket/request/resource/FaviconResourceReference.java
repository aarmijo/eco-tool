package com.tecnalia.wicket.request.resource;

import java.util.Locale;

import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * Static resource reference for favicon resources.
 */
public class FaviconResourceReference extends PackageResourceReference {
  private static final long serialVersionUID = 1L;

  /**
   * Construct.
   * 
   * @param scope
   *          mandatory parameter
   * @param name
   *          mandatory parameter
   * @param locale
   *          resource locale
   * @param style
   *          resource style
   * @param variation
   *          resource variation
   */
  public FaviconResourceReference(Class<?> scope, String name, Locale locale, String style, String variation) {
    super(scope, name, locale, style, variation);
  }

  /**
   * Construct.
   * 
   * @param scope
   *          mandatory parameter
   * @param name
   *          mandatory parameter
   */
  public FaviconResourceReference(Class<?> scope, String name) {
    super(scope, name);
  }

  @Override
  public FaviconPackageResource getResource() {
    return new FaviconPackageResource(getScope(), getName(), getLocale(), getStyle(), getVariation());
  }

}
