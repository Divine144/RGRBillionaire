package com.divinity.hmedia.rgrbillionaire.client.renderer;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.entity.StockGraphEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev._100media.hundredmediageckolib.client.model.SimpleGeoEntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StockGraphEntityRenderer extends GeoEntityRenderer<StockGraphEntity> {

    private float ryRotO = -1;
    private float rxRotO =-1;
    private float yRot =-1;
    private float xRot =-1;

    private StockGraphEntity storedEntity;

    public StockGraphEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SimpleGeoEntityModel<>(RGRBillionaire.MODID, "stock_graph_entity"));
    }

    @Override
    public void preRender(PoseStack pMatrixStack, StockGraphEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(pMatrixStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        if (entity.getOwner() != null) {
            Entity owner = entity.getOwner();
            if (storedEntity != entity) {
                ryRotO = owner.yRotO;
                rxRotO = owner.xRotO;
                yRot = owner.getYRot();
                xRot = owner.getXRot();
                storedEntity = entity;
            }
        }
    }

    @Override
    public void render(StockGraphEntity entity, float entityYaw, float partialTick, PoseStack pMatrixStack, MultiBufferSource bufferSource, int packedLight) {
        pMatrixStack.pushPose();
        pMatrixStack.mulPose(Axis.YN.rotationDegrees(Mth.lerp(partialTick, ryRotO, yRot) - 90.0F));
        pMatrixStack.mulPose(Axis.ZN.rotationDegrees(Mth.lerp(partialTick, rxRotO, xRot)));
        pMatrixStack.mulPose(Axis.YP.rotationDegrees(-90));
        super.render(entity, entityYaw, partialTick, pMatrixStack, bufferSource, packedLight);
        pMatrixStack.popPose();
    }
}
