package com.tecnalia.wicket.markup.head;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IReferenceHeaderItem;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.resource.bundles.IResourceBundle;
import org.apache.wicket.util.string.Strings;

/**
 * {@link HeaderItem} for style tags that are rendered using a {@link ResourceReference}.
 * @author schroederr
 */
public class FaviconReferenceHeaderItem extends FaviconHeaderItem implements IReferenceHeaderItem {
  private final ResourceReference reference;
  private final PageParameters pageParameters;

  /**
   * Creates a new {@code FavIconReferenceHeaderItem}.
   * 
   * @param reference
   *          resource reference pointing to the FavIcon resource
   * @param pageParameters
   *          the parameters for this FavIcon resource reference
   */
  public FaviconReferenceHeaderItem(ResourceReference reference, PageParameters pageParameters) {
    super();
    this.reference = reference;
    this.pageParameters = pageParameters;
  }

  /**
   * @return resource reference pointing to the FavIcon resource
   * @see IReferenceHeaderItem#getReference()
   */
  @Override
public ResourceReference getReference() {
    return reference;
  }

  /**
   * @return the parameters for this FavIcon resource reference
   */
  public PageParameters getPageParameters() {
    return pageParameters;
  }

  @Override
  public List<HeaderItem> getDependencies() {
    return getReference().getDependencies();
  }

  @Override
  public Iterable<? extends HeaderItem> getProvidedResources() {
    if (getReference() instanceof IResourceBundle)
      return ((IResourceBundle) getReference()).getProvidedResources();
    return super.getProvidedResources();
  }

  @Override
  public void render(Response response) {
    internalRenderFavIconReference(response, getUrl());
  }

  @Override
  public Iterable<?> getRenderTokens() {
    return Arrays.asList("favicon-" + Strings.stripJSessionId(getUrl()));
  }

  @Override
  public String toString() {
    return "FavIconReferenceHeaderItem(" + getReference() + ", " + getPageParameters() + ")";
  }

  private String getUrl() {
    IRequestHandler handler = new ResourceReferenceRequestHandler(getReference(), getPageParameters());
    return RequestCycle.get().urlFor(handler).toString();
  }

  @Override
  public int hashCode() {
    return getReference().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof FaviconReferenceHeaderItem)
      return ((FaviconReferenceHeaderItem) obj).getReference().equals(getReference());
    return false;
  }
}
