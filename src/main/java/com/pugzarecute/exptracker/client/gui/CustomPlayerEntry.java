package com.pugzarecute.exptracker.client.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.pugzarecute.exptracker.TruncationUtils;
import com.pugzarecute.exptracker.networking.EXPTrackerPacketHandler;
import com.pugzarecute.exptracker.networking.InitHuntPacketC2S;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.client.gui.GuiComponent.blit;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class CustomPlayerEntry extends ContainerObjectSelectionList.Entry<CustomPlayerEntry> {
    private final Minecraft minecraft;
    private final List<AbstractWidget> children;
    private final String playerName;
    private final Supplier<ResourceLocation> skinGetter;
    @Nullable
    private Button huntButton;
    @Nullable
    final List<FormattedCharSequence> hideTooltip;
    float tooltipHoverTime;
    PlayerChooserScreen screen;

    UUID uuid;
    private static final Component HIDE_TEXT_TOOLTIP = Component.literal("Select player to hunt");
    public static final int PLAYERNAME_COLOR = FastColor.ARGB32.color(255, 255, 255, 255);
    public static final int PLAYER_STATUS_COLOR = FastColor.ARGB32.color(140, 255, 255, 255);

    public CustomPlayerEntry(final Minecraft minecraft, final PlayerChooserScreen screen, UUID uuid, String name, Supplier<ResourceLocation> skinProvider) {
        this.minecraft = minecraft;
        this.playerName = name;
        this.skinGetter = skinProvider;
        this.uuid = uuid;
        this.screen = screen;
        final Component component = Component.translatable("gui.socialInteractions.narration.hide", name);
        this.hideTooltip = minecraft.font.split(HIDE_TEXT_TOOLTIP, 150);
        if (!minecraft.player.getUUID().equals(uuid)) {
            this.huntButton = new ImageButton(0, 0, 20, 20, 0, 38, 20, PlayerChooserScreen.TEXTURE, 256, 256, (p_100612_) -> {
                PlayerChooserScreen.used = true;
                EXPTrackerPacketHandler.CHANNEL.sendToServer(new InitHuntPacketC2S(uuid));
                minecraft.popGuiLayer();
            }, new Button.OnTooltip() {
                public void onTooltip(Button p_170109_, PoseStack p_170110_, int p_170111_, int p_170112_) {
                    CustomPlayerEntry.this.tooltipHoverTime += minecraft.getDeltaFrameTime();
                    if (CustomPlayerEntry.this.tooltipHoverTime >= 10.0F) {
                        CustomPlayerEntry.postRenderTooltip(screen, p_170110_, CustomPlayerEntry.this.hideTooltip, p_170111_, p_170112_);
                    }

                }

                public void narrateTooltip(Consumer<Component> componentConsumer) {
                    componentConsumer.accept(component);
                }
            }, Component.translatable("gui.socialInteractions.hide")) {
                protected MutableComponent createNarrationMessage() {
                    return CustomPlayerEntry.this.getEntryNarationMessage(super.createNarrationMessage());
                }
            };
            this.huntButton.visible = true;
            this.children = ImmutableList.of(this.huntButton);
        } else {
            this.children = ImmutableList.of();
        }

    }

    public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
        int i = pLeft + 4;
        int j = pTop + (pHeight - 24) / 2;
        int k = i + 24 + 4;
        Component component = this.getStatusComponent();
        int l;
        RenderSystem.setShaderTexture(0, PlayerChooserScreen.TEXTURE);
        if (component == CommonComponents.EMPTY) {
            blit(pPoseStack, pLeft, pTop, 0, 2f, 80f, pWidth, pHeight, 256, 256);
            l = pTop + (pHeight - 9) / 2;
        } else {
            blit(pPoseStack, pLeft, pTop, 0, 2f, 80f, pWidth, pHeight, 256, 256);
            l = pTop + (pHeight - (9 + 9)) / 2;
            this.minecraft.font.draw(pPoseStack, component, (float) k, (float) (l + 12), PLAYER_STATUS_COLOR);
        }

        RenderSystem.setShaderTexture(0, this.skinGetter.get());
        PlayerFaceRenderer.draw(pPoseStack, i, j, 24);
        this.minecraft.font.draw(pPoseStack, this.playerName, (float) k, (float) l, PLAYERNAME_COLOR);

        if (this.huntButton != null) {
            float f = this.tooltipHoverTime;
            this.huntButton.x = pLeft + (pWidth - this.huntButton.getWidth() - 4) - 20 - 4;
            this.huntButton.y = pTop + (pHeight - this.huntButton.getHeight()) / 2;
            this.huntButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            if (f == this.tooltipHoverTime) {
                this.tooltipHoverTime = 0.0F;
            }
        }

    }

    public List<? extends GuiEventListener> children() {
        return this.children;
    }

    public List<? extends NarratableEntry> narratables() {
        return this.children;
    }

    public String getPlayerName() {
        return this.playerName;
    }


    MutableComponent getEntryNarationMessage(MutableComponent p_100595_) {
        Component component = this.getStatusComponent();
        return component == CommonComponents.EMPTY ? Component.literal(this.playerName).append(", ").append(p_100595_) : Component.literal(this.playerName).append(", ").append(component).append(", ").append(p_100595_);
    }

    private Component getStatusComponent() {
        return Component.literal(TruncationUtils.floatTruncate(screen.players.get(uuid)) + " blocks");
    }

    static void postRenderTooltip(PlayerChooserScreen screen, PoseStack poseStack, List<FormattedCharSequence> message, int x, int y) {
        screen.renderTooltip(poseStack, message, x, y);
    }
}