package com.pugzarecute.exptracker.networking;

import com.pugzarecute.exptracker.item.HunterItem;
import com.pugzarecute.exptracker.item.ItemRg;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import com.pugzarecute.exptracker.server.Handler;

public class InitHuntPacketC2S {
    public final UUID whom;

    public InitHuntPacketC2S(UUID whom){
        this.whom=whom;
    }
    public void encode(FriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeUUID(whom);
    }
    public static InitHuntPacketC2S decode(FriendlyByteBuf friendlyByteBuf){
        return  new InitHuntPacketC2S(friendlyByteBuf.readUUID());
    }

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier){
        final var success= new AtomicBoolean();
        success.set(false);
        contextSupplier.get().enqueueWork(() ->{

            Level level = contextSupplier.get().getSender().level;
            Player player = contextSupplier.get().getSender();
            if(player.getPersistentData().getBoolean("exptracker.safety_token")){
                if(player.getUUID().equals(this.whom)){
                    player.sendSystemMessage(Component.translatable("exptracker.self_hunt"));
                    contextSupplier.get().getSender().getInventory().add(ItemRg.TRACKING_COMPASS.get().getDefaultInstance());
                } else if (player.getServer().getPlayerList().getPlayerCount()<=1) {
                    player.sendSystemMessage(Component.translatable("exptracker.no_players"));
                    contextSupplier.get().getSender().getInventory().add(ItemRg.TRACKING_COMPASS.get().getDefaultInstance());
                } else if (!(level.dimensionTypeId() == BuiltinDimensionTypes.OVERWORLD)) {
                    contextSupplier.get().getSender().getInventory().add(ItemRg.TRACKING_COMPASS.get().getDefaultInstance());

                    player.sendSystemMessage(Component.translatable("exptracker.overworld"));
                    player.getCooldowns().addCooldown(ItemRg.TRACKING_COMPASS.get(), 20);
                } else if (player.getPersistentData().getBoolean("exptracker.currently_hunting")) {
                    contextSupplier.get().getSender().getInventory().add(ItemRg.TRACKING_COMPASS.get().getDefaultInstance());

                    player.sendSystemMessage(Component.translatable("exptracker.hunt_in_progress"));
                    player.getCooldowns().addCooldown(ItemRg.TRACKING_COMPASS.get(), 20);
                } else if (contextSupplier.get().getSender().level.dimensionTypeId() != contextSupplier.get().getSender().getServer().getPlayerList().getPlayer(whom).level.dimensionTypeId()) {
                    contextSupplier.get().getSender().getInventory().add(ItemRg.TRACKING_COMPASS.get().getDefaultInstance());

                    contextSupplier.get().getSender().sendSystemMessage(Component.translatable("exptracker.dimension"));
                    contextSupplier.get().getSender().getPersistentData().putBoolean("exptracker.safety_token", false);
                } else {
                    success.set(true);
                    Handler.onPlayerChoose(contextSupplier.get().getSender(), contextSupplier.get().getSender().getLevel().getServer().getPlayerList().getPlayer(whom));
                }
                if(!success.get()) Handler.cleanup(player,player);
            }
        });
        contextSupplier.get().setPacketHandled(true);
        return success.get();
    }

    public void  messageConsumer(Supplier<NetworkEvent.Context> context){

    }
}

