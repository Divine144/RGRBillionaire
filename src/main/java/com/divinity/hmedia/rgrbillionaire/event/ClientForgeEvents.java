package com.divinity.hmedia.rgrbillionaire.event;

import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.cap.GlobalLevelHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.init.MarkerInit;
import com.divinity.hmedia.rgrbillionaire.init.MenuInit;
import com.divinity.hmedia.rgrbillionaire.init.MorphInit;
import com.divinity.hmedia.rgrbillionaire.init.SoundInit;
import com.divinity.hmedia.rgrbillionaire.network.ClientHandler;
import com.divinity.hmedia.rgrbillionaire.network.NetworkHandler;
import com.divinity.hmedia.rgrbillionaire.network.serverbound.MugOfCoffeePacket;
import com.divinity.hmedia.rgrbillionaire.network.serverbound.RecallButlerPacket;
import com.divinity.hmedia.rgrbillionaire.util.BillionaireUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import dev._100media.hundredmediaabilities.capability.MarkerHolderAttacher;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import dev._100media.hundredmediaquests.network.HMQNetworkHandler;
import dev._100media.hundredmediaquests.network.packet.OpenMainTreePacket;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientForgeEvents {

    private static final ResourceLocation FOOD_OVERLAY = new ResourceLocation("food_level");

    @SubscribeEvent
    public static void keyPressEvent(InputEvent.Key event) {
        if (ClientModEvents.SKILL_TREE_KEY.isDown()) {
            HMQNetworkHandler.INSTANCE.sendToServer(new OpenMainTreePacket(MenuInit.SKILL_TREE.get()));
        }
        Minecraft instance = Minecraft.getInstance();
        LocalPlayer player = instance.player;
        if (instance != null && player != null) {
            MarkerHolderAttacher.getMarkerHolder(player).ifPresent(m -> {
                if (m.hasMarker(MarkerInit.BILLIONAIRES_CLUB.get())) {
                    if (ClientModEvents.MONEY_EXPLOSION_KEY.isDown()) {
                        var soundList = List.of(SoundInit.RICH_LOW.get(), SoundInit.RICH_LITTLE_LOW.get(), SoundInit.RICH_NORMAL.get(), SoundInit.RICH_HIGH.get());
                        SoundEvent sound = Util.getRandom(soundList, player.getRandom());
                        ClientHandler.startMoneyExplosionAnimation();
                        player.level().playLocalSound(player.blockPosition(), sound, SoundSource.PLAYERS, 0.3f, 1f, false);
                    }
                }
            });
        }
        if (event.getKey() == GLFW.GLFW_KEY_SPACE && event.getAction() == GLFW.GLFW_PRESS) {
            if (player != null) {
                var holder = MarkerHolderAttacher.getMarkerHolderUnwrap(player);
                if (holder != null) {
                    if (holder.hasMarker(MarkerInit.MUG_OF_COFFEE.get()) && !player.onGround()) {
                        NetworkHandler.INSTANCE.sendToServer(new MugOfCoffeePacket());
                    }
                }
            }
        }
        if (ClientModEvents.BUTLER_RECALL_KEY.isDown()) {
            NetworkHandler.INSTANCE.sendToServer(new RecallButlerPacket());
        }
    }

    @SubscribeEvent
    public static void onGuiRender(RenderGuiOverlayEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        if (!event.getOverlay().id().equals(FOOD_OVERLAY) || player == null)
            return;
        BillionaireHolderAttacher.getHolder(player).ifPresent(holder -> {
            if (BillionaireUtils.hasAnyMorph(player)) {
                var font = Minecraft.getInstance().font;
                String text = "$%s/$%s".formatted(holder.getMoney(), holder.getMoneyCap());
                String billionairesClubText = "!! Official Member of the Billionaire’s Club !!";
                PoseStack poseStack = event.getGuiGraphics().pose();
                poseStack.pushPose();
                int x = event.getWindow().getGuiScaledWidth() / 2;
                int y = event.getWindow().getGuiScaledHeight() / 2 + 70;
                var globalHolder = GlobalLevelHolderAttacher.getGlobalLevelCapabilityUnwrap(player.level());
                if (globalHolder != null) {
                    if (globalHolder.getMoneyBarX() > 0) {
                        x = globalHolder.getMoneyBarX();
                    }
                    if (globalHolder.getMoneyBarY() > 0) {
                        y = globalHolder.getMoneyBarY();
                    }
                }
                int finalX = x;
                int finalY = y;
                MarkerHolderAttacher.getMarkerHolder(player).ifPresent(m -> {
                    if (m.hasMarker(MarkerInit.BILLIONAIRES_CLUB.get())) {
                        event.getGuiGraphics().drawString(font, text, finalX - (font.width(text) / 2), finalY - 4, 0x00FF00);
                        event.getGuiGraphics().drawString(font, billionairesClubText, finalX - (font.width(billionairesClubText) / 2), finalY + 8, 0x399A9C);
                    }
                    else {
                        event.getGuiGraphics().drawString(font, text, finalX - (font.width(text) / 2), finalY + 8, 0x00FF00);
                    }
                });
                poseStack.popPose();
            }
        });
    }
}
