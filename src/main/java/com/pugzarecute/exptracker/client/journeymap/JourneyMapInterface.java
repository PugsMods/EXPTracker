package com.pugzarecute.exptracker.client.journeymap;

import com.pugzarecute.exptracker.EXPTracker;
import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.ParametersAreNonnullByDefault;

@ClientPlugin
@ParametersAreNonnullByDefault
public class JourneyMapInterface implements IClientPlugin {
    public IClientAPI api = null;
    private static JourneyMapInterface journeyMap;

    public JourneyMapInterface() {
        journeyMap = this;
    }

    public static JourneyMapInterface get() {
        return journeyMap;
    }

    @Override
    public void initialize(final IClientAPI api) {
        this.api = api;
        MinecraftForge.EVENT_BUS.register(api);
    }

    @Override
    public String getModId() {
        return EXPTracker.MODID;
    }

    @Override
    public void onEvent(ClientEvent clientEvent) {
        //dotn need
    }
}
