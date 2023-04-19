package com.pugzarecute.exptracker.capability;

import java.util.UUID;

public class HuntCapability {
    private boolean isHunter;
    private boolean currently_hunting;
    UUID other;

    public UUID getOther() {
        return other;
    }


    public boolean isHunter() {
        return isHunter;
    }

    public boolean isCurrentlyHunting() {
        return currently_hunting;
    }

    public void setOther(UUID other) {
        this.other = other;
    }

    public void setHunter(boolean hunter) {
        isHunter = hunter;
    }

    public void setCurrentlyHunting(boolean currently_hunting) {
        this.currently_hunting = currently_hunting;
    }
    public void copyFrom(HuntCapability capability){
        this.other = capability.other;
        this.isHunter = capability.isHunter;
        this.currently_hunting = capability.currently_hunting;
    }
}
