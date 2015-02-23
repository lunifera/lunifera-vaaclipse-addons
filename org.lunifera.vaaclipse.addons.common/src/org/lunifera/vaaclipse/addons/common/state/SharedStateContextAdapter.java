/**
 * Copyright (c) 2011 - 2014, Lunifera GmbH (Gross Enzersdorf), Loetz KG (Heidelberg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * 		Florian Pirchner - Initial implementation
 */
package org.lunifera.vaaclipse.addons.common.state;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.lunifera.runtime.common.state.ISharedStateContext;
import org.lunifera.runtime.common.state.ISharedStateContextProvider;
import org.lunifera.vaaclipse.addons.common.api.IE4Constants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This adapter ensures, that the application id is added to the id of the
 * context as prefix. <code>{applicationId}_{id}</code><br>
 * The {@link IE4Constants#APPLICATION_ID} is also added to the OSGi service
 * properties.
 */
public class SharedStateContextAdapter implements ISharedStateContextProvider {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SharedStateContextAdapter.class);

	@Inject
	@Named("e4ApplicationInstanceId")
	String applicationInstanceId;

	public SharedStateContextAdapter() {

	}

	@Override
	public ISharedStateContext getContext(String id,
			Map<String, Object> properties) {
		ISharedStateContextProvider delegate = getDelegate();
		if (delegate == null) {
			LOGGER.error("ISharedStateContextProvider could not be found!");
			return null;
		}

		// add the application id to the OSGi service properties
		properties = properties != null ? properties
				: new HashMap<String, Object>();
		properties.put(IE4Constants.APPLICATION_ID, applicationInstanceId);

		return delegate.getContext(createApplicationId(id), properties);
	}

	private String createApplicationId(String id) {
		return applicationInstanceId + "_" + id;
	}

	@Override
	public void unget(ISharedStateContext context) {
		ISharedStateContextProvider delegate = getDelegate();
		if (delegate == null) {
			LOGGER.error("ISharedStateContextProvider could not be found!");
			return;
		}
		delegate.unget(context);
	}

	protected ISharedStateContextProvider getDelegate() {
		BundleContext context = FrameworkUtil.getBundle(getClass())
				.getBundleContext();
		ServiceReference<ISharedStateContextProvider> reference = context
				.getServiceReference(ISharedStateContextProvider.class);
		return context.getService(reference);
	}

}
