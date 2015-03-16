package com.tecnalia.wicket.markup.head;


import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.lang.Args;

import com.tecnalia.wicket.core.util.string.FaviconUtils;



/**
 * Base class for all {@link HeaderItem}s that represent favicons. This class
 * mainly contains factory methods.
 * 
 * @author schroederr
 */
public abstract class FaviconHeaderItem extends HeaderItem {

  /**
   * Creates a {@link FaviconReferenceHeaderItem} for the given reference.
   * 
   * @param reference
   *          a reference to a FavIcon resource
   * @return A newly created {@link FaviconReferenceHeaderItem} for the given
   *         reference.
   */
  public static FaviconReferenceHeaderItem forReference(ResourceReference reference) {
    return forReference(reference, null);
  }

  /**
   * Creates a {@link FaviconReferenceHeaderItem} for the given reference.
   * 
   * @param reference
   *          a reference to a FavIcon resource
   * @param pageParameters
   *          the parameters for this FavIcon resource reference
   * @return A newly created {@link FaviconReferenceHeaderItem} for the given
   *         reference.
   */
  public static FaviconReferenceHeaderItem forReference(ResourceReference reference, PageParameters pageParameters) {
    return new FaviconReferenceHeaderItem(reference, pageParameters);
  }

  /**
   * Creates a {@link FaviconUrlReferenceHeaderItem} for the given url.
   * 
   * @param url
   *          context-relative url of the FavIcon resource
   * @return A newly created {@link FaviconUrlReferenceHeaderItem} for the given
   *         url.
   */
  public static FaviconUrlReferenceHeaderItem forUrl(String url) {
    return new FaviconUrlReferenceHeaderItem(url);
  }

  protected final void internalRenderFavIconReference(Response response, String url) {
    Args.notEmpty(url, "url");
    FaviconUtils.writeLinkUrl(response, url);
    response.write("\n");
  }

}
