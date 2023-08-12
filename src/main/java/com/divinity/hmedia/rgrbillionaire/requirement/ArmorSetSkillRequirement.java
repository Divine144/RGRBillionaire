package com.divinity.hmedia.rgrbillionaire.requirement;

import dev._100media.hundredmediaquests.skill.requirements.SkillRequirement;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class ArmorSetSkillRequirement implements SkillRequirement {

    private final NonNullList<ItemStack> armorList = NonNullList.create();
    private final ArmorMaterial materialType;
    private Predicate<ItemStack> predicate = stack -> true;

    public ArmorSetSkillRequirement(ArmorMaterial material) {
        this.materialType = material;
    }

    public ArmorSetSkillRequirement(ArmorMaterial material, Predicate<ItemStack> predicate) {
        this(material);
        this.predicate = predicate;
    }

    @Override
    public boolean hasRequirement(Player player) {
        NonNullList<ItemStack> tempList = NonNullList.create();
        checkInventoryWithList(tempList, player.getInventory());
        return tempList.size() == 4;
    }

    @Override
    public void consumeRequirement(ServerPlayer player) {
        checkInventoryWithList(armorList, player.getInventory());
        armorList.forEach(i -> i.setCount(0));
    }

    @Override
    public MutableComponent getFancyDescription(Player player) {
        NonNullList<ItemStack> tempList = NonNullList.create();
        checkInventoryWithList(tempList, player.getInventory());
        int current = tempList.size() == 4 ? 1 : 0;
        return Component.literal("%s/%s Set Of Enchanted Golden Armor".formatted(current, 1));
    }

    private void checkInventoryWithList(NonNullList<ItemStack> list, Inventory inventory) {
        inventory.items.forEach(s -> {
            if (s.getItem() instanceof ArmorItem item && item.getMaterial() == materialType && predicate.test(s)) {
                if (list.stream().filter(i -> i.getItem() instanceof ArmorItem)
                        .map(i -> (ArmorItem) i.getItem())
                        .noneMatch(i -> i.getType() == item.getType())) {
                    list.add(s);
                }
            }
        });
    }
}
