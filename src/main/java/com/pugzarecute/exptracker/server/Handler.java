package com.pugzarecute.exptracker.server;

import com.pugzarecute.exptracker.networking.EXPTrackerPacketHandler;
import com.pugzarecute.exptracker.networking.HuntDataPacketS2C;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public class Handler {
    public static void onPlayerChoose(ServerPlayer mainPlayer, Player hunted){

        CompoundTag playerData = mainPlayer.getPersistentData();
        if(playerData.getBoolean("exptracker.safety_token")){
            playerData.putBoolean("exptracker.isHunter",true);
            playerData.putBoolean("exptracker.safety_token",false);
            playerData.putUUID("exptracker.hunting_who",hunted.getUUID());
            playerData.putBoolean("exptracker.currently_hunting",true);
            hunted.sendSystemMessage(Component.translatable("exptracker.hunted",hunted.getName().getString()));
            CompoundTag huntedData = hunted.getPersistentData();
            huntedData.putUUID("exptracker.hunted_by",mainPlayer.getUUID());
            huntedData.putBoolean("exptracker.currently_hunting",true);
            huntedData.putBoolean("exptracker.isHunter",false);

            mainPlayer.sendSystemMessage(Component.translatable("exptracker.hunt_start"));

            HuntDataService huntDataService = new HuntDataService(mainPlayer,hunted);
            huntDataService.start();
            return;
        }
        System.out.println("!!!!!!!!Player "+mainPlayer.getName().getString()+" attempted packet spoofing!!!!!!!");
        System.out.println("Ban suggested.");

    }
    public static void cleanup(Player hunter, Player hunted){

        CompoundTag playerData = hunter.getPersistentData();
        playerData.remove("exptracker.safety_token");
        playerData.remove("exptracker.hunting_who");
        playerData.remove("exptracker.currently_hunting");
        playerData.remove("exptracker.isHunter");

        CompoundTag huntedData = hunted.getPersistentData();
        huntedData.remove("exptracker.hunted_by");
        huntedData.remove("exptracker.isHunter");
        huntedData.remove("exptracker.currently_hunting");
        huntedData.remove("exptracker.safety_token");

        EXPTrackerPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) hunter),new HuntDataPacketS2C(true,0,0,0));
    }
}
