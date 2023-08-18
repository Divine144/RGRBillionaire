package com.divinity.hmedia.rgrbillionaire.item;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.cap.CannonHolder;
import com.divinity.hmedia.rgrbillionaire.cap.CannonHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.entity.CurrencyProjectileEntity;
import com.divinity.hmedia.rgrbillionaire.init.EntityInit;
import com.divinity.hmedia.rgrbillionaire.init.ItemInit;
import com.divinity.hmedia.rgrbillionaire.util.BillionaireUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
import dev._100media.hundredmediageckolib.item.animated.SimpleAnimatedItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.function.Consumer;

public class CoinCannonItem extends SimpleAnimatedItem {

    public CoinCannonItem(AnimatedItemProperties pProperties) {
        super(pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new GeoItemRenderer<BatonItem>(new DefaultedItemGeoModel<>(new ResourceLocation(RGRBillionaire.MODID, "the_coin_cannon"))) {
                        @Override
                        public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
                            poseStack.pushPose();
                            switch (transformType) {
                                case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                                    poseStack.mulPose(Axis.YP.rotationDegrees(-90));
                                    poseStack.translate(0.1, -0.05, -0.95);

                                }
                                case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                                    poseStack.scale(0.8f, 0.8f, 0.8f);
                                    poseStack.mulPose(Axis.YP.rotationDegrees(-90));
                                    poseStack.translate(0, 0, -1.2);
                                }

                            }
                            super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
                            poseStack.popPose();
                        }

                        @Override
                        protected void renderInGui(ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
                            poseStack.pushPose();
                            poseStack.scale(1.2f, 1.2f, 1.2f);
                            poseStack.translate(0.02, -0.5, -0.5);
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
        if (pPlayer.isShiftKeyDown()) {
            CannonHolderAttacher.getItemStackCapability(itemStack).ifPresent(h -> {
                h.cycleSelection();
                pPlayer.sendSystemMessage(Component.literal("Currency: " + this.getNameForEntityType(this.getAmmoForCycle(h.getCycle())))
                        .withStyle(ChatFormatting.DARK_RED));
            });
            return InteractionResultHolder.consume(itemStack);
        }
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);
        if (pLevel.isClientSide || !(pLivingEntity instanceof Player player)) {
            return;
        }
        if (pRemainingUseDuration % 2 == 0) {
            int cycle = CannonHolderAttacher.getItemStackCapability(pStack).map(CannonHolder::getCycle).orElse(0);
            var type = this.getAmmoForCycle(cycle);
            if (BillionaireUtils.hasEnoughMoney(player,  this.getCostForAmmo(type))) {
                var entity = type.create(pLevel);
                if (entity != null) {
                    entity.setPos(player.getX(), player.getEyeY() - 0.15, player.getZ());
                    entity.setOwner(player);
                    entity.setNoGravity(true);
                    entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 0);
                    player.level().addFreshEntity(entity);
                    // TODO: PUT SOUND HERE
                }
                BillionaireHolderAttacher.getHolder(player).ifPresent(h -> h.addMoney(-this.getCostForAmmo(type)));
                switch (cycle) {
                    case 1 -> {
                        player.getCooldowns().addCooldown(this, 60);
                        player.stopUsingItem();
                    }
                    case 2 -> {
                        player.getCooldowns().addCooldown(this, 100);
                        player.stopUsingItem();
                    }
                }
            }
            else player.displayClientMessage(Component.literal("Not Enough Money").withStyle(ChatFormatting.RED), true);
        }
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    public EntityType<CurrencyProjectileEntity> getAmmoForCycle(int cycle) {
        return switch (cycle) {
            case 1 -> EntityInit.QUARTER_ENTITY.get();
            case 2 -> EntityInit.SILVER_DOLLAR_ENTITY.get();
            default -> EntityInit.PENNY_ENTITY.get();
        };
    }

    public int getCostForAmmo(EntityType<CurrencyProjectileEntity> type) {
        if (type == EntityInit.PENNY_ENTITY.get()) {
            return 1;
        }
        else if (type == EntityInit.QUARTER_ENTITY.get()) {
            return 5;
        }
        return 10;
    }

    public String getNameForEntityType(EntityType<CurrencyProjectileEntity> entity) {
        if (entity == EntityInit.PENNY_ENTITY.get()) {
            return "Penny";
        }
        else if (entity == EntityInit.QUARTER_ENTITY.get()) {
            return "Quarter";
        }
        else return "Silver Dollar";
    }
}
