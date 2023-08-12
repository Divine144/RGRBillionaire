package com.divinity.hmedia.rgrbillionaire.item;

import com.divinity.hmedia.rgrbillionaire.init.EntityInit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;

public class MarketCrasherItem extends Item {

    public MarketCrasherItem(Properties pProperties) {
        super(pProperties);
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
            var type = EntityInit.STOCK_GRAPH_ENTITY.get();
            var entity = type.create(pLevel);
            if (entity != null) {
                entity.setPos(player.getX(), player.getEyeY() - 0.40, player.getZ());
                entity.setOwner(player);
                entity.setNoGravity(true);
                entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 0.65F, 0);
                player.level().addFreshEntity(entity);
                player.getCooldowns().addCooldown(this, 20);
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }
}
