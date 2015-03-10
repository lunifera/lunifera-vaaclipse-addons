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
package org.lunifera.vaaclipse.addons.ecview.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * A view to configure datasources.
 */
public class DatasourceView {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DatasourceView.class);

	@Inject
	private IEclipseContext eclipseContext;
	@Inject
	private MPart mPart;
	@Inject
	private VerticalLayout parentLayout;

	private Table table;

	private BeanItemContainer<Class> datasource;

	@Inject
	public DatasourceView(VerticalLayout parent) {
		
	}

	@PostConstruct
	public void setup() {
		
		table = new Table();
		datasource = new BeanItemContainer<Class>(Class.class);
		table.setContainerDataSource(datasource);
		
		
	}
	
}
