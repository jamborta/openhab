package org.openhab.binding.nestfirebase.internal.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public  enum HVACState {
    HEATING("heating"),
    COOLING("cooling"),
    OFF("off"),
    UNKNOWN("unknown");

    private final String key;

    HVACState(String key) {
        this.key = key;
    }

    @JsonValue
    public String getKey() {
        return key;
    }

    @JsonCreator
    public static HVACState fromString(String key) {
        return key == null ? UNKNOWN : HVACState.valueOf(key.toUpperCase());
    }
}
