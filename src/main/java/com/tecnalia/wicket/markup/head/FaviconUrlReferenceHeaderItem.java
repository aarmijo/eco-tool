package com.tecnalia.wicket.markup.head;

import java.util.Arrays;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.UrlUtils;
import org.apache.wicket.request.cycle.RequestCycle;

/**
 * {@link HeaderItem} for favicon links that are rendered using a fixed URL, for
 * example resources from an external site or context relative urls.
 * 
 * @author schroederr
 */
public class FaviconUrlReferenceHeaderItem extends FaviconHeaderItem {
  private final String url;

  /**
   * Creates a new {@code FavIconUrlReferenceHeaderItem}.
   * 
   * @param url
   *          context-relative url of the FavIcon resource
   */
  public FaviconUrlReferenceHeaderItem(String url) {
    super();
    this.url = url;
  }

  /**
   * @return context-relative url of the FavIcon resource
   */
  public String getUrl() {
    return url;
  }

  @Override
  public void render(Response response) {
    internalRenderFavIconReference(response, UrlUtils.rewriteToContextRelative(getUrl(), RequestCycle.get()));
  }

  @Override
  public Iterable<?> getRenderTokens() {
    return Arrays.asList("favicon-" + UrlUtils.rewriteToContextRelative(getUrl(), RequestCycle.get()));
  }

  @Override
  public String toString() {
    return "FavIconUrlReferenceHeaderItem(" + getUrl() + ")";
  }

  @Override
  public int hashCode() {
    return getUrl().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof FaviconUrlReferenceHeaderItem)
      return ((FaviconUrlReferenceHeaderItem) obj).getUrl().equals(getUrl());
    return false;
  }
}
