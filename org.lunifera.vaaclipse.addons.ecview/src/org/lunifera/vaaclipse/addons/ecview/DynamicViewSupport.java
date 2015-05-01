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

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("restriction")
public class DynamicViewSupport {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DynamicViewSupport.class);

	private static final String PROP_VIEW_ID = "org.lunifera.vaaclipse.addons.ecview.viewId";

	@Inject
	private ISharedStateContextProvider sharedStateProvider;

	@Inject
	private EModelService modelService;

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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void openNewGenericECViewView(String id, String label,
			String description, String iconURI, IEclipseContext context) {

		MApplication app = context.get(MApplication.class);
		EPartService partService = context.get(EPartService.class);
		EModelService modelService = context.get(EModelService.class);
		MUIElement activeElement = modelService.find(id,
				context.get(MPerspective.class));
		if (activeElement != null) {
			// show the part
			partService.activate((MPart) activeElement, true);
		} else {

			YView yView = findViewModel(id);
			if (yView == null) {
				LOGGER.error("Could not find view for {}", id);
				return;
			}

			// determine the view category
			String viewCategory = yView.getCategory();
			if (viewCategory == null) {
				viewCategory = IE4Constants.ID__PARTSTACK__MAIN;
			}

			// find a parent container for the category
			MElementContainer container = null;
			List<MElementContainer> containers = modelService.findElements(app,
					null, MElementContainer.class,
					Collections.singletonList(viewCategory),
					EModelService.PRESENTATION);
			if (!containers.isEmpty()) {
				container = containers.get(0);
			} else {
				LOGGER.error("Could not container for category {}",
						viewCategory);
				return;
			}

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
			IEclipseContext partContext = getContext(container).createChild();
			part.setContext(partContext);
			initializePartContext(part, partContext, yView, id);

			// add the part to its container
			container.getChildren().add(part); // Add part to stack

			// show the part
			partService.showPart(part, PartState.ACTIVATE); // Show
		}
	}

	private IEclipseContext getContext(MUIElement parent) {
		if (parent instanceof MContext) {
			return ((MContext) parent).getContext();
		}
		return modelService.getContainingContext(parent);
	}

	/**
	 * Creates the context for a part that is already opened and was persisted
	 * in the user specific application model.
	 * 
	 * @param part
	 * @return
	 */
	public YView createPartContextForPersisted(MPart part) {
		String viewId = part.getPersistedState().get(PROP_VIEW_ID);
		if (viewId == null) {
			return null;
		}

		YView yView = findViewModel(viewId);
		if (yView == null) {
			return null;
		}

		IEclipseContext context = part.getContext();
		String id = part.getElementId();

		initializePartContext(part, context, yView, id);

		return yView;
	}

	/**
	 * Creates a child context for the part that should be opened.
	 * 
	 * @param part
	 * @param context
	 * @param yView
	 * @param id
	 */
	private void initializePartContext(MPart part, IEclipseContext partContext,
			YView yView, String id) {

		// Store the view id in the part for later reloading
		part.getPersistedState().put(PROP_VIEW_ID, yView.getName());

		// initialize the context -> so no delegation to OSGi services is
		// processed later
		partContext.set(ISharedStateContext.class, null);
		partContext.set(IDTOService.class, null);

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
				@SuppressWarnings({ "unchecked" })
				IDTOService<Object> dtoService = (IDTOService<Object>) DtoServiceAccess
						.getService(yBeanSlot.getValueType());
				partContext.set(IDTOService.class, dtoService);
			}
		}
	}

	/**
	 * Tries to find the view model using the ecview addons service.
	 * 
	 * @return
	 */
	private YView findViewModel(String viewId) {
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
