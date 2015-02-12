package org.lunifera.vaaclipse.addons.common.api;

import org.semanticsoft.vaaclipse.publicapi.resources.BundleResource;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;

/**
 * A util that deals with vaadin resources.
 */
public class ResourceUtil {

	/**
	 * Returns the proper resource for the given uri.
	 * 
	 * @param iconURI
	 * @return
	 */
	public static Resource getResource(String iconURI) {
		if (iconURI.startsWith("bundleclass")) {
			return BundleResource.valueOf(iconURI);
		} else if (iconURI.startsWith("theme:/")) {
			return new ThemeResource(iconURI.replace("theme:/", ""));
		} else if (iconURI.startsWith("http")) {
			return new ExternalResource(iconURI);
		}

		return new ThemeResource(iconURI);
	}

}
