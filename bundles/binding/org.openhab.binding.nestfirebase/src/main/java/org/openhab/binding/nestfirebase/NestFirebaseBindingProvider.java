/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nestfirebase;

import java.util.List;

import org.openhab.binding.nestfirebase.internal.NestType;
import org.openhab.core.binding.BindingProvider;

/**
 * @author Neil Renaud
 * @since 1.8.0
 */
public interface NestFirebaseBindingProvider extends BindingProvider {

	/**
	 * Get all itemNames for this ID
	 * @param nestId
	 * @return
	 */
	List<String> getItemNameFromNestId(String nestId);

	/**
	 * Get the NestType for this itemName
	 * @param itemName
	 * @return
	 */
	NestType getTypeForItemName(String itemName);

	List<String> getItemNamesForType(NestType nestCode);

	String getIdForItemName(String itemName);

}
