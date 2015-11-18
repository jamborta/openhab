package org.openhab.binding.nestfirebase.internal.api.listeners;

import org.openhab.binding.nestfirebase.internal.api.model.Thermostat;

public interface ThermostatListener {
    /**
     * Called when updated data is retrieved for a Thermostat.
     * @param thermostat the new data for the thermostat (guaranteed
     *                   to be non-null)
     */
    void onThermostatUpdated(Thermostat thermostat);
}