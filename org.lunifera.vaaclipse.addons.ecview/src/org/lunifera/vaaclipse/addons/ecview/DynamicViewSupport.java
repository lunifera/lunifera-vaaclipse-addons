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
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.lunifera.dsl.dto.lib.impl.DtoServiceAccess;
import org.lunifera.dsl.dto.lib.services.IDTOService;
import org.lunifera.ecview.core.common.context.IViewContext;
import org.lunifera.ecview.core.common.model.core.YBeanSlot;
import org.lunifera.ecview.core.common.model.core.YView;
import org.lunifera.ecview.xtext.builder.participant.IECViewAddonsMetadataService;
import org.lunifera.runtime.common.state.ISharedStateContext;
import org.lunifera.runtime.common.state.ISharedStateContextProvider;
import org.lunifera.vaaclipse.addons.common.api.IE4Constants;
import org.lunifera.vaaclipse.addons.ecview.impl.Activator;
import org.osgi.util.tracker.ServiceTracker;

public class DynamicViewSupport {

	@Inject
	private ISharedStateContextProvider sharedStateProvider;

	public DynamicViewSupport() {
	}

	/**
	 * Opens a new generic ECView view.
	 * 
	 * @param id
	 * @param label
	 * @param description
	 * @param iconURI
	 * @param context
	 */
	public void openNewGenericECViewView(String id, String label,
			String description, String iconURI, IEclipseContext context) {

		EModelService modelService = context.get(EModelService.class);
		MApplication app = context.get(MApplication.class);
		EPartService partService = context.get(EPartService.class);

		MPartStack stack = (MPartStack) modelService.find(
				IE4Constants.ID__PARTSTACK__DYNAMIC_APPLICATIONS, app);
		MPart part = modelService.createModelElement(MPart.class);
		part.setElementId(id);
		part.setLabel(label);
		part.setDescription(description);
		part.setIconURI(iconURI);
		part.setContributionURI(IE4Constants.BUNDLECLASS_GENERIC_ECVIEW_VIEWPART);
		part.setCloseable(true);
		part.setOnTop(true);
		part.setToBeRendered(true);
		part.setVisible(true);
		part.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
		MToolBar mToolbar = MMenuFactory.INSTANCE.createToolBar();
		part.setToolbar(mToolbar);

		//
		// create a child context and configure with the view model
		//
		createPartContext(part, context, id);

		stack.getChildren().add(part); // Add part to stack

		// show the part
		partService.showPart(part, PartState.ACTIVATE); // Show
	}

	/**
	 * Creates a child context for the part.
	 * 
	 * @param part
	 * @param context
	 * @param id
	 */
	protected void createPartContext(MPart part, IEclipseContext context,
			String id) {
		IEclipseContext partContext = context.createChild();

		// initialize the context -> so no delegation to OSGi services is
		// processed later
		partContext.set(YView.class, null);
		partContext.set(ISharedStateContext.class, null);
		partContext.set(IDTOService.class, null);

		YView yView = findViewModel(id);
		partContext.set(YView.class, yView);

		if (yView != null) {
			// create shared state
			if (yView.getSharedStateGroup() != null
					&& !yView.getSharedStateGroup().trim().equals("")) {
				ISharedStateContext sharedState = sharedStateProvider
						.getContext(yView.getSharedStateGroup(), null);
				partContext.set(ISharedStateContext.class, sharedState);
			}

			// put the main DTO service into the context
			YBeanSlot yBeanSlot = yView
					.getBeanSlot(IViewContext.MAIN_BEAN_SLOT);
			if (yBeanSlot != null) {
				@SuppressWarnings({ "restriction", "unchecked" })
				IDTOService<Object> dtoService = (IDTOService<Object>) DtoServiceAccess
						.getService(yBeanSlot.getValueType());
				partContext.set(IDTOService.class, dtoService);
			}
		}

		part.setContext(partContext);
	}

	/**
	 * Tries to find the view model using the ecview addons service.
	 * 
	 * @return
	 */
	protected YView findViewModel(String viewId) {
		ServiceTracker<IECViewAddonsMetadataService, IECViewAddonsMetadataService> tracker = new ServiceTracker<IECViewAddonsMetadataService, IECViewAddonsMetadataService>(
				Activator.getContext(), IECViewAddonsMetadataService.class,
				null);
		tracker.open();
		try {
			IECViewAddonsMetadataService uiService = tracker
					.waitForService(5000);
			return uiService.getViewMetadata(viewId);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			tracker.close();
		}
		return null;
	}

}
