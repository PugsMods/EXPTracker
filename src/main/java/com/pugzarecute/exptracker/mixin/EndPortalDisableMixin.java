package com.pugzarecute.exptracker.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EndPortalBlock.class)
public class EndPortalDisableMixin {

    /**
     * @author PugzAreCute
     * @reason Disable End Portals for players being hunted and hunters
     */
    @Overwrite
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (!entity.isPassenger() && !entity.isVehicle() && entity.canChangeDimensions() && !entity.getPersistentData().getBoolean("exptracker.currently_hunting")) {
            entity.handleInsidePortal(blockPos);
        }

    }
}
