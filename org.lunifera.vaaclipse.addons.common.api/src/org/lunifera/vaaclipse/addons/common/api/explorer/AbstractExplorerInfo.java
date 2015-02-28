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
package org.lunifera.vaaclipse.addons.common.api.explorer;

import org.lunifera.vaaclipse.addons.common.api.ResourceUtil;

import com.vaadin.server.Resource;

public abstract class AbstractExplorerInfo implements IExplorerInfo {

	private String id;
	private String target;
	private IExplorerCategory category;
	private String iconKey;
	private String i18nLabelKey;
	private String i18nDescriptionKey;
	private Resource icon;
	private String label;
	private String description;
	private String iconURI;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTarget() {
		return target;
	}

	@Override
	public IExplorerCategory getCategory() {
		return category;
	}

	@Override
	public String getIconI18nKey() {
		return iconKey;
	}

	@Override
	public String getI18nLabelKey() {
		return i18nLabelKey;
	}

	@Override
	public String getI18nDescriptionKey() {
		return i18nDescriptionKey;
	}

	@Override
	public String getIconURI() {
		return iconURI;
	}

	@Override
	public Resource getIcon() {
		if (icon == null && iconURI != null && !iconURI.equals("")) {
			icon = ResourceUtil.getResource(iconURI);
		}
		return icon;
	}

	@Override
	public String getLabel() {
		return label != null ? label : "";
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setCategory(IExplorerCategory category) {
		this.category = category;
	}

	public void setIconI18nKey(String iconKey) {
		this.iconKey = iconKey;
	}

	public void setI18nLabelKey(String i18nLabelKey) {
		this.i18nLabelKey = i18nLabelKey;
	}

	public void setI18nDescriptionKey(String i18nDescriptionKey) {
		this.i18nDescriptionKey = i18nDescriptionKey;
	}

	public void setIconURI(String iconURI) {
		this.iconURI = iconURI;
	}

	public void setIcon(Resource icon) {
		this.icon = icon;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int compareTo(IExplorerInfo other) {
		return getLabel().compareTo(other.getLabel());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractExplorerInfo other = (AbstractExplorerInfo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
