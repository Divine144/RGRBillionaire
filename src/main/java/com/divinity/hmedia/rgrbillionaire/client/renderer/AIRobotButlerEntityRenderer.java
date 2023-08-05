package com.divinity.hmedia.rgrbillionaire.client.renderer;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.entity.AIRoboButlerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev._100media.hundredmediageckolib.client.model.SimpleGeoEntityModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AIRobotButlerEntityRenderer extends GeoEntityRenderer<AIRoboButlerEntity> {

    private AIRoboButlerEntity currentRenderingEntity;
    private RenderType currentRenderType;

    public AIRobotButlerEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SimpleGeoEntityModel<>(RGRBillionaire.MODID, "butler_entity"));
    }

    @Override
    public void render(AIRoboButlerEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        currentRenderingEntity = entity;
        RenderType renderType = getRenderType(entity, getTextureLocation(entity), bufferSource, partialTick);
        this.currentRenderType = renderType;
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        currentRenderType = null;
    }

    @Override
    public void renderRecursively(PoseStack stack, AIRoboButlerEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if ("hand".equals(bone.getName()) && !animatable.isSitting()) {
            stack.pushPose();
            moveAndRotateMatrixToMatchBone(stack, bone);
            stack.mulPose(Axis.XP.rotationDegrees(-90));
            stack.mulPose(Axis.YN.rotationDegrees(180));
            stack.mulPose(Axis.ZN.rotationDegrees(90));
            Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(currentRenderingEntity, currentRenderingEntity.getTool(),
                    ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, stack, bufferSource,
                    packedLight);
            stack.popPose();
            buffer = bufferSource.getBuffer(currentRenderType);
        }
        super.renderRecursively(stack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    protected void moveAndRotateMatrixToMatchBone(PoseStack stack, GeoBone bone) {
        stack.translate(bone.getPivotX() / 16, bone.getPivotY() / 16, bone.getPivotZ() / 16);
        float xRot = bone.getRotX() * (180 / (float) Math.PI);
        float yRot = bone.getRotY() * (180 / (float) Math.PI);
        float zRot = bone.getRotZ() * (180 / (float) Math.PI);
        stack.mulPose(Axis.XP.rotationDegrees(xRot));
        stack.mulPose(Axis.YP.rotationDegrees(yRot));
        stack.mulPose(Axis.ZP.rotationDegrees(zRot));
    }
}
