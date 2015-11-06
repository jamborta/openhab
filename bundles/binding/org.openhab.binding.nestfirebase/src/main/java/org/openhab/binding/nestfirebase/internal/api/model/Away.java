package org.openhab.binding.nestfirebase.internal.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Away {
    /** State when the user has explicitly set the structure to "Away" state. */
    AWAY("away"),
    /** State when Nest has detected that the structure is not occupied. */
    AUTO_AWAY("auto-away"),
    /** State when the user is at home */
    HOME("home"),
    /** State for when the home/away status is unknown */
    UNKNOWN("unknown");

    private final String key;

    Away(String key) {
        this.key = key;
    }
    
    @JsonCreator
    public static Away fromString(String key) {
        return key == null ? UNKNOWN : Away.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String getKey() {
        return key;
    }        
}
