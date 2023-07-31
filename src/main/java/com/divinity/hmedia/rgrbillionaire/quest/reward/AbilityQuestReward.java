package com.divinity.hmedia.rgrbillionaire.quest.reward;

import com.divinity.hmedia.rgrbillionaire.init.SkillInit;
import dev._100media.hundredmediaabilities.ability.Ability;
import dev._100media.hundredmediaquests.reward.QuestReward;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class AbilityQuestReward implements QuestReward {

    private final Supplier<Ability> ability;

    public AbilityQuestReward(Supplier<Ability> ability) {
        this.ability = ability;
    }

    @Override
    public void applyReward(ServerLevel level, ServerPlayer player) {
        SkillInit.unlockAbility(player, ability.get());
    }

    @Override
    public String getDescriptionId() {
        return null;
    }
}
