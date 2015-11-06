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
public final class Thermostat extends BaseDevice {
    private boolean can_cool;
    private boolean can_heat;
    private boolean is_using_emergency_heat;
    private boolean has_fan;
    private Date fan_timer_timeout;
    private boolean has_leaf;
    private String temperature_scale;
    private long away_temperature_high_f;
    private double away_temperature_high_c;
    private long away_temperature_low_f;
    private double away_temperature_low_c;
    private long ambient_temperature_f;
    private double ambient_temperature_c;
    private boolean fan_timer_active;
    private long target_temperature_f;
    private double target_temperature_c;
    private long target_temperature_high_f;
    private double target_temperature_high_c;
    private long target_temperature_low_f;
    private double target_temperature_low_c;
    private HVACMode hvac_mode;
    private HVACState hvac_state;
    private long humidity;

    private Thermostat() {
        super();
    }
    
    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
    	System.out.println("Unknown property Key[" + key + "] Value["+ value+"]");
    }    

    /**
     * Returns true if this thermostat is connected to a cooling system
     */
    public boolean getCan_cool() {
		return can_cool;
	}

    /**
     * Returns true if this thermostat is connected to a heating system
     */
    public boolean getCan_heat() {
		return can_heat;
	}

    /**
     * Returns true if this thermostat is currently operating using the
     * emergency heating system
     */
    public boolean getIs_using_emergency_heat() {
		return is_using_emergency_heat;
	}

    /**
     * Returns true if this thermostat has a connected fan
     */
    public boolean getHas_fan() {
		return has_fan;
	}

    /**
     * If the fan is running on a timer, this provides the timestamp
     * (in ISO-8601 format) at which the fan will stop running.
     * @see #hasFan()
     * @see #isFanTimerActive()
     */
    public Date getFan_timer_timeout() {
		return fan_timer_timeout;
	}

    /**
     * Returns true if the thermostat is currently displaying the leaf indicator
     */
    public boolean getHas_leaf() {
		return has_leaf;
	}

    /**
     * Returns the temperature scale: one of "C" (Celsius) or "F" (Fahrenheit)
     * that this thermostat should display temperatures in.
     */
    public String getTemperature_scale() {
		return temperature_scale;
	}

    /**
     * Returns the temperature (in Fahrenheit) at which the cooling
     * system will engage when in "Away" state.
     */
    public long getAway_temperature_high_f() {
		return away_temperature_high_f;
	}

    /**
     * Returns the temperature (in Celsius) at which the cooling
     * system will engage when in "Away" state.
     */
    public double getAway_temperature_high_c() {
		return away_temperature_high_c;
	}

    /**
     * Returns the temperature (in Fahrenheit) at which the heating
     * system will engage when in "Away" state.
     */
    public long getAway_temperature_low_f() {
		return away_temperature_low_f;
	}

    /**
     * Returns the temperature (in Celsius) at which the heating
     * system will engage when in "Away" state.
     */
    public double getAway_temperature_low_c() {
		return away_temperature_low_c;
	}

    /**
     * Returns the current ambient temperature in the structure
     * in Fahrenheit.
     */
    public long getAmbient_temperature_f() {
		return ambient_temperature_f;
	}

    /**
     * Returns the current ambient temperature in the structure
     * in Celsius.
     */
    public double getAmbient_temperature_c() {
		return ambient_temperature_c;
	}

    /**
     * Returns true if the fan is currently running on a timer
     * @see #getFanTimerTimeout()
     * @see #hasFan()
     */
    public boolean getFan_timer_active() {
		return fan_timer_active;
	}

    /**
     * Returns the target temperature of the thermostat in Fahrenheit.
     * Note that this is only applicable when in Heat or Cool mode,
     * not "Heat and Cool" mode.
     *
     * @see org.openhab.binding.nestfirebase.internal.api.model.Thermostat.HVACMode
     */
    public long getTarget_temperature_f() {
		return target_temperature_f;
	}

    /**
     * Returns the target temperature of the thermostat in Celsius.
     * Note that this is only applicable when in Heat or Cool mode,
     * not "Heat and Cool" mode.
     *
     * @see org.openhab.binding.nestfirebase.internal.api.model.Thermostat.HVACMode
     */
    public double getTarget_temperature_c() {
		return target_temperature_c;
	}

    /**
     * Returns the target temperature of the cooling system in Fahrenheit
     * when in "Heat and Cool" mode.
     *
     * @see org.openhab.binding.nestfirebase.internal.api.model.Thermostat.HVACMode
     */
    public long getTarget_temperature_high_f() {
		return target_temperature_high_f;
	}

    /**
     * Returns the target temperature of the cooling system in Celsius
     * when in "Heat and Cool" mode.
     *
     * @see org.openhab.binding.nestfirebase.internal.api.model.Thermostat.HVACMode
     */
    public double getTarget_temperature_high_c() {
		return target_temperature_high_c;
	}

    /**
     * Returns the target temperature of the heating system in Celsius
     * when in "Heat and Cool" mode.
     *
     * @see org.openhab.binding.nestfirebase.internal.api.model.Thermostat.HVACMode
     */
    public double getTarget_temperature_low_c() {
		return target_temperature_low_c;
	}

    /**
     * Returns the target temperature of the heating system in Fahrenheit
     * when in "Heat and Cool" mode.
     *
     * @see org.openhab.binding.nestfirebase.internal.api.model.Thermostat.HVACMode
     */
    public long getTarget_temperature_low_f() {
		return target_temperature_low_f;
	}
    
    /** 
     * Returns the humidity as a percentage 
     */
    public long getHumidity(){
        return humidity;
    }

    /**
     * Returns the current operating mode of the thermostat.
     * @see org.openhab.binding.nestfirebase.internal.api.model.Thermostat.HVACMode
     */
    public HVACMode getHvac_mode() {
		return hvac_mode;
	}

    /**
     * Returns the current state of the thermostat.
     * @see org.openhab.binding.nestfirebase.internal.api.model.Thermostat.HVACState
     */
    public HVACState getHvac_state() {
		return hvac_state;
	}

    @Override
    public String toString() {
        return "Thermostat{" +
                "ID='" + getDevice_id() + '\'' +
                ", Locale='" + getLocale() + '\'' +
                ",SoftwareVersion='" + getSoftware_version() + '\'' +
                ",StructureId='" + getStructure_id() + '\'' +
                ",Name='" + getName() + '\'' +
                ",NameLong='" + getName_long() + '\'' +
                ",LastConnected='" + getLast_connection() + '\'' +
                ",isOnline='" + getIs_online() + '\'' +
                ",canCool='" + getCan_cool() + '\'' +
                ",canHeat='" + getCan_heat() + '\'' +
                ",isUsingEmergencyHeat='" + getIs_using_emergency_heat() + '\'' +
                ",hasFan='" + getHas_fan() + '\'' +
                ",FanTimerTimeout='" + getFan_timer_timeout() + '\'' +
                ",hasLeaf='" + getHas_leaf() + '\'' +
                ",TemperatureScale='" + getTemperature_scale() + '\'' +
                ",AwayTemperatureHighC='" + getAway_temperature_high_c() + '\'' +
                ",AwayTemperatureLowC='" + getAway_temperature_low_c() + '\'' +
                ",AwayTemperatureHighF='" + getAway_temperature_high_f() + '\'' +
                ",AwayTemperatureLowF='" + getAway_temperature_low_f() + '\'' +
                ",AmbientTemperatureC='" + getAmbient_temperature_c() + '\'' +
                ",AmbientTemperatureF='" + getAmbient_temperature_f() + '\'' +
                ",isFanTimerActive='" + getFan_timer_active() + '\'' +
                ",TargetTemperatureC='" + getTarget_temperature_c() + '\'' +
                ",TargetTemperatureF='" + getTarget_temperature_f() + '\'' +
                ",TargetTemperatureHighC='" + getTarget_temperature_high_c() + '\'' +
                ",TargetTemperatureLowC='" + getTarget_temperature_low_c() + '\'' +
                ",TargetTemperatureHighF='" + getTarget_temperature_high_f() + '\'' +
                ",TargetTemperatureLowF='" + getTarget_temperature_low_f() + '\'' +
                ",HVACmode='" + getHvac_mode() + '\'' +
                ",HVACState='" + getHvac_state() + '\'' +
                ",Humidity='" + getHumidity() + '\'' +
                '}';
    }
}
