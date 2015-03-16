package com.tecnalia.wicket.core.util.string;

import org.apache.wicket.request.Response;
import org.apache.wicket.util.string.Strings;

/**
 * Utility methods for FavIcon.
 */
public final class FaviconUtils {
  /**
   * Hidden constructor.
   */
  private FaviconUtils() {}

  /**
   * Writes a reference to a favicon file in the response object
   * 
   * @param response
   *          the response to write to
   * @param url
   *          the url of the favicon reference
   */
  public static void writeLinkUrl(final Response response, final CharSequence url) {
    response.write("<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"");
    response.write(Strings.escapeMarkup(url));
    response.write("\" />");
  }
}
