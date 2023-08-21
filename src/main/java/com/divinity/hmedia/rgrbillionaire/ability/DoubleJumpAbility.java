package com.divinity.hmedia.rgrbillionaire.ability;


import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import dev._100media.hundredmediaabilities.ability.Ability;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class DoubleJumpAbility extends Ability {

    @Override
    public void executePressed(ServerLevel level, ServerPlayer player) {
        BillionaireHolderAttacher.getHolder(player).ifPresent(p -> {
            if (p.isCanDoubleJump() && !player.onGround()) {
                player.hurtMarked = true;
                player.jumpFromGround();
                level.sendParticles(ParticleTypes.POOF, player.getX(), player.getY() - 1, player.getZ(), 4, 0, 0, 0, 0.1);
                p.setCanDoubleJump(false);
            }
        });
        super.executePressed(level, player);
    }



    @Override
    public boolean isHiddenAbility() {
        return true;
    }
}
