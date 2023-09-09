package com.pugzarecute.exptracker.mixin;

import com.pugzarecute.exptracker.capability.HuntCapabilityProvider;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
public class AntiDimensionMixin {

    @Inject(method = "canChangeDimensions()Z", at=@At("RETURN"), cancellable = true)
    private void inject(CallbackInfoReturnable<Boolean> cir){
        ((Entity)(Object)this).getCapability(HuntCapabilityProvider.HUNT_CAPABILITY).ifPresent((cap)->{
            if(cap.isCurrentlyHunting()) cir.setReturnValue(false);
        });
    }
}
