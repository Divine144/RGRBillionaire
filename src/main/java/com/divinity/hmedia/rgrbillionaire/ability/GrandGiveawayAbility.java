package com.divinity.hmedia.rgrbillionaire.ability;

import com.divinity.hmedia.rgrbillionaire.entity.DollarBillEntity;
import com.divinity.hmedia.rgrbillionaire.init.EntityInit;
import com.divinity.hmedia.rgrbillionaire.init.SoundInit;
import com.divinity.hmedia.rgrbillionaire.util.BillionaireUtils;
import dev._100media.hundredmediaabilities.ability.Ability;
import dev._100media.hundredmediaabilities.capability.AbilityHolderAttacher;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;

public class GrandGiveawayAbility extends Ability {

    @Override
    public void executePressed(ServerLevel level, ServerPlayer player) {
        super.executePressed(level, player);
    }

    @Override
    public void executeHeld(ServerLevel level, ServerPlayer player, int tick) {
        super.executeHeld(level, player, tick);
        if (BillionaireUtils.hasEnoughMoney(player, 0)) {
            if (tick % 20 == 0) {
                player.level().playSound(null, player.getX(), player.getY(),  player.getZ(), SoundInit.GRAND_GIVEAWAY.get(), SoundSource.PLAYERS, 1, 1);
                BillionaireUtils.takeMoney(player, 100);
            }
            for (int x = (int) (player.getX() - 10); x < player.getX() + 10; x++) {
                for (int z = (int) player.getZ() - 10; z < player.getZ() + 10; z++) {
                    if (level.getRandom().nextIntBetweenInclusive(0, 50) == 0) {
                        var type = EntityInit.DOLLAR_COIN_ENTITY.get();
                        if (level.getRandom().nextIntBetweenInclusive(0, 10) <= 7) {
                            type = EntityInit.DOLLAR_BILL_ENTITY.get();
                        }
                        var entity = type.create(level);
                        if (entity != null) {
                            entity.setPos(x, player.getY() + 15, z);
                            entity.setOwner(player);
                            level.addFreshEntity(entity);
                        }
                    }
                }
            }
        }
        else AbilityHolderAttacher.getAbilityHolder(player).ifPresent(h -> h.removeActiveAbility(this, true));
    }

    @Override
    public boolean isToggleAbility() {
        return true;
    }
}
