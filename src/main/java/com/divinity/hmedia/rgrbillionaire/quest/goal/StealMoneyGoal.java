package com.divinity.hmedia.rgrbillionaire.quest.goal;

import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class StealMoneyGoal extends BasicQuestGoal {

    public StealMoneyGoal(double target) {
        super(target);
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrbillionaire.steal_money_goal";
    }
}
