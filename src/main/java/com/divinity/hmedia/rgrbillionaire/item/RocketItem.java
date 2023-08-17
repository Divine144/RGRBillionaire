package com.divinity.hmedia.rgrbillionaire.item;

import com.divinity.hmedia.rgrbillionaire.entity.RocketEntity;
import com.divinity.hmedia.rgrbillionaire.init.EntityInit;
import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
import dev._100media.hundredmediageckolib.item.animated.SimpleAnimatedItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class RocketItem extends SimpleAnimatedItem {
    public RocketItem(AnimatedItemProperties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        ItemStack stack = pContext.getItemInHand();
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();
        BlockPos blockpos = pContext.getClickedPos().relative(pContext.getClickedFace());
        if (pContext.getClickedFace() == Direction.UP) {
            if (player instanceof ServerPlayer serverPlayer) {
                RocketEntity entity = new RocketEntity(EntityInit.ROCKET_ENTITY.get(), serverPlayer.serverLevel());
                entity.setPos(blockpos.getX(), blockpos.getY(), blockpos.getZ());
                serverPlayer.level().addFreshEntity(entity);
                stack.shrink(1);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }
}
