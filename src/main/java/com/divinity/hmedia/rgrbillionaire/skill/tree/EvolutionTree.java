package com.divinity.hmedia.rgrbillionaire.skill.tree;

import com.divinity.hmedia.rgrbillionaire.init.MenuInit;
import dev._100media.hundredmediaquests.skill.Skill;
import dev._100media.hundredmediaquests.skill.defaults.MenuProvidingTree;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class EvolutionTree extends MenuProvidingTree {

    public EvolutionTree(@NotNull MutableComponent name, @NotNull List<Supplier<Skill>> skills, @Nullable ResourceLocation buttonImage) {
        super(name, skills, buttonImage, MenuInit.EVOLUTION_TREE.get());
    }

    public EvolutionTree(@NotNull MutableComponent name, @NotNull List<Supplier<Skill>> skills) {
        super(name, skills, MenuInit.EVOLUTION_TREE.get());

    }
}
