package com.divinity.hmedia.rgrbillionaire.skill.tree;

import com.divinity.hmedia.rgrbillionaire.init.MenuInit;
import dev._100media.hundredmediaquests.skill.Skill;
import dev._100media.hundredmediaquests.skill.defaults.MenuProvidingTree;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class UtilityTree extends MenuProvidingTree {

    public UtilityTree(@NotNull MutableComponent name, @NotNull List<Supplier<Skill>> skills, @Nullable ResourceLocation buttonImage) {
        super(name, skills, buttonImage, MenuInit.UTILITY_TREE.get());
    }

    public UtilityTree(@NotNull MutableComponent name, @NotNull List<Supplier<Skill>> skills) {
        super(name, skills, MenuInit.UTILITY_TREE.get());
    }
}
