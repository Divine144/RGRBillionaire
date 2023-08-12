package com.divinity.hmedia.rgrbillionaire.client;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class CustomRenderTypes extends RenderType {

    private CustomRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    private static final ResourceLocation POWER_LOCATION = new ResourceLocation(RGRBillionaire.MODID,"textures/misc/enchanted_glint_item.png");

    public static RenderType ENERGY_SWIRL_ITEM = buildEnergySwirlRenderType();

    private static RenderType buildEnergySwirlRenderType() {
        return RenderType.create("energy_swirl_item", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false,
                RenderType.CompositeState.builder()
                        .setShaderState(RENDERTYPE_GLINT_DIRECT_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(POWER_LOCATION, true, false))
                        .setTexturingState(CustomRenderStateShards.GLINT_TEXTURING_SPECIAL)
                        .setTransparencyState(GLINT_TRANSPARENCY)
                        .setDepthTestState(EQUAL_DEPTH_TEST)
                        .setCullState(NO_CULL)
                        .setOverlayState(OVERLAY)
                        .createCompositeState(false));
    }

    private static class CustomRenderStateShards {
        protected static  RenderStateShard.TexturingStateShard GLINT_TEXTURING_SPECIAL = new RenderStateShard.TexturingStateShard("glint_texturing_special", () -> {
            setupGlintTexturingSpecial(8.0F);
        }, RenderSystem::resetTextureMatrix);

        private static void setupGlintTexturingSpecial(float pScale) {
            long i = (long)((double) Util.getMillis() * Minecraft.getInstance().options.glintSpeed().get() * 8.0D);
            float f = 0.9f; // Set this to 0.9, otherwise it will constantly move the texture position up, and it will look weird
            float f1 = (float)(i % 30000L) / 30000.0F;
            Matrix4f matrix4f = (new Matrix4f()).translation(-f, f1, 0F);
            matrix4f.rotateZ(0.17453292F).scale(pScale);
            RenderSystem.setTextureMatrix(matrix4f);
        }
    }
}
