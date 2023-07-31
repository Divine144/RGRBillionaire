package com.divinity.hmedia.rgrbillionaire.event;

import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientForgeEvents {

    private static final ResourceLocation FOOD_OVERLAY = new ResourceLocation("food_level");

    @SubscribeEvent
    public static void onGuiRender(RenderGuiOverlayEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        if (!event.getOverlay().id().equals(FOOD_OVERLAY) || player == null)
            return;
        BillionaireHolderAttacher.getHolder(player).ifPresent(holder -> {
            // TODO: Check if holder has morph then render
            PoseStack poseStack = event.getGuiGraphics().pose();
            poseStack.pushPose();
            int x = event.getWindow().getGuiScaledWidth() / 2 - 19;
            int y = event.getWindow().getGuiScaledHeight() - 39 - 13;
            event.getGuiGraphics().drawString(Minecraft.getInstance().font, "$%s/$%s".formatted(holder.getMoney(), holder.getMoneyCap()), x, y, 0x00FF00);
            poseStack.popPose();
        });
    }
}
