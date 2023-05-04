package com.pugzarecute.exptracker;

import com.mojang.logging.LogUtils;
import com.pugzarecute.exptracker.capability.HuntCapability;
import com.pugzarecute.exptracker.capability.HuntCapabilityProvider;
import com.pugzarecute.exptracker.item.ItemRg;
import com.pugzarecute.exptracker.networking.EXPTrackerPacketHandler;
import com.pugzarecute.exptracker.networking.HuntDataPacketS2C;
import com.pugzarecute.exptracker.server.Handler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Mod(EXPTracker.MODID)
public class EXPTracker {
    public static final String MODID = "exptracker";
    private static final Logger LOGGER = LogUtils.getLogger();

    public EXPTracker() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        ItemRg.init();
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        EXPTrackerPacketHandler.init();
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            event.getEntity().getCapability(HuntCapabilityProvider.HUNT_CAPABILITY).ifPresent(hunt ->{
            if (hunt.isCurrentlyHunting()) {
                if (!hunt.isHunter()) {
                    //Hunted death
                    Player hunter = event.getEntity().getLevel().getServer().getPlayerList().getPlayer(hunt.getOther());
                    hunter.sendSystemMessage(Component.translatable("exptracker.hunted_death"));
                    Handler.cleanup(hunter, (Player) event.getEntity());
                } else {
                    //Hunter death
                    Player hunted = event.getEntity().getLevel().getServer().getPlayerList().getPlayer(hunt.getOther());
                    hunted.sendSystemMessage(Component.translatable("exptracker.hunter_death"));
                    Handler.cleanup((Player) event.getEntity(), hunted);
                }
            }
            });
        }
    }

    @SubscribeEvent
    public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {

        Player player = event.getEntity();
        player.getCapability(HuntCapabilityProvider.HUNT_CAPABILITY).ifPresent(hunt ->{
        if (hunt.isCurrentlyHunting()&& (!hunt.isHunter())  && event.getTo().location().toString().equals("javd:void")) {
            //Hunted
            Player hunter = player.getLevel().getServer().getPlayerList().getPlayer(hunt.getOther());
            hunter.sendSystemMessage(Component.translatable("exptracker.hunter_lose"));
            player.sendSystemMessage(Component.translatable("exptracker.hunt_win"));
            Handler.cleanup(hunter, player);
        }
        });
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        player.getCapability(HuntCapabilityProvider.HUNT_CAPABILITY).ifPresent(hunt->{
        if (hunt.isCurrentlyHunting() && (!hunt.isHunter())) {
            //Hunted
            Player hunter = player.getLevel().getServer().getPlayerList().getPlayer(hunt.getOther());
            hunter.sendSystemMessage(Component.translatable("exptracker.hunted_left"));
            Handler.cleanup(hunter, player);
            hunter.getInventory().add(ItemRg.TRACKING_COMPASS.get().getDefaultInstance());
        } else if (hunt.isCurrentlyHunting() && hunt.isCurrentlyHunting()) {
            //Hunter
            Player hunted = player.getLevel().getServer().getPlayerList().getPlayer(hunt.getOther());
            hunted.sendSystemMessage(Component.translatable("exptracker.hunter_leave"));
            Handler.cleanup(player, hunted);
        }
        });
    }

    @SubscribeEvent
    public void onRegisterCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(new ResourceLocation(MODID,"huntdata"),new HuntCapabilityProvider());
        }
    }
    @SubscribeEvent
    public void onClone(PlayerEvent.Clone event){
        if(event.isWasDeath()){
            event.getOriginal().getCapability(HuntCapabilityProvider.HUNT_CAPABILITY).ifPresent(old ->
                        event.getOriginal().getCapability(HuntCapabilityProvider.HUNT_CAPABILITY).ifPresent(newS -> newS.copyFrom(old))
                    );
        }
    }
    @SubscribeEvent
    public void onCapReg(RegisterCapabilitiesEvent event){
        event.register(HuntCapability.class);
    }
}
