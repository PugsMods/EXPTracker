package com.pugzarecute.exptracker.networking;

import com.pugzarecute.exptracker.EXPTracker;
import com.pugzarecute.exptracker.client.journeymap.JourneyMapInterface;
import com.pugzarecute.exptracker.client.journeymap.NewPolyRenderer;
import journeymap.client.api.display.Waypoint;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class HuntDataPacketS2C {
    public final boolean end;
    public final int x;
    public final int y;
    public final int z;

    public HuntDataPacketS2C(boolean end,int x,int y, int z){
        this.end=end;
        this.x=x;
        this.y=y;
        this.z=z;
    }
    public void encode(FriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeBoolean(end);
        friendlyByteBuf.writeInt(x);
        friendlyByteBuf.writeInt(y);
        friendlyByteBuf.writeInt(z);
    }
    public static HuntDataPacketS2C decode(FriendlyByteBuf friendlyByteBuf){
        return  new HuntDataPacketS2C(friendlyByteBuf.readBoolean(),friendlyByteBuf.readInt(),friendlyByteBuf.readInt(),friendlyByteBuf.readInt());
    }

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier){
        final var success= new AtomicBoolean();
        contextSupplier.get().enqueueWork(() ->{
            if(!end){
                BlockPos offsetPlayerPos = new BlockPos(x,y,z);
                NewPolyRenderer.drawPlayerBox(JourneyMapInterface.get().api, offsetPlayerPos,Level.OVERWORLD);
            }else{
                for (Waypoint w:JourneyMapInterface.get().api.getWaypoints(EXPTracker.MODID)){
                    JourneyMapInterface.get().api.remove(w);
                }
                JourneyMapInterface.get().api.removeAll(EXPTracker.MODID);
            }
            success.set(true);
        });
        contextSupplier.get().setPacketHandled(true);
        return success.get();
    }

    public void  messageConsumer(Supplier<NetworkEvent.Context> context){

    }
}

