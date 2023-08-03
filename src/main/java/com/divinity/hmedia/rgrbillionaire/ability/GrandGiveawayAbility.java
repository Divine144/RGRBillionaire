package com.divinity.hmedia.rgrbillionaire.ability;

import com.divinity.hmedia.rgrbillionaire.init.EntityInit;
import com.divinity.hmedia.rgrbillionaire.util.BillionaireUtils;
import dev._100media.hundredmediaabilities.ability.Ability;
import dev._100media.hundredmediaabilities.capability.AbilityHolderAttacher;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class GrandGiveawayAbility extends Ability {

    @Override
    public void executePressed(ServerLevel level, ServerPlayer player) {
        super.executePressed(level, player);
    }

    @Override
    public void executeHeld(ServerLevel level, ServerPlayer player, int tick) {
        super.executeHeld(level, player, tick);
        // TODO: Change Placeholder amounts
        if (BillionaireUtils.hasEnoughMoney(player, 0)) {
            if (tick % 20 == 0) {
                BillionaireUtils.takeMoney(player, 0);
            }
            for (int x = (int) (player.getX() - 10); x < player.getX() + 10; x++) {
                for (int z = (int) player.getZ() - 10; z < player.getZ() + 10; z++) {
                    if (level.getRandom().nextIntBetweenInclusive(0, 50) == 0) {
                        // TODO: Set 80% chance to spawn Dollar bills and 20% chance to spawn coin
                        var type = EntityInit.PENNY_ENTITY.get();
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
