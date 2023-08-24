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

public class ConfettiGuiOverlay implements IGuiOverlay {

    public static final ConfettiGuiOverlay INSTANCE = new ConfettiGuiOverlay();
    private static final ResourceLocation[] CONFETTI_FRAMES = initializeConfettiFrames();

    private long startTime = 0;
    private boolean enabled = false;

    private int loopTimes = 0;

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    private ConfettiGuiOverlay() {}

    private static ResourceLocation[] initializeConfettiFrames() {
        List<ResourceLocation> locations = new ArrayList<>();
        for (int i = 1; i <= 300; i++) {
            ResourceLocation location = new ResourceLocation(RGRBillionaire.MODID, "textures/gui/confetti/%04d.png".formatted(i));
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
        System.out.println(loopTimes);
        if (elapsedTime > 6_000L) {
            if (++loopTimes < 5) {
                setStartTime(Util.getMillis());
            }
            else {
                this.enabled = false;
                loopTimes = 0;
                return;
            }
        }

        int currentFrame = Mth.clamp((int) (elapsedTime / 20), 0, 299);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, ((float) (299 - currentFrame) / 299));
        RenderSystem.setShaderTexture(0, CONFETTI_FRAMES[currentFrame]);
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