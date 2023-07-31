package com.divinity.hmedia.rgrbillionaire.requirement;

import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolder;
import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import dev._100media.hundredmediaquests.skill.requirements.SkillRequirement;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class MoneySkillRequirement implements SkillRequirement {

    private final int cost;

    public MoneySkillRequirement(int amount) {
        this.cost = amount;
    }

    @Override
    public boolean hasRequirement(Player player) {
        return BillionaireHolderAttacher.getHolder(player).filter(h -> h.getMoney() >= cost).isPresent();
    }

    @Override
    public void consumeRequirement(ServerPlayer player) {
        BillionaireHolderAttacher.getHolder(player).ifPresent(h -> h.setMoney(h.getMoney() - cost));
    }

    @Override
    public MutableComponent getFancyDescription(Player player) {
        int current = BillionaireHolderAttacher.getHolder(player).map(BillionaireHolder::getMoney).orElse(0);
        return Component.literal("$%s/$%s".formatted(current, cost));
    }
}
