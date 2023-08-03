package com.divinity.hmedia.rgrbillionaire.quest.goal;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class AquireAdvancementGoal extends BasicQuestGoal {

    private final String advancementID;
    private final String descriptionID;

    public AquireAdvancementGoal(String advancementID, String descriptionID) {
        super(1);
        this.advancementID = advancementID;
        this.descriptionID = "quest.goal." + RGRBillionaire.MODID + "." + descriptionID;
    }

    public String getAdvancementID() {
        return this.advancementID;
    }

    @Override
    public String getDescriptionId() {
        return descriptionID;
    }
}
