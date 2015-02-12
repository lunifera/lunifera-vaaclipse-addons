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

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.lunifera.vaaclipse.addons.common.api.IE4Constants;
import org.lunifera.vaaclipse.addons.common.api.explorer.AbstractExplorerInfo;
import org.lunifera.vaaclipse.addons.common.api.explorer.IExplorerLeaf;

@SuppressWarnings("restriction")
public class ExplorerApplicationLeaf extends AbstractExplorerInfo implements
		IExplorerLeaf {

	@Inject
	IEclipseContext context;

	public ExplorerApplicationLeaf() {
	}

	@Override
	public void execute(IEclipseContext context) {
		EModelService modelService = context.get(EModelService.class);
		MApplication app = context.get(MApplication.class);
		EPartService partService = context.get(EPartService.class);
		MPartStack stack = (MPartStack) modelService.find(
				IE4Constants.ID__PARTSTACK__DYNAMIC_APPLICATIONS, app);
		MPart part = modelService.createModelElement(MPart.class);
		part.setElementId(getId());
		part.setLabel(getLabel());
		part.setDescription(getDescription());
		part.setIconURI("platform:/plugin/org.lunifera.vaaclipse.addons.application/images/package_explorer.png");
		part.setContributionURI(IE4Constants.BUNDLECLASS_GENERIC_ECVIEW_VIEWPART);
		part.getPersistedState().put(IE4Constants.PROP_INPUT_VIEW_ID, getId());
		part.setCloseable(true);
		part.setOnTop(true);
		part.setToBeRendered(true);
		part.setVisible(true);
		part.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);

		stack.getChildren().add(part); // Add part to stack
		MPart viewPart = partService.showPart(part, PartState.ACTIVATE); // Show
	}
}
