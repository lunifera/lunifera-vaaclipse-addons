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
package org.lunifera.vaaclipse.addons.ecview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.lunifera.runtime.common.validation.Status;
import org.lunifera.vaaclipse.addons.common.api.explorer.AbstractExplorerInfo;
import org.lunifera.vaaclipse.addons.common.api.explorer.IExplorerCategory;
import org.lunifera.vaaclipse.addons.common.api.explorer.IExplorerInfo;
import org.lunifera.vaaclipse.addons.common.api.explorer.IExplorerInfoManager;
import org.lunifera.vaaclipse.addons.common.api.status.IStatusManager;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExplorerCategory extends AbstractExplorerInfo implements
		IExplorerCategory {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ExplorerCategory.class);

	private final IEclipseContext eclipseContext;

	protected IExplorerCategory parentCategory;
	protected List<IExplorerInfo> children;
	protected boolean lazyHasChildren;

	public ExplorerCategory(IEclipseContext eclipseContext) {
		this.eclipseContext = eclipseContext;
	}

	@Override
	public IExplorerCategory getParentCategory() {
		return parentCategory;
	}

	@Override
	public List<IExplorerInfo> getChildren() {
		if (children == null) {
			loadChilds();
		}
		return children != null ? Collections.unmodifiableList(children)
				: Collections.<IExplorerInfo> emptyList();
	}

	private boolean addChild(IExplorerInfo info) {
		return children.add(info);
	}

	/**
	 * Returns the infos as an input for the tree.
	 */
	private void loadChilds() {
		if (children == null) {
			children = new ArrayList<IExplorerInfo>();
		} else {
			return;
		}

		ServiceTracker<IExplorerInfoManager, IExplorerInfoManager> tracker = new ServiceTracker<IExplorerInfoManager, IExplorerInfoManager>(
				FrameworkUtil.getBundle(getClass()).getBundleContext(),
				IExplorerInfoManager.class, null);

		try {
			tracker.open();
			IExplorerInfoManager provider = tracker.waitForService(500);
			for (IExplorerInfo info : provider.getExplorerInfo(this,
					eclipseContext)) {
				addChild(info);
			}
		} catch (InterruptedException e) {
			LOGGER.error("{}", e);
			eclipseContext.get(IStatusManager.class).getActiveScope()
					.addStatus(Status.createErrorStatus(e));
		} finally {
			tracker.close();
		}
	}
}