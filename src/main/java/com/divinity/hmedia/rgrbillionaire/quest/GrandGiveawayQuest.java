package com.divinity.hmedia.rgrbillionaire.quest;

import com.divinity.hmedia.rgrbillionaire.init.AbilityInit;
import com.divinity.hmedia.rgrbillionaire.quest.goal.AquireAdvancementGoal;
import com.divinity.hmedia.rgrbillionaire.quest.goal.StealMoneyGoal;
import com.divinity.hmedia.rgrbillionaire.quest.reward.AbilityQuestReward;
import dev._100media.hundredmediaquests.goal.HarvestBlocksGoal;
import dev._100media.hundredmediaquests.goal.QuestGoal;
import dev._100media.hundredmediaquests.quest.Quest;
import dev._100media.hundredmediaquests.quest.QuestType;
import dev._100media.hundredmediaquests.reward.QuestReward;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class GrandGiveawayQuest extends Quest {

    public GrandGiveawayQuest(QuestType<?> type) {
        super(type);
    }

    @Override
    protected List<QuestGoal> initializeGoals() {
        List<QuestGoal> goals = new ArrayList<>();
        goals.add(new AquireAdvancementGoal("feed_snifflet", "little_sniffs_advancement_goal"));
        goals.add(new HarvestBlocksGoal(2, Blocks.SPAWNER) {
            @Override
            public String getDescriptionId() {
                return "quest.goal.rgrbillionaire.harvest_spawner";
            }
        });
        goals.add(new StealMoneyGoal(2500));
        return goals;
    }

    @Override
    protected List<QuestReward> initializeRewards() {
        List<QuestReward> rewards = new ArrayList<>();
        rewards.add(new AbilityQuestReward(AbilityInit.GRAND_GIVEAWAY_ABILITY));
        return rewards;
    }
}
