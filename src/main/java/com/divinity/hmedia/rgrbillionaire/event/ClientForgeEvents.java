package com.divinity.hmedia.rgrbillionaire.event;

import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.init.MarkerInit;
import com.divinity.hmedia.rgrbillionaire.init.MenuInit;
import com.divinity.hmedia.rgrbillionaire.init.MorphInit;
import com.divinity.hmedia.rgrbillionaire.network.ClientHandler;
import com.divinity.hmedia.rgrbillionaire.util.BillionaireUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import dev._100media.hundredmediaabilities.capability.MarkerHolderAttacher;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import dev._100media.hundredmediaquests.network.HMQNetworkHandler;
import dev._100media.hundredmediaquests.network.packet.OpenMainTreePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
                        ClientHandler.startMoneyExplosionAnimation();
                    }
                }
            });
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
                MarkerHolderAttacher.getMarkerHolder(player).ifPresent(m -> {
                    if (m.hasMarker(MarkerInit.BILLIONAIRES_CLUB.get())) {
                        event.getGuiGraphics().drawString(font, text, x - (font.width(text) / 2), y - 4, 0x00FF00);
                        event.getGuiGraphics().drawString(font, billionairesClubText, x - (font.width(billionairesClubText) / 2), y + 8, 0x399A9C);
                    }
                    else {
                        event.getGuiGraphics().drawString(font, text, x - (font.width(text) / 2), y + 8, 0x00FF00);
                    }
                });
                poseStack.popPose();
            }
        });
    }
}
