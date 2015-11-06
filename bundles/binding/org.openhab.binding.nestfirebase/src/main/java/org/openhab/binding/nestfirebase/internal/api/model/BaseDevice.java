/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nestfirebase.internal.api.model;

import java.util.Date;


/**
 * @author Neil Renaud
 * @since 1.7.0
 */
abstract class BaseDevice  {

    private String device_id;
    private String locale;
    private String software_version;
    private String structure_id;
    private String name;
    private String name_long;
    private Date last_connection;
    private boolean is_online;

    protected BaseDevice() {
    }

    /**
     * Returns the unique identifier of this device.
     */
    public String getDevice_id() {
		return device_id;
	}

    /**
     * Returns the locale for this device, if set.
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Returns the current software version that this device
     * has installed.
     */
    public String getSoftware_version() {
		return software_version;
	}

    /**
     * Returns the unique identifier of the structure to which this
     * device is paired.
     * @see org.openhab.binding.nestfirebase.internal.api.model.Structure
     * @see Structure#getStructureID()
     */
    public String getStructure_id() {
		return structure_id;
	}

    /**
     * Returns an abbreviated version of the user's name for this device.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a verbose version of the user's name for this device.
     */
    public String getName_long() {
		return name_long;
	}
    
    public boolean getIs_online() {
		return is_online;
	}

    /**
     * Returns the timestamp (in ISO-8601 format) at which the device last connected to the
     * Nest service.
     */
    public Date getLast_connection() {
		return last_connection;
	}
}
