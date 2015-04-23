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
package org.lunifera.vaaclipse.addons.common.resource;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.URIUtil;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.internal.workbench.E4XMIResource;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl;
import org.eclipse.e4.ui.model.fragment.MFragmentFactory;
import org.eclipse.e4.ui.model.fragment.MModelFragment;
import org.eclipse.e4.ui.model.fragment.MModelFragments;
import org.eclipse.e4.ui.model.fragment.MStringModelFragment;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.osgi.service.datalocation.Location;
import org.lunifera.vaaclipse.addons.common.api.resource.ICustomizedModelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stateful implementation.
 */
@SuppressWarnings("restriction")
public class CustomizedModelHandler implements ICustomizedModelHandler {

	private static final String FEATURE__CHILDREN = UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN
			.getName();

	private static final Logger logger = LoggerFactory
			.getLogger(CustomizedModelHandler.class);

	@Inject
	private ResourceSet resourceSet;

	@Inject
	private IEclipseContext context;

	@Inject
	@Named(E4Workbench.INITIAL_WORKBENCH_MODEL_URI)
	private URI applicationDefinitionInstance;

	@Inject
	@org.eclipse.e4.core.di.annotations.Optional
	@Named(E4Workbench.INSTANCE_LOCATION)
	private Location instanceLocation;

	@Inject
	@org.eclipse.e4.core.di.annotations.Optional
	@Named("user")
	private String userId;

	@Override
	public void mergeFragments(List<MModelFragments> fragmentsContainers) {
		for (MModelFragments fragmentsContainer : fragmentsContainers) {
			mergeFragment(fragmentsContainer);
		}

	}

	/**
	 * Returns the resource for the application model.
	 * 
	 * @return
	 */
	private Resource getApplicationModelResource() {
		return resourceSet.getResources().get(0);
	}

	@Override
	public void mergeFragment(MModelFragments fragmentsContainer) {
		if (fragmentsContainer == null) {
			return;
		}
		E4XMIResource applicationResource = (E4XMIResource) getApplicationModelResource();

		List<MModelFragment> fragments = fragmentsContainer.getFragments();
		for (MModelFragment fragment : fragments) {
			List<MApplicationElement> elements = fragment.getElements();
			if (elements.size() == 0) {
				continue;
			}

			for (MApplicationElement el : elements) {
				EObject o = (EObject) el;
				E4XMIResource r = (E4XMIResource) o.eResource();

				if (applicationResource.getIDToEObjectMap().containsKey(
						r.getID(o))) {
					continue;
				}

				applicationResource.setID(o, r.getID(o));

				// Remember IDs of subitems
				TreeIterator<EObject> treeIt = EcoreUtil
						.getAllContents(o, true);
				while (treeIt.hasNext()) {
					EObject eObj = treeIt.next();
					r = (E4XMIResource) eObj.eResource();
					applicationResource.setID(eObj, r.getInternalId(eObj));
				}
			}

			fragment.merge((MApplication) applicationResource.getContents()
					.get(0));
		}
	}

	@Override
	public List<MModelFragment> findFragments(String parentId,
			String featureName, MModelFragments fragmentsContainer) {
		final List<MModelFragment> result = new ArrayList<MModelFragment>();
		fragmentsContainer
				.getFragments()
				.stream()
				.filter(e -> e instanceof MStringModelFragment)
				.map(c -> (MStringModelFragment) c)
				.filter(e -> e.getParentElementId().equals(parentId)
						&& e.getFeaturename().equals(featureName))
				.forEach(e -> result.add(e));
		return result;
	}

	@Override
	public MModelFragment createFragment(String parentId, String featureName) {
		MStringModelFragment fragment = MFragmentFactory.INSTANCE
				.createStringModelFragment();
		fragment.setParentElementId(parentId);
		fragment.setFeaturename(featureName);
		return fragment;
	}

	protected MModelFragments createFragmentContainer() {
		MModelFragments container = MFragmentFactory.INSTANCE
				.createModelFragments();
		return container;
	}

	@Override
	public MModelFragment addPerspectiveFragment(MPerspectiveStack parent,
			MPerspective newPerspective, MModelFragments fragmentsContainer) {
		MModelFragment fragment = createPerspectiveFragment(parent,
				newPerspective);
		fragmentsContainer.getFragments().add(fragment);

		return fragment;
	}

	@Override
	public void removePerspective(String userId, String parentId,
			MPerspective perspective) {
		try {
			MModelFragments container = loadCustomized(userId);
			removePerspective(parentId, perspective, container);
		} catch (IOException e) {
			logger.error("{}", e);
		}
	}

	@Override
	public void removePerspective(String parentId, MPerspective perspective,
			MModelFragments fragmentsContainer) {

		MModelFragment fragment = findFragmentForPerspective(parentId,
				perspective, fragmentsContainer);
		if (fragment.getElements().size() > 1) {
			throw new IllegalArgumentException(
					"Customized fragments may only contain one element!");
		}
		fragmentsContainer.getFragments().remove(fragment);
	}

	@Override
	public MModelFragment createPerspectiveFragment(MPerspectiveStack parent,
			MPerspective newPerspective) {
		MModelFragment fragment = createFragment(parent.getElementId(),
				FEATURE__CHILDREN);
		fragment.getElements().add(newPerspective);
		return fragment;
	}

	@Override
	public MModelFragment findFragmentForPerspective(String parentId,
			MPerspective perspective, MModelFragments fragmentsContainer) {
		List<MModelFragment> fragments = findFragments(parentId,
				FEATURE__CHILDREN, fragmentsContainer);

		Optional<MModelFragment> fragment = fragments
				.stream()
				.filter(e -> e
						.getElements()
						.stream()
						.filter(c -> c.getElementId().equals(
								perspective.getElementId())).iterator()
						.hasNext()).findFirst();

		return fragment.isPresent() ? fragment.get() : null;
	}

	@Override
	public void persistCustomized(MModelFragments mFragments)
			throws IOException {
		persistCustomized(userId, mFragments);
	}

	@Override
	public void persistCustomized(String userId, MModelFragments mFragments)
			throws IOException {

		if (!validateCustomizedFragements(mFragments)) {
			throw new IllegalStateException(
					"Fragments are not configured properly.");
		}

		File saveLocation = getCustomizedSaveLocation(userId);
		Resource toSave = resourceSet.getResource(
				URI.createFileURI(saveLocation.getAbsolutePath()), true);
		toSave.getContents().clear();
		toSave.getContents().add((EObject) mFragments);
		toSave.save(null);
	}

	@Override
	public boolean validateCustomizedFragements(MModelFragments mFragments) {
		java.util.Optional<MModelFragment> result = mFragments.getFragments()
				.stream().filter(e -> e.getElements().size() > 1).findFirst();
		if (result.isPresent()) {
			return false;
		}

		return true;
	}

	@Override
	public MModelFragments loadCustomized() throws IOException {
		return loadCustomized(userId);
	}

	@Override
	public MModelFragments loadCustomized(String userId) throws IOException {

		// Then load the customized user fragments
		//
		File userLocation = getCustomizedSaveLocation(userId);
		if (userLocation.exists()) {
			Resource userResource = resourceSet.getResource(
					URI.createFileURI(userLocation.getAbsolutePath()), true);
			if (!userResource.getContents().isEmpty()) {
				return (MModelFragments) userResource.getContents().get(0);
			} else {
				MModelFragments container = createFragmentContainer();
				userResource.getContents().add((EObject) container);
				return container;
			}
		} else {
			Resource userResource = resourceSet.createResource(URI
					.createFileURI(userLocation.getAbsolutePath()));
			MModelFragments container = createFragmentContainer();
			userResource.getContents().add((EObject) container);
			return container;
		}
	}

	private File getCustomizedSaveLocation(String userId) {
		File workbenchData = new File(getBaseLocation(), String.format(
				"%s_fragment.xmi", userId)); //$NON-NLS-1$
		return workbenchData;
	}

	private File getBaseLocation() {
		File baseLocation;
		try {
			baseLocation = new File(URIUtil.toURI(instanceLocation.getURL()));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		baseLocation = new File(baseLocation, ".metadata"); //$NON-NLS-1$
		baseLocation = new File(baseLocation, ".plugins"); //$NON-NLS-1$
		baseLocation = new File(baseLocation, "org.eclipse.e4.workbench"); //$NON-NLS-1$
		return baseLocation;
	}

}
