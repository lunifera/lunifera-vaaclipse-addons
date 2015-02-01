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
package org.lunifera.vaaclipse.addons.application.views;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.lunifera.vaaclipse.addons.common.api.explorer.IExplorerCategory;
import org.lunifera.vaaclipse.addons.common.api.explorer.IExplorerInfo;
import org.lunifera.vaaclipse.addons.common.api.explorer.IExplorerInfoManager;
import org.lunifera.vaaclipse.addons.common.api.explorer.IExplorerLeaf;
import org.lunifera.vaaclipse.addons.common.api.status.IStatusManager;
import org.lunifera.vaaclipse.addons.common.api.status.Status;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * The explorer view is the main entry point for the user to deal with the
 * applications. Applications, processes,... and beeing displayed there.
 */
public class ExplorerView {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ExplorerView.class);

	private Tree tree;
	private Panel panel;

	private IEclipseContext eclipseContext;

	private LazyInfoLoader container;

	@Inject
	public ExplorerView(VerticalLayout parent, IEclipseContext eclipseContext,
			MApplication app) {

		this.eclipseContext = eclipseContext;

		panel = new Panel();
		panel.addStyleName(Reindeer.PANEL_LIGHT);
		panel.setSizeFull();
		tree = new Tree();
		panel.setContent(tree);
		tree.setImmediate(true);

		container = new LazyInfoLoader();
		tree.setContainerDataSource(container);

		parent.addComponent(panel);
	}

	public Object getPlatformComponent() {
		return panel;
	}

	@SuppressWarnings("serial")
	private class LazyInfoLoader extends HierarchicalContainer {

		private Set<IExplorerInfo> resolved = new HashSet<IExplorerInfo>();

		public LazyInfoLoader() {
			resolveChildren(null);
		}

		@Override
		public Collection<?> getChildren(Object itemId) {
			if (!resolved.contains(itemId)) {
				resolveChildren(itemId);
			}
			return super.getChildren(itemId);
		}

		@Override
		public boolean hasChildren(Object itemId) {
			if (!resolved.contains(itemId)) {
				resolveChildren(itemId);
			}
			return super.hasChildren(itemId);
		}

		private void resolveChildren(Object itemId) {
			if (itemId instanceof IExplorerCategory) {
				IExplorerCategory category = (IExplorerCategory) itemId;
				for (IExplorerInfo newInfo : getInfos(category)) {
					addItem(newInfo);
					setParent(newInfo, category);
					if (newInfo instanceof IExplorerLeaf) {
						setChildrenAllowed(newInfo, false);
					}
				}
			}
		}

		/**
		 * Returns the infos as an input for the tree.
		 */
		private Iterable<IExplorerInfo> getInfos(IExplorerCategory parent) {
			ServiceTracker<IExplorerInfoManager, IExplorerInfoManager> tracker = new ServiceTracker<IExplorerInfoManager, IExplorerInfoManager>(
					FrameworkUtil.getBundle(getClass()).getBundleContext(),
					IExplorerInfoManager.class, null);

			try {
				tracker.open();
				IExplorerInfoManager provider = tracker.waitForService(500);
				return provider.getExplorerInfo(parent, eclipseContext);
			} catch (InterruptedException e) {
				LOGGER.error("{}", e);
				eclipseContext.get(IStatusManager.class).addStatus(
						Status.createErrorStatus(e));
			} finally {
				tracker.close();
			}
			return Collections.emptyList();
		}
	}
}
