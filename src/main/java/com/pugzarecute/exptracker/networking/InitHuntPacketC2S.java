package com.pugzarecute.exptracker.networking;

import com.pugzarecute.exptracker.EXPTracker;
import com.pugzarecute.exptracker.capability.HuntCapabilityProvider;
import com.pugzarecute.exptracker.item.ItemRg;
import com.pugzarecute.exptracker.server.Handler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class InitHuntPacketC2S {
    public final UUID whom;

    public InitHuntPacketC2S(UUID whom) {
        this.whom = whom;
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(whom);
    }

    public static InitHuntPacketC2S decode(FriendlyByteBuf friendlyByteBuf) {
        return new InitHuntPacketC2S(friendlyByteBuf.readUUID());
    }

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
        final var success = new AtomicBoolean();
        success.set(false);
        contextSupplier.get().enqueueWork(() -> {

            Level level = contextSupplier.get().getSender().level;
            Player player = contextSupplier.get().getSender();
            if (player.getPersistentData().getBoolean("exptracker.safety_token")) {
                player.getCapability(HuntCapabilityProvider.HUNT_CAPABILITY).ifPresent(hunt ->{
                if (player.getUUID().equals(this.whom)) {
                    player.sendSystemMessage(Component.translatable("exptracker.self_hunt"));
                    EXPTracker.addItem(contextSupplier.get().getSender());
                } else if (player.getServer().getPlayerList().getPlayerCount() <= 1) {
                    player.sendSystemMessage(Component.translatable("exptracker.no_players"));
                    EXPTracker.addItem(contextSupplier.get().getSender());
                } else if (level.dimensionTypeId() != BuiltinDimensionTypes.OVERWORLD) {
                    EXPTracker.addItem(contextSupplier.get().getSender());

                    player.sendSystemMessage(Component.translatable("exptracker.overworld"));
                    player.getCooldowns().addCooldown(ItemRg.TRACKING_COMPASS.get(), 20);
                } else if (hunt.isCurrentlyHunting()) {
                    EXPTracker.addItem(contextSupplier.get().getSender());
                    success.set(true);
                    player.getPersistentData().remove("exptracker.safety_token");
                    player.sendSystemMessage(Component.translatable("exptracker.hunt_in_progress"));
                    player.getCooldowns().addCooldown(ItemRg.TRACKING_COMPASS.get(), 20);
                } else if (contextSupplier.get().getSender().level.dimensionTypeId() != contextSupplier.get().getSender().getServer().getPlayerList().getPlayer(whom).level.dimensionTypeId()) {
                    EXPTracker.addItem(contextSupplier.get().getSender());

                    contextSupplier.get().getSender().sendSystemMessage(Component.translatable("exptracker.dimension"));
                    contextSupplier.get().getSender().getPersistentData().putBoolean("exptracker.safety_token", false);
                } else {
                    success.set(true);
                    Handler.onPlayerChoose(contextSupplier.get().getSender(), contextSupplier.get().getSender().getLevel().getServer().getPlayerList().getPlayer(whom));
                }});
                if (!success.get()) Handler.cleanup(player, player);
            }
        });
        contextSupplier.get().setPacketHandled(true);
        return success.get();
    }

    public void messageConsumer(Supplier<NetworkEvent.Context> context) {

    }
}

