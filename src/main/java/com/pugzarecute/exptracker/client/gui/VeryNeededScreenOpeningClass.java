package com.pugzarecute.exptracker.client.gui;

import net.minecraft.client.Minecraft;

import java.util.Map;
import java.util.UUID;

public class VeryNeededScreenOpeningClass {
    public VeryNeededScreenOpeningClass(Map<UUID,Float> players){
        Minecraft.getInstance().setScreen(new PlayerChooserScreen(players));
    }
}
