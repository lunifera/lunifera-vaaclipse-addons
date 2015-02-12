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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.lunifera.dsl.dto.lib.impl.DtoServiceAccess;
import org.lunifera.dsl.dto.lib.services.IDTOService;
import org.lunifera.ecview.core.common.context.ContextException;
import org.lunifera.ecview.core.common.context.IViewContext;
import org.lunifera.ecview.core.common.model.core.YBeanSlot;
import org.lunifera.ecview.core.common.model.core.YView;
import org.lunifera.ecview.xtext.builder.participant.IECViewAddonsMetadataService;
import org.lunifera.runtime.web.ecview.presentation.vaadin.VaadinRenderer;
import org.lunifera.vaaclipse.addons.common.api.IE4Constants;
import org.lunifera.vaaclipse.addons.ecview.event.E4EventBrokerAdapter;
import org.lunifera.vaaclipse.addons.ecview.impl.Activator;
import org.osgi.util.tracker.ServiceTracker;

import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("restriction")
public class GenericECViewView {

	private final String viewId;
	private final IEclipseContext eclipseContext;
	private final VerticalLayout parent;

	private IViewContext viewContext;
	private IDTOService<?> dtoService;

	@Inject
	private org.eclipse.e4.core.services.events.IEventBroker e4EventBroker;

	@Inject
	public GenericECViewView(VerticalLayout parent,
			IEclipseContext eclipseContext, MPart mPart) {
		this.parent = parent;
		this.eclipseContext = eclipseContext;

		this.viewId = mPart.getPersistedState().get(
				IE4Constants.PROP_INPUT_VIEW_ID);

		VerticalLayout layout = new VerticalLayout();
		parent.addComponent(layout);
		layout.setSizeFull();

		YView yView = findViewModel();
		if (yView == null) {
			Notification.show(viewId + " could not be found!",
					Notification.Type.ERROR_MESSAGE);
			return;
		}

		YBeanSlot yBeanSlot = yView.getBeanSlot("main");
		if (yBeanSlot != null) {
			dtoService = DtoServiceAccess.getService(yBeanSlot.getValueType());
		}

		// render the Vaadin UI
		Map<String, Object> properties = new HashMap<String, Object>();
		Map<String, Object> services = new HashMap<String, Object>();
		services.put(
				org.lunifera.runtime.common.event.IEventBroker.class.getName(),
				new E4EventBrokerAdapter(e4EventBroker));
		properties.put(IViewContext.PARAM_SERVICES, services);

		VaadinRenderer renderer = new VaadinRenderer();
		try {
			viewContext = renderer.render(layout, yView, properties);
		} catch (ContextException e) {
			e.printStackTrace();
		}
	}

	@PreDestroy
	public void dispose() {
		try {
			viewContext.dispose();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Tries to find the view model using the ecview addons service.
	 * 
	 * @return
	 */
	protected YView findViewModel() {
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
