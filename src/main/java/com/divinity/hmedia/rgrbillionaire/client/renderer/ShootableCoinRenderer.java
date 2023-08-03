package com.divinity.hmedia.rgrbillionaire.client.renderer;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.entity.CurrencyProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev._100media.hundredmediageckolib.client.model.SimpleGeoEntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShootableCoinRenderer extends GeoEntityRenderer<CurrencyProjectileEntity> {

    public ShootableCoinRenderer(EntityRendererProvider.Context renderManager, String name) {
        super(renderManager, new SimpleGeoEntityModel<>(RGRBillionaire.MODID, name));
    }

    @Override
    public void render(CurrencyProjectileEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
