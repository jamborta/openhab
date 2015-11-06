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
import java.util.List;
import com.fasterxml.jackson.annotation.JsonAnySetter;

/**
 * @author Neil Renaud
 * @since 1.7.0
 */
public class Structure {

    private  String structure_id;
    private List<String> thermostats;
    private List<String> smoke_co_alarms;
    private String name;
    private String country_code;
    private String postal_code;

    
    private Date peak_period_start_time;
    private Date peak_period_end_time;
    private String time_zone;
    private Away away;
    private Eta eta;

    private Structure() {
    }

    /** Returns the unique identifier for this structure. */
    public String getStructure_id() {
		return structure_id;
	}
    
    /** Returns an unsorted list of all thermostat IDs that
     *  are paired with this structure. */
    public List<String> getThermostats() {
		return thermostats;
	}
    
    /**
     * Gets the current occupancy state of the structure (Home,
     * Away, or Auto Away)
     * @see org.openhab.binding.nestfirebase.internal.api.model.CopyOfStructure.AwayState */    
    public Away getAway() {
		return away;
	}
    
    /** Returns an unsorted list of all Nest Protect IDs that
     *  are paired with this structure. */
    public List<String> getSmoke_co_alarms() {
		return smoke_co_alarms;
	}
    
    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
    	System.out.println("Unknown property Key[" + key + "] Value["+ value+"]");
    }
    

    /** Returns the users's name for this structure */
    public String getName() {
        return name;
    }

    /**
     * Returns the country code for this structure is located in,
     * if it has been set. Returns an empty string otherwise.
     */
    public String getCountry_code() {
		return country_code;
	}
    
    public String getPostal_code() {
		return postal_code;
	}
    
    /**
     * Returns the ETA window for this structure, if one exists.
     * @see org.openhab.binding.nestfirebase.internal.api.model.CopyOfStructure.ETA
     */
    public Eta getEta() {
		return eta;
	}
    

    /**
     * Get the time (in ISO-8601 format) at which a Rush Hour Rewards
     * event will begin adjusting enrolled thermostats. Returns an empty string
     * if no event is active.
     */
    public Date getPeak_period_start_time() {
		return peak_period_start_time;
	}

    /**
     * Get the time (in ISO-8601 format) at which a Rush Hour Rewards
     * event will stop adjusting enrolled thermostats. Returns an empty string
     * if no event is active.
     */
    public Date getPeak_period_end_time() {
		return peak_period_end_time;
	}

    /**
     * Returns the Olson code (e.g. "America/Los_Angeles") of the
     * structure's location, if set. Returns an empty string otherwise.
     */
    public String getTime_zone() {
		return time_zone;
	}

    /**
     * Returns the number of thermostats paired with this structure
     */
    public int getThermostatCount() {
        return thermostats.size();
    }

    /**
     * Returns the number of Nest Protect Smoke & CO Detectors paired
     * with this structure.
     */
    public int getSmokeCOAlarmCount() {
        return smoke_co_alarms.size();
    }

    @Override
    public String toString() {
        return "Structure{" +
                "mStructureID='" + structure_id + '\'' +
                ", mThermostatIDs=" + thermostats +
                ", mSmokeCOAlarms=" + smoke_co_alarms +
                ", mAwayState='" + away + '\'' +
                ", mName='" + name + '\'' +
                ", mCountryCode='" + country_code + '\'' +
                ", mPeakPeriodStartTime='" + peak_period_start_time + '\'' +
                ", mPeakPeriodEndTime='" + peak_period_end_time + '\'' +
                ", mTimeZone='" + time_zone + '\'' +
                ", mETA=" + eta +
                '}';
    }
}
