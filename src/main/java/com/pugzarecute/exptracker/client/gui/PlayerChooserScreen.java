package com.pugzarecute.exptracker.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.pugzarecute.exptracker.networking.EXPTrackerPacketHandler;
import com.pugzarecute.exptracker.networking.RefundItemsIfPlayerlistEmptyC2S;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class PlayerChooserScreen extends Screen {
    protected static final ResourceLocation TEXTURE = new ResourceLocation("exptracker:hunt/img2.png");
    private static final Component SEARCH_HINT = Component.translatable("exptracker.search_hint").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
    static final Component EMPTY_SEARCH = Component.translatable("exptracker.empty_search").withStyle(ChatFormatting.GRAY);
    public PlayerChooserPlayerList playerList;
    EditBox searchBox;
    private String lastSearch = "";
    private boolean initialized;
    protected static boolean used = false;

    protected final Map<UUID, Float> players;

    public PlayerChooserScreen(Map<UUID, Float> players) {
        super(Component.literal("HuntScreen"));
        System.out.println(players.size());
        this.players = players;
    }

    private int windowHeight() {
        return Math.max(52, height - 128 - 16);
    }

    private int backgroundUnits() {
        return windowHeight() / 16;
    }

    private int listEnd() {
        return 80 + backgroundUnits() * 16 - 8;
    }

    private int marginX() {
        return (width - 238) / 2;
    }

    public void tick() {
        super.tick();
        searchBox.tick();
    }

    protected void init() {
        minecraft.keyboardHandler.setSendRepeatsToGui(true);
        if (initialized) {
            playerList.updateSize(width, height, 88, listEnd());
        } else {
            playerList = new PlayerChooserPlayerList(this, minecraft, width, height, 88, listEnd(), 36);
        }

        showPlayerList();
        String searchBoxValue = searchBox != null ? searchBox.getValue() : "";
        searchBox = new EditBox(font, marginX() + 28, 78, 196, 16, SEARCH_HINT) {
            protected MutableComponent createNarrationMessage() {
                return !PlayerChooserScreen.this.searchBox.getValue().isEmpty() && PlayerChooserScreen.this.playerList.isEmpty() ? super.createNarrationMessage().append(", ").append(PlayerChooserScreen.EMPTY_SEARCH) : super.createNarrationMessage();
            }
        };
        searchBox.setMaxLength(16);
        searchBox.setBordered(false);
        searchBox.setVisible(true);
        searchBox.setTextColor(16777215);
        searchBox.setValue(searchBoxValue);
        searchBox.setResponder(this::doSearch);
        addWidget(searchBox);
        addWidget(playerList);
        initialized = true;
        showPlayerList();
    }

    private void showPlayerList() {
        playerList.updatePlayerList(players, playerList.getScrollAmount());
    }

    public void removed() {
        minecraft.keyboardHandler.setSendRepeatsToGui(false);
        if (!used) EXPTrackerPacketHandler.CHANNEL.sendToServer(new RefundItemsIfPlayerlistEmptyC2S());
    }

    public void renderBackground(PoseStack poseStack) {
        int i = marginX() + 3;
        super.renderBackground(poseStack);
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(poseStack, i, 64, 1, 1, 236, 8);
        int j = backgroundUnits();

        for (int k = 0; k < j; ++k) {
            blit(poseStack, i, 72 + 16 * k, 1, 10, 236, 16);
        }

        blit(poseStack, i, 72 + 16 * j, 1, 27, 236, 8);
        blit(poseStack, i + 10, 76, 243, 1, 12, 12);
    }

    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (players.size() == 0) {
            Minecraft.getInstance().player.sendSystemMessage(Component.translatable("exptracker.no_players"));
            Minecraft.getInstance().popGuiLayer();
        }
        renderBackground(poseStack);
        drawString(poseStack, minecraft.font, Component.literal("Choose a player to hunt."), marginX() + 8, 35, -1);
        drawString(poseStack, minecraft.font, Component.literal("Portals will be disabled."), marginX() + 8, 51, -1);

        if (!playerList.isEmpty()) {
            playerList.render(poseStack, mouseX, mouseY, partialTicks);
        } else if (!searchBox.getValue().isEmpty()) {
            drawCenteredString(poseStack, minecraft.font, EMPTY_SEARCH, width / 2, (78 + listEnd()) / 2, -1);
        }

        if (!searchBox.isFocused() && searchBox.getValue().isEmpty()) {
            drawString(poseStack, minecraft.font, SEARCH_HINT, searchBox.x, searchBox.y, -1);
        } else {
            searchBox.render(poseStack, mouseX, mouseY, partialTicks);
        }

    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (searchBox.isFocused()) {
            searchBox.mouseClicked(mouseX, mouseY, button);
        }

        return super.mouseClicked(mouseX, mouseY, button) || playerList.mouseClicked(mouseX, mouseY, button);
    }

    public boolean keyPressed(int keyCode, int mouseY, int mouseX) {
        if (!searchBox.isFocused()) {
            minecraft.setScreen(null);
            return true;
        } else {
            return super.keyPressed(keyCode, mouseY, mouseX);
        }
    }

    public boolean isPauseScreen() {
        return false;
    }

    private void doSearch(String search) {
        search = search.toLowerCase();
        if (!search.equals(lastSearch)) {
            playerList.setFilter(search);
            lastSearch = search;
            showPlayerList();
        }

    }
}
