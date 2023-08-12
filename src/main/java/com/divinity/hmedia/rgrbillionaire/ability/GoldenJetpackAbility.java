package com.divinity.hmedia.rgrbillionaire.ability;

import com.divinity.hmedia.rgrbillionaire.util.BillionaireUtils;
import dev._100media.hundredmediaabilities.ability.Ability;
import dev._100media.hundredmediaabilities.capability.AbilityHolderAttacher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.BaseFireBlock;

public class GoldenJetpackAbility extends Ability {

    private float tick = 0;

    @Override
    public void executePressed(ServerLevel level, ServerPlayer player) {
        if (tick == 0) {
            super.executePressed(level, player);
        }
    }

    @Override
    public void executeToggle(ServerLevel level, ServerPlayer player, boolean toggledOn) {
        super.executeToggle(level, player, toggledOn);
        if (toggledOn) {
            int radius = 8;
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            for (int x = (int) (player.getX() - radius); x < player.getX() + radius; x++) {
                for (int y = (int) (player.getY() - radius); y < player.getY() + radius; y++) {
                    for (int z = (int) player.getZ() - radius; z < player.getZ() + radius; z++) {
                        if (level.getRandom().nextIntBetweenInclusive(0, 2) == 0) {
                            pos.set(x, y, z);
                            if (!level.getBlockState(pos).isAir()) {
                                BlockPos blockPos = pos.relative(Direction.getRandom(player.getRandom()));
                                if (BaseFireBlock.canBePlacedAt(level, blockPos, Direction.NORTH)) {
                                    var fireState = BaseFireBlock.getState(level, blockPos);
                                    level.setBlock(blockPos, fireState, 11);
                                }
                            }
                        }
                    }
                }
            }
            BillionaireUtils.getEntitiesInRange(player, LivingEntity.class, radius, radius, radius, p -> p != player)
                .forEach(livingEntity -> {
                    double d0 = player.getX() - livingEntity.getX();
                    double d1;
                    for(d1 = player.getZ() - livingEntity.getZ(); d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
                        d0 = (Math.random() - Math.random()) * 0.01D;
                    }
                    livingEntity.knockback(6F, d0, d1);
                    livingEntity.setSecondsOnFire(6);
                    livingEntity.setLastHurtByPlayer(player);
                });
        }
        else {
            AbilityHolderAttacher.getAbilityHolder(player).ifPresent(h -> h.addCooldown(this, true));
        }
    }

    @Override
    public void executeHeld(ServerLevel level, ServerPlayer player, int tick) {
        super.executeHeld(level, player, tick);
        this.tick += 1;
        BillionaireUtils.createHelix(ParticleTypes.FLAME, player, level, 0.5f + (this.tick / 2), 0.005f);
        if (this.tick > 18) {
            super.executePressed(level, player);
            this.tick = 0;
        }
    }

    @Override
    public int getCooldownDuration() {
        return 20 * 8;
    }

    @Override
    public boolean isToggleAbility() {
        return true;
    }

    @Override
    public boolean isHiddenAbility() {
        return true;
    }
}
