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

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.lunifera.runtime.common.validation.Status;
import org.lunifera.vaaclipse.addons.common.api.explorer.IExplorerCategory;
import org.lunifera.vaaclipse.addons.common.api.explorer.IExplorerInfo;
import org.lunifera.vaaclipse.addons.common.api.explorer.IExplorerInfoManager;
import org.lunifera.vaaclipse.addons.common.api.explorer.IExplorerLeaf;
import org.lunifera.vaaclipse.addons.common.api.status.IStatusManager;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.Resource;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * The explorer view is the main entry point for the user to deal with the
 * applications. Applications, processes,... and beeing displayed there.
 */
@SuppressWarnings("serial")
public class ExplorerView implements ItemClickListener, ExpandListener {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ExplorerView.class);

	private Tree tree;
	private Panel panel;

	private IEclipseContext eclipseContext;

	private LazyInfoLoader container;

	private Set<IExplorerInfo> resolved = new HashSet<IExplorerInfo>();

	@Inject
	public ExplorerView(VerticalLayout parent, IEclipseContext eclipseContext,
			MApplication app) {

		this.eclipseContext = eclipseContext;

		panel = new Panel();
		panel.addStyleName(Reindeer.PANEL_LIGHT);
		panel.setSizeFull();
		tree = new Tree();
		tree.setImmediate(true);
		panel.setContent(tree);

		container = new LazyInfoLoader();
		tree.setContainerDataSource(container);
		tree.setItemCaptionPropertyId(IExplorerInfo.PROP__LABEL);
		tree.setItemIconPropertyId(IExplorerInfo.PROP__ICON);

		tree.addItemClickListener(this);
		tree.addExpandListener(this);

		parent.addComponent(panel);
	}

	@Override
	public void nodeExpand(ExpandEvent event) {
		resolveChildren((IExplorerInfo) event.getItemId());
	}

	@SuppressWarnings("unchecked")
	private void resolveChildren(IExplorerInfo itemId) {
		try {
			if (itemId instanceof IExplorerCategory) {
				IExplorerCategory category = (IExplorerCategory) itemId;
				// access the categories getChildren method. The category
				// will lazy load the childrens.
				for (IExplorerInfo newInfo : ((IExplorerCategory) itemId)
						.getChildren()) {
					Item item = container.addItem(newInfo);
					if (item == null) {
						continue;
					}
					item.getItemProperty("label").setValue(newInfo.getLabel());
					item.getItemProperty("icon").setValue(newInfo.getIcon());
					container.setParent(newInfo, category);
					if (newInfo instanceof IExplorerLeaf) {
						container.setChildrenAllowed(newInfo, false);
					}
				}
			}
		} finally {
			resolved.add(itemId);
		}
	}

	public Object getPlatformComponent() {
		return panel;
	}

	@Override
	public void itemClick(ItemClickEvent event) {
		IExplorerInfo info = (IExplorerInfo) event.getItemId();
		if (info instanceof IExplorerLeaf) {
			IExplorerLeaf leaf = (IExplorerLeaf) info;
			leaf.execute(eclipseContext);
		}
	}

	private class LazyInfoLoader extends HierarchicalContainer {

		public LazyInfoLoader() {
			addContainerProperty("label", String.class, "");
			addContainerProperty("icon", Resource.class, null);

			loadRootElements();
		}

		/**
		 * Returns the infos as an input for the tree.
		 */
		@SuppressWarnings("unchecked")
		private void loadRootElements() {
			ServiceTracker<IExplorerInfoManager, IExplorerInfoManager> tracker = new ServiceTracker<IExplorerInfoManager, IExplorerInfoManager>(
					FrameworkUtil.getBundle(getClass()).getBundleContext(),
					IExplorerInfoManager.class, null);

			try {
				tracker.open();
				IExplorerInfoManager provider = tracker.waitForService(500);
				if (provider == null) {
					return;
				}
				for (IExplorerInfo newInfo : provider.getExplorerInfo(null,
						eclipseContext)) {
					Item item = addItem(newInfo);
					item.getItemProperty("label").setValue(newInfo.getLabel());
					item.getItemProperty("icon").setValue(newInfo.getIcon());
					if (newInfo instanceof IExplorerLeaf) {
						setChildrenAllowed(newInfo, false);
					}
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
}
