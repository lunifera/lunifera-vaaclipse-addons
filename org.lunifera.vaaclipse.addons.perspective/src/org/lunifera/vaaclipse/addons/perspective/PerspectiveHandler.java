/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jan-Hendrik Diederich, Bredex GmbH - bug 201052
 *******************************************************************************/
package org.lunifera.vaaclipse.addons.perspective;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPlaceholderResolver;
import org.eclipse.emf.ecore.EObject;
import org.lunifera.vaaclipse.addons.common.api.resource.ICustomizedModelHandler;
import org.lunifera.vaaclipse.addons.common.api.resource.ICustomizedModelResourceHandler;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.semanticsoft.vaaclipse.publicapi.perspective.IPerspectiveHandler;
import org.semanticsoft.vaaclipse.publicapi.preferences.IPreferenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Perspective registry.
 * <p>
 * Stateful implementation.
 */
public class PerspectiveHandler implements IPerspectiveHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PerspectiveHandler.class);

	@Inject
	private EModelService modelService;

	@Inject
	private MApplication application;

	@Inject
	private IEclipseContext context;

	@Inject
	private IPreferenceProvider preferenceProvider;

	@Inject
	private ICustomizedModelHandler modelHandler;

	@Inject
	private ICustomizedModelResourceHandler resourceHandler;

	@Inject
	@Optional
	@Named("user")
	private String userId;

	@PostConstruct
	void postConstruct(MApplication application) {

	}

	/**
	 * Construct a new registry.
	 */
	public PerspectiveHandler() {

	}

	@Override
	public MPerspective clonePerspective(String userId, String label,
			MPerspective original) throws IllegalArgumentException {

		String newID = createNewId(label, original);
		MPerspective newPerspective = (MPerspective) modelService.cloneElement(
				original, null);
		EPlaceholderResolver resolver = context.get(EPlaceholderResolver.class);
		// Re-resolve any placeholder references
		List<MPlaceholder> phList = modelService.findElements(newPerspective,
				null, MPlaceholder.class, null);
		for (MPlaceholder ph : phList) {
			resolver.resolvePlaceholderRef(ph,
					modelService.getTopLevelWindowFor(original));
		}

		newPerspective.getPersistedState().put(
				IPerspectiveHandler.PROP_ORIGINAL_PERSPECTIVE,
				original.getElementId());
		newPerspective.getTags().add(IPerspectiveHandler.TAG_CREATED_BY_USER);

		newPerspective.setElementId(newID);
		newPerspective.setLabel(label);

		// add the new object to the part stack
		MPerspectiveStack stack = (MPerspectiveStack) (EObject) original
				.getParent();
		stack.getChildren().add((MPerspective) newPerspective);

		try {
			resourceHandler.save();
		} catch (IOException e) {
			LOGGER.error("{}", e);
		}

		if (userId != null
				&& userId.equals(ICustomizedModelResourceHandler.SYSTEM_USER)) {
			// TODO define fragment for system user
		}

		return newPerspective;
	}

	@Override
	public boolean canDeletePerspective(String userId, MPerspective perspective) {
		return perspective.getTags().contains(
				IPerspectiveHandler.TAG_CREATED_BY_USER);
	}

	@Override
	public boolean deletePerspective(String userId, MPerspective toDelete) {
		MPerspective perspective = (MPerspective) toDelete;
		MUIElement parent = (MUIElement) ((EObject) perspective).eContainer();

		boolean result = false;
		if (userId == null
				|| !userId.equals(ICustomizedModelResourceHandler.SYSTEM_USER)) {
			if (parent instanceof MPerspectiveStack) {
				MPerspectiveStack stack = (MPerspectiveStack) parent;
				stack.getChildren().remove(toDelete);
				result = true;
			}

			try {
				resourceHandler.save();
			} catch (IOException e) {
				LOGGER.error("{}", e);
			}
		} else {
			// TODO define system user
		}
		return result;
	}

	@Override
	public MPerspective findPerspectiveWithId(String perspectiveId) {
		return (MPerspective) modelService.find(perspectiveId, application);
	}

	@Override
	public String getDefaultPerspective() {
		return getDefaultPerspective(userId);
	}

	@Override
	public String getDefaultPerspective(String userId) {
		String defaultId = preferenceProvider.getUserPreferences(userId).get(
				IPerspectiveHandler.PREF_DEFAULT_PERSPECTIVE, null);
		// empty string may be returned but we want to return null if nothing
		// found
		if (defaultId == null || defaultId.length() == 0
				|| findPerspectiveWithId(defaultId) == null) {
			defaultId = preferenceProvider.getSystemPreferences().get(
					IPerspectiveHandler.PREF_DEFAULT_PERSPECTIVE, null);
		}

		return defaultId;
	}

	@Override
	public void setSystemDefaultPerspective(String id) {
		MPerspective perspective = findPerspectiveWithId(id);
		if (perspective != null) {
			Preferences prefs = preferenceProvider.getSystemPreferences();
			setDefaultPerspective(prefs, id);
		}
	}

	@Override
	public void setUserDefaultPerspective(String userId, String id) {
		MPerspective perspective = findPerspectiveWithId(id);
		if (perspective != null) {
			Preferences prefs = preferenceProvider.getUserPreferences();
			setDefaultPerspective(prefs, id);
		}
	}

	protected void setDefaultPerspective(Preferences prefs, String id) {
		prefs.put(IPerspectiveHandler.PREF_DEFAULT_PERSPECTIVE, id);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			LOGGER.error("{}", e);
		}
	}

	/**
	 * Return <code>true</code> if a label is valid. This checks only the given
	 * label in isolation. It does not check whether the given label is used by
	 * any existing perspectives.
	 * 
	 * @param label
	 *            the label to test
	 * @return whether the label is valid
	 */
	public boolean validateLabel(String label) {
		label = label.trim();
		if (label.length() <= 0) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canRevertPerspective(MPerspective perspToRevert) {
		EObject container = ((EObject) perspToRevert).eContainer();
		if (!(container instanceof MPerspectiveStack)) {
			return false;
		}

		String originalId = perspToRevert.getPersistedState().get(
				IPerspectiveHandler.PROP_ORIGINAL_PERSPECTIVE);
		if (originalId != null) {
			MPerspective originalPerspective = findPerspectiveWithId(originalId);
			return originalPerspective != null;
		}
		return false;
	}

	@Override
	public void revertPerspective(MPerspective perspToRevert) {
		EObject container = ((EObject) perspToRevert).eContainer();
		if (!(container instanceof MPerspectiveStack)) {
			return;
		}
		MPerspectiveStack stack = (MPerspectiveStack) container;

		String originalId = perspToRevert.getPersistedState().get(
				IPerspectiveHandler.PROP_ORIGINAL_PERSPECTIVE);
		if (originalId != null) {
			MPerspective originalPerspective = findPerspectiveWithId(originalId);
			if (originalPerspective != null) {
				String label = perspToRevert.getLabel();

				// delte the old perspective
				deletePerspective(userId, perspToRevert);

				// create a new one
				MPerspective newPerspective = clonePerspective(userId, label,
						originalPerspective);
				stack.setSelectedElement(newPerspective);
			}
		}
	}

	/**
	 * Create a new perspective.
	 * 
	 * @param label
	 *            the name of the new descriptor
	 * @param description
	 *            the description of the new descriptor
	 * @param original
	 *            the descriptor on which to base the new descriptor
	 * @return a new perspective or <code>null</code> if the creation failed.
	 */
	public MPerspective createPerspective(String label, String description,
			MPerspective original) {

		String newID = createNewId(label, original);
		MPerspective newPerspective = (MPerspective) modelService.cloneElement(
				original, null);
		newPerspective.setElementId(newID);
		return newPerspective;
	}

	/**
	 * Return an id for the new descriptor.
	 * 
	 * The id must encode the original id. id is of the form <originalId>.label
	 * 
	 * @param label
	 * @param originalDescriptor
	 * @return the new id
	 */
	private String createNewId(String label, MPerspective originalDescriptor) {
		String originalId = originalDescriptor.getPersistedState().get(
				IPerspectiveHandler.PROP_ORIGINAL_PERSPECTIVE);
		if (originalId == null) {
			originalId = "root";
		}
		return originalId + '.' + label;
	}
}
