package com.pugzarecute.exptracker.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class PlayerChooserPlayerList extends ContainerObjectSelectionList<CustomPlayerEntry> {
    private final PlayerChooserScreen screen;
    private final List<CustomPlayerEntry> players = Lists.newArrayList();

    @Nullable
    private String filter;

    public PlayerChooserPlayerList(PlayerChooserScreen screen, Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
        super(minecraft, width, height, y0, y1, itemHeight);
        this.screen = screen;
        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);
    }

    public void render(PoseStack postStack, int mouseX, int mouseY, float partialTicks) {
        double guiScale = this.minecraft.getWindow().getGuiScale();
        RenderSystem.enableScissor((int) ((double) this.getRowLeft() * guiScale), (int) ((double) (this.height - this.y1) * guiScale), (int) ((double) (this.getScrollbarPosition() + 6) * guiScale), (int) ((double) (this.height - (this.height - this.y1) - this.y0 - 4) * guiScale));
        super.render(postStack, mouseX, mouseY, partialTicks);
        RenderSystem.disableScissor();
    }

    public void updatePlayerList(Map<UUID, Float> players, double scrollAmount) {
        Map<UUID, CustomPlayerEntry> map = new LinkedHashMap<>();
        addOnlinePlayers(players, map);
        resetList(map.values(), scrollAmount);
    }

    private void addOnlinePlayers(Map<UUID, Float> playerIDS, Map<UUID, CustomPlayerEntry> listings) {
        ClientPacketListener clientpacketlistener = minecraft.player.connection;

        playerIDS.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach((entry -> {
                    PlayerInfo info = clientpacketlistener.getPlayerInfo(entry.getKey());
                    if (info != null) {
                        UUID uuid = info.getProfile().getId();
                        listings.put(uuid, new CustomPlayerEntry(minecraft, screen, uuid, info.getProfile().getName(), info::getSkinLocation));
                    }
                }));
    }

    private void resetList(Collection<CustomPlayerEntry> playerEntries, double scrollAmount) {
        players.clear();
        players.addAll(playerEntries);
        getPlayersMatchingFilter();
        replaceEntries(players);
        setScrollAmount(scrollAmount);
    }

    private void getPlayersMatchingFilter() {
        if (filter != null) {
            players.removeIf((entry) ->
                    !entry.getPlayerName().toLowerCase().contains(filter)
            );
        }
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }
}
