package com.divinity.hmedia.rgrbillionaire.quest;

import com.divinity.hmedia.rgrbillionaire.init.ItemInit;
import com.divinity.hmedia.rgrbillionaire.quest.goal.AquireAdvancementGoal;
import com.divinity.hmedia.rgrbillionaire.quest.goal.LootEndShipGoal;
import com.divinity.hmedia.rgrbillionaire.util.BillionaireUtils;
import dev._100media.hundredmediaquests.goal.KillPlayersGoal;
import dev._100media.hundredmediaquests.goal.QuestGoal;
import dev._100media.hundredmediaquests.quest.Quest;
import dev._100media.hundredmediaquests.quest.QuestType;
import dev._100media.hundredmediaquests.reward.ItemQuestReward;
import dev._100media.hundredmediaquests.reward.QuestReward;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RocketToMarsQuest extends Quest {

    public RocketToMarsQuest(QuestType<?> type) {
        super(type);
    }

    @Override
    protected List<QuestGoal> initializeGoals() {
        List<QuestGoal> goals = new ArrayList<>();
        goals.add(new AquireAdvancementGoal("cure_zombie_villager", "cure_zombie_villager_advancement_goal"));
        goals.add(new KillPlayersGoal(5) {
            @Override
            public boolean tallyKill(Entity entity, DamageSource source) {
                return entity instanceof Player player && player.getLastAttacker() instanceof Player killer && BillionaireUtils.hasAnyMorph(killer) &&
                source.is(DamageTypes.FALL) && super.tallyKill(entity, source);
            }
            @Override
            public String getDescriptionId() {
                return "quest.goal.rgrbillionaire.kill_player_fall_damage";
            }
        });
        goals.add(new LootEndShipGoal(1));
        return goals;
    }

    @Override
    protected List<QuestReward> initializeRewards() {
        List<QuestReward> rewards = new ArrayList<>();
        rewards.add(new ItemQuestReward(new ItemStack(ItemInit.ROCKET_TO_MARS.get())));
        return rewards;
    }
}
