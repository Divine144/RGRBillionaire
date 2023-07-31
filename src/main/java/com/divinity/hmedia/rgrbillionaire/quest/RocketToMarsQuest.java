package com.divinity.hmedia.rgrbillionaire.quest;

import com.divinity.hmedia.rgrbillionaire.init.ItemInit;
import com.divinity.hmedia.rgrbillionaire.quest.goal.AquireAdvancementGoal;
import com.divinity.hmedia.rgrbillionaire.quest.goal.LootEndShipGoal;
import dev._100media.hundredmediaquests.goal.KillPlayersGoal;
import dev._100media.hundredmediaquests.goal.QuestGoal;
import dev._100media.hundredmediaquests.quest.Quest;
import dev._100media.hundredmediaquests.quest.QuestType;
import dev._100media.hundredmediaquests.reward.ItemQuestReward;
import dev._100media.hundredmediaquests.reward.QuestReward;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
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
        goals.add(new AquireAdvancementGoal("trim_with_all_exclusive_armor_patterns", "quest.goal.rgrbillionaire.smithing_with_style_advancement_goal"));
        goals.add(new KillPlayersGoal(5) {
            @Override
            public boolean tallyKill(Entity entity, DamageSource source) {
                return source.is(DamageTypes.FALL) ? super.tallyKill(entity, source) : false;
            }
            @Override
            public String getDescriptionId() {
                return "quest.goal.rgrbillionaire.kill_player_fall_damage";
            }
        });
        goals.add(new LootEndShipGoal(2));
        return goals;
    }

    @Override
    protected List<QuestReward> initializeRewards() {
        List<QuestReward> rewards = new ArrayList<>();
        rewards.add(new ItemQuestReward(new ItemStack(ItemInit.ROCKET_TO_MARS.get())));
        return rewards;
    }
}
