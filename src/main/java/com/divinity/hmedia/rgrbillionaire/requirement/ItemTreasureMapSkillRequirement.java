package com.divinity.hmedia.rgrbillionaire.requirement;

import dev._100media.hundredmediaquests.skill.requirements.SkillRequirement;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;

public record ItemTreasureMapSkillRequirement(int count) implements SkillRequirement {

    @Override
    public boolean hasRequirement(Player player) {
        return calculateProgress(player) >= count;
    }

    @Override
    public void consumeRequirement(ServerPlayer player) {
        Inventory inventory = player.getInventory();
        int found = 0;
        for (ItemStack itemStack : inventory.items) {
            if (itemStack.getItem() instanceof MapItem) {
                var data = MapItem.getSavedData(itemStack, player.level());
                if (data != null) {
                    if (data.isExplorationMap()) {
                        itemStack.shrink(1);
                        if (++found >= count) {
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public MutableComponent getFancyDescription(Player player) {
        return Component.literal(calculateProgress(player) + "/" + count + " ").append(Component.literal("[Treasure Map]").withStyle(ChatFormatting.YELLOW));
    }

    private int calculateProgress(Player player) {
        Inventory inventory = player.getInventory();
        int found = 0;
        for (ItemStack itemStack : inventory.items) {
            if (itemStack.getItem() instanceof MapItem) {
                var data = MapItem.getSavedData(itemStack, player.level());
                if (data != null) {
                    if (data.isExplorationMap()) {
                        found += 1;
                    }
                }
            }
        }
        return found;
    }
}
