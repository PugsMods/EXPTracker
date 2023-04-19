package com.pugzarecute.exptracker.capability;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HuntCapabilityProvider implements ICapabilityProvider {
    public static Capability<HuntCapability> HUNT_CAPABILITY = CapabilityManager.get(new CapabilityToken<HuntCapability>() {});
    private HuntCapability huntCapability = null;
    private final LazyOptional<HuntCapability> optional = LazyOptional.of(this::create);

    private HuntCapability create(){
        if(this.huntCapability == null) huntCapability = new HuntCapability();
        return huntCapability;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == HUNT_CAPABILITY) return optional.cast();
        return LazyOptional.empty();
    }
}
