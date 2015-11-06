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

import com.fasterxml.jackson.annotation.JsonAnySetter;

/**
 * @author Neil Renaud
 * @since 1.7.0
 */
public final class SmokeCOAlarm extends BaseDevice {
    private String battery_health;
    private String co_alarm_state;
    private String smoke_alarm_state;
    private String ui_color_state;
    private Date last_manual_test_time;
    private boolean is_manual_test_active;

    private SmokeCOAlarm() {
        super();
    }
    
    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
    	System.out.println("Unknown property Key[" + key + "] Value["+ value+"]");
      // do something: put to a Map; log a warning, whatever
    } 

    public String getBattery_health() {
		return battery_health;
	}

    public String getCo_alarm_state() {
		return co_alarm_state;
	}

    public String getSmoke_alarm_state() {
		return smoke_alarm_state;
	}

    public String getUi_color_state() {
		return ui_color_state;
	}
    
    public Date getLast_manual_test_time() {
		return last_manual_test_time;
	}
    
    public boolean getIs_manual_test_active() {
		return is_manual_test_active;
	}
    
    @Override
    public String toString() {
        return "Protect{" +
                "ID='" + getDevice_id() + '\'' +
                ", Locale='" + getLocale() + '\'' +
                ",SoftwareVersion='" + getSoftware_version() + '\'' +
                ",StructureId='" + getStructure_id() + '\'' +
                ",Name='" + getName() + '\'' +
                ",NameLong='" + getName_long() + '\'' +
                ",LastConnected='" + getLast_connection() + '\'' +
                ",isOnline='" + getIs_online() + '\'' +
                ",BatteryHealth='" + getBattery_health() + '\'' +
                ",COAlarmState='" + getCo_alarm_state() + '\'' +
                ",SmokeAlarmState='" + getSmoke_alarm_state() + '\'' +
                ",UIColorState='" + getUi_color_state() + '\'' +
                ",LastManualTestTime='" + getLast_manual_test_time() + '\'' +
                '}';
    }
}
