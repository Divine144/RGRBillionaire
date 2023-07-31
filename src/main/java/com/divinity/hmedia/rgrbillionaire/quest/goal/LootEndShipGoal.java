package com.divinity.hmedia.rgrbillionaire.quest.goal;

import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class LootEndShipGoal extends BasicQuestGoal {

    public LootEndShipGoal(double target) {
        super(target);
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrbillionaire.loot_end_ship_goal";
    }
}
