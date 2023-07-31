package com.divinity.hmedia.rgrbillionaire.quest.reward;

import dev._100media.hundredmediaabilities.capability.MarkerHolderAttacher;
import dev._100media.hundredmediaabilities.marker.Marker;
import dev._100media.hundredmediaquests.reward.QuestReward;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class MarkerQuestReward implements QuestReward {

    private final Supplier<Marker> marker;

    public MarkerQuestReward(Supplier<Marker> marker) {
        this.marker = marker;
    }

    @Override
    public void applyReward(ServerLevel level, ServerPlayer player) {
        MarkerHolderAttacher.getMarkerHolder(player).ifPresent(cap -> cap.addMarker(marker.get(), true));
    }

    @Override
    public String getDescriptionId() {
        return null;
    }
}
