package com.divinity.hmedia.rgrbillionaire.requirement;

import dev._100media.hundredmediaquests.skill.requirements.SkillRequirement;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class ItemSkillRequirementSpecial implements SkillRequirement {

    private final Supplier<ItemStack> itemSupplier;
    private String name = "";

    public ItemSkillRequirementSpecial(Supplier<ItemStack> itemSupplier) {
        this.itemSupplier = itemSupplier;
    }

    public ItemSkillRequirementSpecial(Supplier<Item> itemSupplier, int amount) {
        this(() -> new ItemStack(itemSupplier.get(), amount));
    }

    public ItemSkillRequirementSpecial(Supplier<Item> itemSupplier, int amount, String name) {
        this(() -> new ItemStack(itemSupplier.get(), amount));
        this.name = name;
    }

    @Override
    public boolean hasRequirement(Player player) {
        ItemStack item = itemSupplier.get();
        return calculateProgress(player) >= item.getCount();
    }

    @Override
    public void consumeRequirement(ServerPlayer player) {
        Inventory inventory = player.getInventory();
        int found = 0;
        ItemStack item = itemSupplier.get();

        for (ItemStack itemStack : inventory.items) {
            if (itemStack == null || itemStack == ItemStack.EMPTY)
                continue;

            if (item.getItem() != itemStack.getItem())
                continue;

            if (itemStack.getCount() > (item.getCount() - found)) {
                itemStack.shrink(item.getCount() - found);
                found = item.getCount();
            } else {
                found += itemStack.getCount();
                itemStack.setCount(0);
            }

            if (found >= item.getCount())
                return;
        }
    }

    @Override
    public MutableComponent getFancyDescription(Player player) {
        ItemStack item = itemSupplier.get();
        return Component.literal(calculateProgress(player) + "/" + item.getCount() + " ").append(name.isEmpty() ? item.getDisplayName() : Component.literal(name).withStyle(ChatFormatting.WHITE));
    }

    private int calculateProgress(Player player) {
        Inventory inventory = player.getInventory();
        int found = 0;
        ItemStack item = itemSupplier.get();

        for (ItemStack itemStack : inventory.items) {
            if (itemStack == null || itemStack == ItemStack.EMPTY)
                continue;

            if (item.getItem() != itemStack.getItem())
                continue;

            found += itemStack.getCount();
            if (found >= item.getCount())
                return item.getCount();
        }
        return found;
    }


}
