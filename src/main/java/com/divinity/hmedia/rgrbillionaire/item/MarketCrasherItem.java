package com.divinity.hmedia.rgrbillionaire.item;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.entity.StockGraphEntity;
import com.divinity.hmedia.rgrbillionaire.init.EntityInit;
import com.divinity.hmedia.rgrbillionaire.init.SoundInit;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
import dev._100media.hundredmediageckolib.item.animated.SimpleAnimatedItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.function.Consumer;

public class MarketCrasherItem extends SimpleAnimatedItem {

    public MarketCrasherItem(AnimatedItemProperties pProperties) {
        super(pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new GeoItemRenderer<MarketCrasherItem>(new DefaultedItemGeoModel<>(new ResourceLocation(RGRBillionaire.MODID, "market_crasher"))) {
                        @Override
                        public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
                            poseStack.pushPose();
                            switch (transformType) {
                                case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                                    poseStack.scale(0.6f, 0.6f, 0.6f);
                                    poseStack.translate(0.35, 0, 0);

                                }
                                case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                                    poseStack.scale(0.6f, 0.6f, 0.6f);
                                }

                            }
                            super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
                            poseStack.popPose();
                        }

                        @Override
                        protected void renderInGui(ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
                            poseStack.pushPose();
                            poseStack.scale(0.5f, 0.5f, 0.5f);
                            poseStack.mulPose(Axis.YN.rotationDegrees(90));
                            poseStack.mulPose(Axis.XP.rotationDegrees(45));
                            poseStack.translate(4, -1, -2);
                            super.renderInGui(transformType, poseStack, bufferSource, packedLight, packedOverlay);
                            poseStack.popPose();
                        }
                    };
                return this.renderer;
            }
        });
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            return InteractionResultHolder.pass(itemStack);
        }
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);
        if (pLevel.isClientSide || !(pLivingEntity instanceof Player player)) {
            return;
        }
        if (pRemainingUseDuration % 20 == 0) {
            StockGraphEntity entity = new StockGraphEntity(EntityInit.STOCK_GRAPH_ENTITY.get(), pLevel);
            entity.setPos(player.getX(), player.getEyeY() - 0.40, player.getZ());
            entity.setOwner(player);
            entity.setNoGravity(true);
            entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 0.65F, 0);
            entity.setYRot(-Mth.wrapDegrees(player.getYRot()));
            entity.setXRot(-Mth.wrapDegrees(player.getXRot()));
            entity.xRotO = -Mth.wrapDegrees(player.xRotO);
            entity.yRotO = -Mth.wrapDegrees(player.yRotO);
            player.level().addFreshEntity(entity);
            player.getCooldowns().addCooldown(this, 20);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundInit.MARKET_CRASHER.get(), SoundSource.PLAYERS, 0.3f, 1);

        }
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }
}
