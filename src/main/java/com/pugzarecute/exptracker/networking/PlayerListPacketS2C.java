package com.pugzarecute.exptracker.networking;

import com.pugzarecute.exptracker.EXPTracker;
import com.pugzarecute.exptracker.client.gui.PlayerChooserPlayerList;
import com.pugzarecute.exptracker.client.gui.PlayerChooserScreen;
import com.pugzarecute.exptracker.client.gui.VeryNeededScreenOpeningClass;
import com.pugzarecute.exptracker.client.journeymap.JourneyMapInterface;
import com.pugzarecute.exptracker.client.journeymap.NewPolyRenderer;
import journeymap.client.api.display.Waypoint;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class PlayerListPacketS2C {
    public final Map<UUID,Float> players;

    public PlayerListPacketS2C(Map<UUID,Float> players){
        this.players = players;
    }
    public void encode(FriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeInt(players.size());
        players.entrySet().stream().forEach(k ->
        {
            friendlyByteBuf.writeUUID(k.getKey());
            friendlyByteBuf.writeFloat(k.getValue());
        });
    }
    public static PlayerListPacketS2C decode(FriendlyByteBuf friendlyByteBuf){
        Map<UUID,Float> returnMap = new LinkedHashMap<>();
        int m = friendlyByteBuf.readInt();
        int x = 0;
        while (x<m){
            returnMap.put(friendlyByteBuf.readUUID(),friendlyByteBuf.readFloat());
            x++;
        }
        return new PlayerListPacketS2C(returnMap);
    }

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier){
        final var success= new AtomicBoolean();

        contextSupplier.get().enqueueWork(() ->{
            new VeryNeededScreenOpeningClass(players);
            success.set(true);
        });
        contextSupplier.get().setPacketHandled(true);
        return success.get();
    }

    public void  messageConsumer(Supplier<NetworkEvent.Context> context){

    }
}

