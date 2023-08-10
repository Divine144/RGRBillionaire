package com.divinity.hmedia.rgrbillionaire.client.gui;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.ArrayList;
import java.util.List;

public class MoneyExplosionGuiOverlay implements IGuiOverlay {

    public static final MoneyExplosionGuiOverlay INSTANCE = new MoneyExplosionGuiOverlay();
    private static final ResourceLocation[] MONEY_EXPLOSION_FRAMES = initializeMoneyFrames();

    private long startTime = 0;
    private boolean enabled = false;

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private MoneyExplosionGuiOverlay() {}

    private static ResourceLocation[] initializeMoneyFrames() {
        List<ResourceLocation> locations = new ArrayList<>();
        for (int i = 1; i <= 150; i++) {
            ResourceLocation location = new ResourceLocation(RGRBillionaire.MODID, "textures/gui/money/%04d.png".formatted(i));
            locations.add(location);
        }
        return locations.toArray(ResourceLocation[]::new);
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (!this.enabled)
            return;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        long elapsedTime = Util.getMillis() - this.startTime;
        if (elapsedTime > 3_000L) {
            this.enabled = false;
            return;
        }

        int currentFrame = Mth.clamp((int) (elapsedTime / 20), 0, 149);
        RenderSystem.setShaderTexture(0, MONEY_EXPLOSION_FRAMES[currentFrame]);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(0.0D, screenHeight, -90.0D).uv(0.0F, 1.0F).endVertex();
        bufferbuilder.vertex(screenWidth, screenHeight, -90.0D).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(screenWidth, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
        tesselator.end();
    }
}
