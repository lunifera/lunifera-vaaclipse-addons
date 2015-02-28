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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.lunifera.ecview.core.common.context.II18nService;
import org.lunifera.ecview.xtext.builder.participant.IECViewAddonsMetadataService;
import org.lunifera.vaaclipse.addons.common.api.explorer.IExplorerCategory;
import org.lunifera.vaaclipse.addons.common.api.explorer.IExplorerInfo;
import org.lunifera.vaaclipse.addons.common.api.explorer.IExplorerInfoManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(property = { "service.ranking=100" }, immediate = true)
public class ExplorerInfoManager implements IExplorerInfoManager {

	private static final String I18NKEY_CATEGORY_DESCRIPTION = "desc.%s";
	private static final String I18NKEY_CATEGORY = "category.%s";
	private IECViewAddonsMetadataService ecviewService;
	private II18nService i18nService;

	@Override
	public Iterable<IExplorerInfo> getExplorerInfo(IExplorerCategory parent,
			IEclipseContext context) {
		if (parent == null) {
			return getRootInfos(context);
		} else {
			return getChildren(context, parent);
		}
	}

	@Reference(name = "ecviewMetadataService", unbind = "unbindECViewMetadataService", cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.DYNAMIC)
	protected void bindECViewMetadataService(
			IECViewAddonsMetadataService service) {
		// handle mandatory dynamic
		if (service != this.ecviewService) {
			this.ecviewService = service;
		}
	}

	protected void unbindECViewMetadataService(
			IECViewAddonsMetadataService service) {
		// handle mandatory dynamic
		if (service == this.ecviewService) {
			this.ecviewService = null;
		}
	}

	@Reference(name = "i18nService", unbind = "unbindI18nService", cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.DYNAMIC)
	protected void bindI18nService(II18nService service) {
		// handle mandatory dynamic
		if (service != this.i18nService) {
			this.i18nService = service;
		}
	}

	protected void unbindI18nService(II18nService service) {
		// handle mandatory dynamic
		if (service == this.i18nService) {
			this.i18nService = null;
		}
	}

	/**
	 * Creates a category for each package name of found views.
	 * 
	 * @param context
	 * @return
	 */
	protected Iterable<IExplorerInfo> getRootInfos(IEclipseContext context) {
		Map<String, IExplorerInfo> infos = new HashMap<String, IExplorerInfo>();

		List<String> viewNames = getSortedRootViewNames(null, false);
		// access all views that are contained in root packages
		for (String viewFQN : viewNames) {
			String pkg = viewFQN.substring(0, viewFQN.lastIndexOf("."));
			String categoryId = toCategoryI18nKey(pkg);
			ExplorerCategory category = null;
			if (!infos.containsKey(categoryId)) {
				category = new ExplorerCategory(context);
				category.setId(categoryId);
				category.setTarget(pkg);
				category.setI18nLabelKey(categoryId);
				category.setIconI18nKey(toImageI18nKey(categoryId));
				category.setI18nDescriptionKey(toDescriptionI18nKey(categoryId));
				category.setLabel(translate(category.getI18nLabelKey()));
				category.setDescription(translate(category
						.getI18nDescriptionKey()));
				category.setIconURI(translate(category.getIconI18nKey()));
				infos.put(categoryId, category);
			}
		}

		return infos.values();
	}

	/**
	 * Fetches all children for the given category.
	 * 
	 * @param context
	 * @return
	 */
	protected Iterable<IExplorerInfo> getChildren(IEclipseContext context,
			IExplorerCategory parent) {
		Map<String, IExplorerInfo> infos = new HashMap<String, IExplorerInfo>();

		List<String> viewNames = getSortedRootViewNames(parent.getTarget(),
				false);
		// access all views that are contained in the parent category
		for (String viewFQN : viewNames) {
			String pkg = viewFQN.substring(0, viewFQN.lastIndexOf("."));
			String categoryId = toCategoryI18nKey(pkg);

			if (pkg.equals(parent.getTarget())) {
				// the package is the same as the parent#target. So lets add an
				// application leaf
				ExplorerApplicationLeaf leaf = new ExplorerApplicationLeaf();
				leaf.setId(viewFQN);
				leaf.setTarget(viewFQN);
				leaf.setI18nLabelKey(viewFQN);
				leaf.setIconI18nKey(toImageI18nKey(viewFQN));
				leaf.setI18nDescriptionKey(toDescriptionI18nKey(viewFQN));
				leaf.setLabel(translate(leaf.getI18nLabelKey()));
				leaf.setDescription(translate(leaf.getI18nDescriptionKey()));
				leaf.setIconURI(translate(leaf.getIconI18nKey()));

				infos.put(viewFQN, leaf);
			} else {
				// must be a new category
				ExplorerCategory category = null;

				// if not already created, then do so
				if (!infos.containsKey(categoryId)) {
					category = new ExplorerCategory(context);
					category.setId(categoryId);
					category.setTarget(pkg);
					category.setI18nLabelKey(categoryId);
					category.setIconI18nKey(toImageI18nKey(categoryId));
					category.setI18nDescriptionKey(toDescriptionI18nKey(categoryId));
					category.setLabel(translate(category.getI18nLabelKey()));
					category.setDescription(translate(category
							.getI18nDescriptionKey()));
					category.setIconURI(translate(category.getIconI18nKey()));

					infos.put(categoryId, category);
				}
			}
		}

		List<IExplorerInfo> result = new ArrayList<IExplorerInfo>(
				infos.values());
		Collections.sort(result);

		return result;
	}

	private List<String> getSortedRootViewNames(String pkgName,
			boolean includeChildren) {
		List<String> viewNames = ecviewService.getIDEViewNames(pkgName,
				includeChildren);
		// Collections.sort(viewNames, new Comparator<String>() {
		// @Override
		// public int compare(String o0, String o1) {
		// String pkg0 = o0.substring(0, o0.lastIndexOf("."));
		// String pkg1 = o1.substring(0, o1.lastIndexOf("."));
		//
		// return pkg0.length() - pkg1.length();
		// }
		// });
		return viewNames;
	}

	private String toCategoryI18nKey(String pkg) {
		return String.format(I18NKEY_CATEGORY, pkg);
	}

	private String toImageI18nKey(String category) {
		return category + ".image";
	}

	private String toDescriptionI18nKey(String categoryId) {
		return String.format(I18NKEY_CATEGORY_DESCRIPTION, categoryId);
	}

	private String translate(String key) {
		return i18nService.getValue(key, Locale.getDefault());
	}
}
