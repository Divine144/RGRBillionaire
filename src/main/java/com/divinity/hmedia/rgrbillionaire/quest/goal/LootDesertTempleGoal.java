package com.divinity.hmedia.rgrbillionaire.quest.goal;

import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class LootDesertTempleGoal extends BasicQuestGoal {

    public LootDesertTempleGoal(double target) {
        super(target);
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrbillionaire.loot_desert_temple_goal";
    }
}
