package com.pugzarecute.exptracker.networking;

import com.pugzarecute.exptracker.EXPTracker;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class EXPTrackerPacketHandler {
    public  static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(EXPTracker.MODID,"hunt"), () -> PROTOCOL_VERSION,PROTOCOL_VERSION::equals,PROTOCOL_VERSION::equals);

    public static void init(){
        int id = 0;
        CHANNEL.messageBuilder(InitHuntPacketC2S.class,id++, NetworkDirection.PLAY_TO_SERVER).encoder(InitHuntPacketC2S::encode).decoder(InitHuntPacketC2S::decode).consumerMainThread(InitHuntPacketC2S::handle).add();
        CHANNEL.messageBuilder(HuntDataPacketS2C.class,id++, NetworkDirection.PLAY_TO_CLIENT).encoder(HuntDataPacketS2C::encode).decoder(HuntDataPacketS2C::decode).consumerMainThread(HuntDataPacketS2C::handle).add();
        CHANNEL.messageBuilder(RequestPlayersPacketC2S.class,id++,NetworkDirection.PLAY_TO_SERVER).encoder(RequestPlayersPacketC2S::encode).decoder(RequestPlayersPacketC2S::decode).consumerMainThread(RequestPlayersPacketC2S::handle).add();
        CHANNEL.messageBuilder(PlayerListPacketS2C.class,id++, NetworkDirection.PLAY_TO_CLIENT).encoder(PlayerListPacketS2C::encode).decoder(PlayerListPacketS2C::decode).consumerMainThread(PlayerListPacketS2C::handle).add();
        CHANNEL.messageBuilder(RefundItemsIfPlayerlistEmptyC2S.class, id++,NetworkDirection.PLAY_TO_SERVER).encoder(RefundItemsIfPlayerlistEmptyC2S::encode).decoder(RefundItemsIfPlayerlistEmptyC2S::decode).consumerMainThread(RefundItemsIfPlayerlistEmptyC2S::handle).add();
    }
}
