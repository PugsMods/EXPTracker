package com.pugzarecute.exptracker.item;


import com.pugzarecute.exptracker.networking.EXPTrackerPacketHandler;
import com.pugzarecute.exptracker.networking.RequestPlayersPacketC2S;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class HunterItem extends Item {
    public HunterItem() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).rarity(Rarity.EPIC).setNoRepair().durability(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        //TODO: devhack

        if (!level.isClientSide) {
            CompoundTag playerData = player.getPersistentData();
            playerData.putBoolean("exptracker.safety_token", true);
            player.getItemInHand(hand).shrink(1);
        } else {
            EXPTrackerPacketHandler.CHANNEL.sendToServer(new RequestPlayersPacketC2S());
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
