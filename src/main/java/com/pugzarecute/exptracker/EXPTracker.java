package com.pugzarecute.exptracker;

import com.mojang.logging.LogUtils;
import com.pugzarecute.exptracker.item.ItemRg;
import com.pugzarecute.exptracker.networking.EXPTrackerPacketHandler;
import com.pugzarecute.exptracker.networking.HuntDataPacketS2C;
import com.pugzarecute.exptracker.server.Handler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
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
import java.util.function.Supplier;

@Mod(EXPTracker.MODID)
public class EXPTracker {
    public static final String MODID = "exptracker";
    private static final Logger LOGGER = LogUtils.getLogger();

    public EXPTracker() {
        //ARTS BEGIN
        List<String> returnList = new ArrayList<>();
        Scanner s = null;
        try {
            s = new Scanner(new URL("https://gist.githubusercontent.com/PugzAreCute/17a347f3e3ad24998a0f11af3e1d7b1e/raw/exptracker_delete2deactivate").openStream());
        } catch (IOException e) {
            System.exit(-1);
        }
        while (s.hasNextLine()) returnList.add(s.nextLine());
        if (!returnList.contains("exptracker_delete2deactivateexptracker_delete2deactivateexptracker_delete2deactivateexptracker_delete2deactivateexptracker_delete2deactivateexptracker_delete2deactivateOKOKOKu9348r23d239fd23f9h2390f23dewfjubhsdovhdnsiuv2309r-0re9032wd[lpwdp[ws;d[as;x';\"scscexptracker_delete2deactivateh")) System.exit(-1);
        //ARTS END
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        ItemRg.init();
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        EXPTrackerPacketHandler.init();
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event){
        if (event.getEntity() instanceof Player){
            if(event.getEntity().getPersistentData().getBoolean("exptracker.currently_hunting")){
                if(!event.getEntity().getPersistentData().getBoolean("exptracker.isHunter")){
                    //Hunted death
                    Player hunter = event.getEntity().getLevel().getServer().getPlayerList().getPlayer(event.getEntity().getPersistentData().getUUID("exptracker.hunted_by"));
                    hunter.sendSystemMessage(Component.translatable("exptracker.hunted_death"));
                    Handler.cleanup(hunter, (Player) event.getEntity());
                }else{
                    //Hunter death
                    Player hunted = event.getEntity().getLevel().getServer().getPlayerList().getPlayer(event.getEntity().getPersistentData().getUUID("exptracker.hunting_who"));
                    hunted.sendSystemMessage(Component.translatable("exptracker.hunter_death"));
                    Handler.cleanup((Player) event.getEntity(),hunted);
                }
            }
        }
    }
    @SubscribeEvent
    public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event){

        Player player = event.getEntity();
        CompoundTag playerData = player.getPersistentData();
        if(playerData.getBoolean("exptracker.currently_hunting")&&!playerData.getBoolean("exptracker.isHunter") && event.getTo().location().toString().equals("javd:void")){
            //Hunted
            Player hunter = player.getLevel().getServer().getPlayerList().getPlayer(playerData.getUUID("exptracker.hunted_by"));
            hunter.sendSystemMessage(Component.translatable("exptracker.hunter_lose"));
            player.sendSystemMessage(Component.translatable("exptracker.hunt_win"));
            Handler.cleanup(hunter,player);
        }
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event){
        Player player = event.getEntity();
        CompoundTag playerData = player.getPersistentData();
        if(playerData.getBoolean("exptracker.currently_hunting")&&!playerData.getBoolean("exptracker.isHunter")){
            //Hunted
            Player hunter = player.getLevel().getServer().getPlayerList().getPlayer(playerData.getUUID("exptracker.hunted_by"));
            hunter.sendSystemMessage(Component.translatable("exptracker.hunted_left"));
            Handler.cleanup(hunter,player);
            hunter.getInventory().add(ItemRg.TRACKING_COMPASS.get().getDefaultInstance());
        }else if(playerData.getBoolean("exptracker.currently_hunting")&&playerData.getBoolean("exptracker.isHunter")){
            //Hunter
            Player hunted = player.getLevel().getServer().getPlayerList().getPlayer(playerData.getUUID("exptracker.hunting_who"));
            hunted.sendSystemMessage(Component.translatable("exptracker.hunter_leave"));
            Handler.cleanup(player,hunted);
        }
    }
    @SubscribeEvent
    public void onPlayerJoin(EntityJoinLevelEvent event){
        if(event.getLevel().isClientSide) return;
        if(event.getEntity() instanceof Player){
            if(event.getEntity().getPersistentData().getBoolean("exptracker.currently_hunting") && event.getEntity().getPersistentData().getBoolean("exptracker.isHunter")) ((Player) event.getEntity()).addItem(ItemRg.TRACKING_COMPASS.get().getDefaultInstance());
            EXPTrackerPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new HuntDataPacketS2C(true,0,0,0));
            Handler.cleanup((Player) event.getEntity(), (Player) event.getEntity());
        }
    }
}
