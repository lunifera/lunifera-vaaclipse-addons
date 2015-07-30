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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.lunifera.runtime.common.validation.IStatus;
import org.lunifera.vaaclipse.addons.common.api.status.IStatusManager;
import org.lunifera.vaaclipse.addons.common.api.status.IStatusScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * View that shows problems.
 */
public class ProblemsView {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ProblemsView.class);

	private final VerticalLayout parent;
	@SuppressWarnings("unused")
	private final IEclipseContext eclipseContext;

	@Inject
	private IStatusManager statusManager;

	private Table table;

	private BeanItemContainer<IStatus> container;

	@Inject
	public ProblemsView(VerticalLayout parent, IEclipseContext eclipseContext,
			MApplication app) {
		this.parent = parent;
		this.eclipseContext = eclipseContext;
	}

	@PostConstruct
	protected void init() {

		table = new Table();
		table.setSelectable(true);
		table.setSizeFull();
		parent.addComponent(table);

		container = new BeanItemContainer<IStatus>(IStatus.class);
		table.setContainerDataSource(container);

		refreshContent();
	}

	protected void refreshContent() {
		container.removeAllItems();

		IStatusScope scope = statusManager.getActiveScope();
		if (scope != null) {
			container.addAll(scope.getAllStatus());
		}
	}

}
