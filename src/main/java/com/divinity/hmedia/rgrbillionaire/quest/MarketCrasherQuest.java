package com.divinity.hmedia.rgrbillionaire.quest;

import com.divinity.hmedia.rgrbillionaire.entity.CurrencyProjectileEntity;
import com.divinity.hmedia.rgrbillionaire.init.ItemInit;
import com.divinity.hmedia.rgrbillionaire.quest.goal.AquireAdvancementGoal;
import dev._100media.hundredmediaquests.goal.KillPlayersGoal;
import dev._100media.hundredmediaquests.goal.KillSpecificTypeGoal;
import dev._100media.hundredmediaquests.goal.QuestGoal;
import dev._100media.hundredmediaquests.quest.Quest;
import dev._100media.hundredmediaquests.quest.QuestType;
import dev._100media.hundredmediaquests.reward.ItemQuestReward;
import dev._100media.hundredmediaquests.reward.QuestReward;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MarketCrasherQuest extends Quest {

    public MarketCrasherQuest(QuestType<?> type) {
        super(type);
    }

    @Override
    protected List<QuestGoal> initializeGoals() {
        List<QuestGoal> goals = new ArrayList<>();
        goals.add(new AquireAdvancementGoal("walk_on_powder_snow_with_leather_boots", "light_as_a_rabbit_advancement_goal"));
        goals.add(new KillSpecificTypeGoal(1, EntityType.WARDEN));
        goals.add(new KillPlayersGoal(4) {
            @Override
            public boolean tallyKill(Entity entity, DamageSource source) {
                return source.getDirectEntity() instanceof CurrencyProjectileEntity projectile && projectile.getDeltaMovement().x == 0 && super.tallyKill(entity, source);
            }

            @Override
            public String getDescriptionId() {
                return "quest.goal.rgrbillionaire.kill_player_grand_giveaway";
            }
        });
        return goals;
    }

    @Override
    protected List<QuestReward> initializeRewards() {
        List<QuestReward> rewards = new ArrayList<>();
        rewards.add(new ItemQuestReward(new ItemStack(ItemInit.MARKET_CRASHER.get())));
        return rewards;
    }
}
