package com.pugzarecute.exptracker.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class RequestPlayersPacketC2S {
    List<UUID> uuids = new ArrayList<>();

    public RequestPlayersPacketC2S() {
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {

    }

    public static RequestPlayersPacketC2S decode(FriendlyByteBuf friendlyByteBuf) {
        return new RequestPlayersPacketC2S();
    }

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
        final var success = new AtomicBoolean();
        contextSupplier.get().enqueueWork(() -> {

            Level level = contextSupplier.get().getSender().level;
            Player player = contextSupplier.get().getSender();

            List<Player> players = new ArrayList<>(level.getServer().getPlayerList().getPlayers());
            players.removeIf(l -> !l.level.dimension().location().toString().equals(level.dimension().location().toString()));
            players.removeIf(h -> h.getUUID().equals(player.getUUID()));

            Map<UUID, Float> player2distanceMap = new LinkedHashMap<>();

            for (Player p : players) {
                player2distanceMap.put(p.getUUID(), p.distanceTo(player));
            }

            EXPTrackerPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> contextSupplier.get().getSender()), new PlayerListPacketS2C(player2distanceMap));
            success.set(true);
        });

        contextSupplier.get().setPacketHandled(true);
        return success.get();
    }
}

