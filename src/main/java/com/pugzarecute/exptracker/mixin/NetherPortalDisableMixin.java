package com.pugzarecute.exptracker.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(NetherPortalBlock.class)
public class NetherPortalDisableMixin {

    /**
     * @author PugzAreCute
     * @reason Disable Nether Portals for players being hunted and hunters
     */
    @Overwrite
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (!entity.isPassenger() && !entity.isVehicle() && entity.canChangeDimensions()&& !entity.getPersistentData().getBoolean("exptracker.currently_hunting")) {
            entity.handleInsidePortal(blockPos);
        }

    }
}
