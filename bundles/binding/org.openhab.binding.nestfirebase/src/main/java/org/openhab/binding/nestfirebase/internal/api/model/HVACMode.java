package org.openhab.binding.nestfirebase.internal.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum HVACMode {
    HEAT("heat"),
    COOL("cool"),
    HEAT_AND_COOL("heat-cool"),
    OFF("off"),
    UNKNOWN("unknown");

    private final String key;

    HVACMode(String key) {
        this.key = key;
    }

    @JsonValue
    public String getKey() {
        return key;
    }
    
    @JsonCreator
    public static HVACMode fromString(String key) {
        return key == null ? UNKNOWN : HVACMode.valueOf(key.toUpperCase());
    }


}
