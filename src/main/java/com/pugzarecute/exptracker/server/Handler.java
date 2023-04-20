package com.pugzarecute.exptracker.server;

import com.pugzarecute.exptracker.capability.HuntCapabilityProvider;
import com.pugzarecute.exptracker.networking.EXPTrackerPacketHandler;
import com.pugzarecute.exptracker.networking.HuntDataPacketS2C;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public class Handler {
    public static void onPlayerChoose(ServerPlayer mainPlayer, Player hunted) {

        CompoundTag playerData = mainPlayer.getPersistentData();
        if (playerData.getBoolean("exptracker.safety_token")) {
            mainPlayer.getCapability(HuntCapabilityProvider.HUNT_CAPABILITY).ifPresent(hunt ->{
                hunt.setCurrentlyHunting(true);
                hunt.setHunter(true);
                hunt.setOther(hunted.getUUID());
            });
            playerData.putBoolean("exptracker.safety_token", false);
            hunted.sendSystemMessage(Component.translatable("exptracker.hunted", mainPlayer.getName().getString()));
            hunted.getCapability(HuntCapabilityProvider.HUNT_CAPABILITY).ifPresent(hunt -> {
                hunt.setOther(mainPlayer.getUUID());
                hunt.setHunter(false);
                hunt.setCurrentlyHunting(true);
            });

            mainPlayer.sendSystemMessage(Component.translatable("exptracker.hunt_start"));

            HuntDataService huntDataService = new HuntDataService(mainPlayer, hunted);
            huntDataService.start();
            return;
        }
        System.out.println("!!!!!!!!Player " + mainPlayer.getName().getString() + " attempted packet spoofing!!!!!!!");
        System.out.println("Ban suggested.");

    }

    public static void cleanup(Player hunter, Player hunted) {

        CompoundTag playerData = hunter.getPersistentData();
        hunter.getCapability(HuntCapabilityProvider.HUNT_CAPABILITY).ifPresent(hunt ->{
            hunt.setCurrentlyHunting(false);
        });
        playerData.putBoolean("exptracker.safety_token", false);
        hunted.getCapability(HuntCapabilityProvider.HUNT_CAPABILITY).ifPresent(hunt -> {
            hunt.setCurrentlyHunting(false);
        });

        CompoundTag huntedData = hunted.getPersistentData();
        huntedData.remove("exptracker.safety_token");

        EXPTrackerPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) hunter), new HuntDataPacketS2C(true, 0, 0, 0));
    }
}
