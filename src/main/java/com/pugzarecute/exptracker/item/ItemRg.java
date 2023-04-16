package com.pugzarecute.exptracker.item;

import com.pugzarecute.exptracker.EXPTracker;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRg {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EXPTracker.MODID);

    public static void init() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());

    }

    public static final RegistryObject<Item> TRACKING_COMPASS = ITEMS.register("tracking_compass", HunterItem::new);
}
