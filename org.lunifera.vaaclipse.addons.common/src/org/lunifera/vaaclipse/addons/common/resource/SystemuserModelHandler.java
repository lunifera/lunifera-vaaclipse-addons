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
import java.util.Collections;
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
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl;
import org.eclipse.e4.ui.model.fragment.MFragmentFactory;
import org.eclipse.e4.ui.model.fragment.MModelFragment;
import org.eclipse.e4.ui.model.fragment.MModelFragments;
import org.eclipse.e4.ui.model.fragment.MStringModelFragment;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPlaceholderResolver;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.osgi.service.datalocation.Location;
import org.lunifera.vaaclipse.addons.common.api.model.VaaclipseModelUtils;
import org.lunifera.vaaclipse.addons.common.api.resource.ISystemuserModelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stateful implementation.
 */
@SuppressWarnings("restriction")
public class SystemuserModelHandler implements ISystemuserModelHandler {

	/**
	 * A fragment used for the system user settings. Will copied into location
	 * if not available.
	 */
	public static final String DEFAULT_SYSTEM_USER_FRAGMENT = "platform:/plugin/org.lunifera.vaaclipse.addons.perspective/template/systemUserFragment.e4xmi-template";
	private static final String SYSTEM_USER_FRAGMENT_PROPERTY = "org.lunifera.vaaclipse.addons.perspective.systemuser.fragment";

	private static final String FEATURE__CHILDREN = UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN
			.getName();

	private static final Logger logger = LoggerFactory
			.getLogger(SystemuserModelHandler.class);

	@Inject
	private EModelService modelService;

	@Inject
	private IEclipseContext context;

	@Inject
	private ResourceSet resourceSet;

	@Inject
	@org.eclipse.e4.core.di.annotations.Optional
	@Named(E4Workbench.INSTANCE_LOCATION)
	private Location instanceLocation;

	private Resource systemResource;

	/**
	 * Returns the resource for the application model.
	 * 
	 * @return
	 */
	private Resource getApplicationModelResource() {
		return resourceSet.getResources().get(0);
	}

	@Override
	public void mergeFragment() {
		E4XMIResource applicationResource = (E4XMIResource) getApplicationModelResource();

		try {
			MModelFragments fragmentsContainer = getFragment();

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
					TreeIterator<EObject> treeIt = EcoreUtil.getAllContents(o,
							true);
					while (treeIt.hasNext()) {
						EObject eObj = treeIt.next();
						r = (E4XMIResource) eObj.eResource();
						applicationResource.setID(eObj, r.getInternalId(eObj));
					}
				}

				MApplication application = (MApplication) applicationResource
						.getContents().get(0);
				merge(application, (MStringModelFragment) fragment);
			}
		} finally {
			unload();
		}
	}

	public List<MApplicationElement> merge(MApplication application,
			MStringModelFragment fragment) {
		MApplicationElement o = VaaclipseModelUtils.findElementById(
				application, fragment.getParentElementId());
		if (o != null) {
			EStructuralFeature feature = ((EObject) o).eClass()
					.getEStructuralFeature(fragment.getFeaturename());
			if (feature != null) {
				return VaaclipseModelUtils.merge(o, feature,
						fragment.getElements(), fragment.getPositionInList());
			}

		}

		return Collections.emptyList();
	}

	protected List<MModelFragment> findFragments(String parentId,
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

	protected MModelFragment createFragment(String parentId, String featureName) {
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
	public void addPerspectiveFragment(MPerspectiveStack parent,
			MPerspective newPerspective) {

		MModelFragment fragment = null;
		try {
			MModelFragments fragmentsContainer = getFragment();
			fragment = createPerspectiveFragment(parent, newPerspective);
			fragmentsContainer.getFragments().add(fragment);

			persistFragment(fragmentsContainer);
		} catch (IOException e) {
			logger.warn("{}", e);
		} finally {
			unload();
		}
	}

	@Override
	public void removePerspectiveFragment(String parentId,
			MPerspective perspective) {
		try {
			MModelFragments container = getFragment();
			if (container != null) {
				removePerspective(parentId, perspective, container);

				persistFragment(container);
			}
		} catch (IOException e) {
			logger.error("{}", e);
		}
	}

	protected void removePerspective(String parentId, MPerspective perspective,
			MModelFragments fragmentsContainer) {

		MModelFragment fragment = findFragmentForPerspective(parentId,
				perspective, fragmentsContainer);
		if (fragment.getElements().size() > 1) {
			throw new IllegalArgumentException(
					"Customized fragments may only contain one element!");
		}
		fragmentsContainer.getFragments().remove(fragment);
	}

	protected MModelFragment createPerspectiveFragment(
			MPerspectiveStack parent, MPerspective newPerspective) {
		MModelFragment fragment = createFragment(parent.getElementId(),
				FEATURE__CHILDREN);

		MPerspective clone = (MPerspective) modelService.cloneElement(
				newPerspective, null);
		EPlaceholderResolver resolver = context.get(EPlaceholderResolver.class);
		// Re-resolve any placeholder references
		List<MPlaceholder> phList = modelService.findElements(clone, null,
				MPlaceholder.class, null);
		for (MPlaceholder ph : phList) {
			resolver.resolvePlaceholderRef(ph,
					modelService.getTopLevelWindowFor(parent));
		}
		fragment.getElements().add(clone);
		return fragment;
	}

	protected MModelFragment findFragmentForPerspective(String parentId,
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
	public void persistFragment(MModelFragments mFragments) throws IOException {
		if (!validateFragement(mFragments)) {
			throw new IllegalStateException(
					"Fragments are not configured properly.");
		}

		ensureSystemUserFragment();

		File saveLocation = getSystemUserFragmentLocation();
		Resource toSave = resourceSet.getResource(
				URI.createFileURI(saveLocation.getAbsolutePath()), true);
		toSave.getContents().clear();
		toSave.getContents().add((EObject) mFragments);
		toSave.save(null);
		toSave.unload();
	}

	protected boolean validateFragement(MModelFragments mFragments) {
		java.util.Optional<MModelFragment> result = mFragments.getFragments()
				.stream().filter(e -> e.getElements().size() > 1).findFirst();
		if (result.isPresent()) {
			return false;
		}

		return true;
	}

	protected MModelFragments getFragment() {

		ensureSystemUserFragment();

		// Then load the customized user fragments
		//
		File systemLocation = getSystemUserFragmentLocation();
		if (systemLocation.exists()) {
			systemResource = resourceSet.getResource(
					URI.createFileURI(systemLocation.getAbsolutePath()), true);
			MModelFragments result = (MModelFragments) systemResource
					.getContents().get(0);
			return result;
		}

		return null;
	}

	/**
	 * Unload the system resource. After merge it will be damaged since
	 * containment references are broken.
	 */
	protected void unload() {
		if (systemResource != null) {
			systemResource.unload();
		}
	}

	/**
	 * Ensures that the system user fragment exists in workbench location.
	 */
	protected void ensureSystemUserFragment() {
		// check the system user file
		File systemUserFile = getSystemUserFragmentLocation();
		if (!systemUserFile.exists()) {

			String uri = (String) context.get(SYSTEM_USER_FRAGMENT_PROPERTY);
			if (uri == null) {
				uri = DEFAULT_SYSTEM_USER_FRAGMENT;
			}

			Resource sysuserFragment = resourceSet.getResource(
					URI.createURI(uri), true);
			// copy model
			sysuserFragment.setURI(URI.createFileURI(systemUserFile
					.getAbsolutePath()));
			try {
				sysuserFragment.save(null);
			} catch (IOException e) {
				logger.warn("{}", e);
			}

			// unload the fragment
			sysuserFragment.unload();
			resourceSet.getResources().remove(sysuserFragment);
		}
	}

	private File getSystemUserFragmentLocation() {
		File workbenchData = new File(getBaseLocation(),
				"systemUserFragment.e4xmi"); //$NON-NLS-1$
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
