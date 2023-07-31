package com.divinity.hmedia.rgrbillionaire.init;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.requirement.ArmorSetSkillRequirement;
import com.divinity.hmedia.rgrbillionaire.requirement.MoneySkillRequirement;
import com.divinity.hmedia.rgrbillionaire.skill.MorphSkill;
import com.divinity.hmedia.rgrbillionaire.skill.tree.CombatTree;
import com.divinity.hmedia.rgrbillionaire.skill.tree.EvolutionTree;
import com.divinity.hmedia.rgrbillionaire.skill.tree.UtilityTree;
import dev._100media.hundredmediaabilities.ability.Ability;
import dev._100media.hundredmediaabilities.capability.AbilityHolderAttacher;
import dev._100media.hundredmediaabilities.init.HMAAbilityInit;
import dev._100media.hundredmediamorphs.init.HMMMorphInit;
import dev._100media.hundredmediaquests.init.HMQSkillsInit;
import dev._100media.hundredmediaquests.skill.Skill;
import dev._100media.hundredmediaquests.skill.SkillTree;
import dev._100media.hundredmediaquests.skill.defaults.MenuProvidingTree;
import dev._100media.hundredmediaquests.skill.defaults.QuestSkill;
import dev._100media.hundredmediaquests.skill.defaults.SimpleSkill;
import dev._100media.hundredmediaquests.skill.requirements.ItemSkillRequirement;
import dev._100media.hundredmediaquests.skill.requirements.ItemTagSkillRequirement;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.List;

public class SkillInit {
    public static final DeferredRegister<SkillTree> SKILL_TREES = DeferredRegister.create(HMQSkillsInit.SKILL_TREES.getRegistryName(), RGRBillionaire.MODID);
    public static final DeferredRegister<Skill> SKILLS = DeferredRegister.create(HMQSkillsInit.SKILLS.getRegistryName(), RGRBillionaire.MODID);

    // Evolution
    public static final RegistryObject<Skill> BROKE_BABY = SKILLS.register("broke_baby", () -> new MorphSkill(
            Component.literal("Broke Baby"),
            Component.literal("%s Hearts".formatted(5)),
            Arrays.asList(),
            () -> HMMMorphInit.getRegistry().getValue(ForgeRegistries.ENTITY_TYPES.getKey(EntityType.ZOMBIE)) // Placeholder
    ));
    public static final RegistryObject<Skill> TIGHT_BUDGET_TEEN = SKILLS.register("tight_budget_teen", () -> new MorphSkill(
            Component.literal("Tight Budget Teen"),
            Component.literal("%s Hearts, Strength %s, Speed %s"),
            Arrays.asList(
                    new ItemSkillRequirement(() -> Items.MANGROVE_LEAVES, 64),
                    new ItemSkillRequirement(() -> Items.GOLD_INGOT, 32),
                    new ItemSkillRequirement(() -> Items.MAP, 1)
            ),
            () -> HMMMorphInit.getRegistry().getValue(ForgeRegistries.ENTITY_TYPES.getKey(EntityType.ZOMBIE)) // Placeholder
    ));
    public static final RegistryObject<Skill> MIDDLE_CLASS_MAN = SKILLS.register("middle_class_man", () -> new MorphSkill(
            Component.literal("Middle Class Man"),
            Component.literal("%s Hearts, Strength %s, Speed %s"),
            Arrays.asList(
                    new ItemSkillRequirement(() -> Items.GLOW_BERRIES, 64),
                    new ItemSkillRequirement(() -> Items.PRISMARINE_SHARD, 32),
                    new ItemSkillRequirement(() -> Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
            ),
            () -> HMMMorphInit.getRegistry().getValue(ForgeRegistries.ENTITY_TYPES.getKey(EntityType.ZOMBIE)) // Placeholder
    ));
    public static final RegistryObject<Skill> MULTI_MILLIONAIRE = SKILLS.register("multi_millionaire", () -> new MorphSkill(
            Component.literal("Multi Millionaire"),
            Component.literal("%s Hearts, Strength %s, Speed %s"),
            Arrays.asList(
                    new ItemSkillRequirement(() -> Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, 1),
                    new ItemSkillRequirement(() -> Items.TOTEM_OF_UNDYING, 2),
                    new ItemSkillRequirement(() -> Items.MAGMA_CREAM, 10)
            ),
            () -> HMMMorphInit.getRegistry().getValue(ForgeRegistries.ENTITY_TYPES.getKey(EntityType.ZOMBIE)) // Placeholder
    ));
    public static final RegistryObject<Skill> THE_BILLIONAIRE = SKILLS.register("the_billionaire", () -> new MorphSkill(
            Component.literal("The Billionaire"),
            Component.literal("%s Hearts, Strength %s, Speed %s"),
            Arrays.asList(
                    new ItemSkillRequirement(() -> Items.DRAGON_EGG, 1),
                    new ItemSkillRequirement(() -> Items.NETHER_STAR, 1),
                    new ItemSkillRequirement(() -> Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
            ),
            () -> HMMMorphInit.getRegistry().getValue(ForgeRegistries.ENTITY_TYPES.getKey(EntityType.ZOMBIE)) // Placeholder
    ));

    public static final RegistryObject<MenuProvidingTree> EVOLUTION_TREE = SKILL_TREES.register("evolution", () -> new EvolutionTree(
            Component.literal("Evolution"),
            Arrays.asList(BROKE_BABY, TIGHT_BUDGET_TEEN, MIDDLE_CLASS_MAN, MULTI_MILLIONAIRE, THE_BILLIONAIRE)
    ));
    // Combat
    public static final RegistryObject<Skill> THE_COIN_CANNON = SKILLS.register("the_coin_cannon", () -> new QuestSkill(
            Component.literal("The Coin Cannon"),
            Component.literal("""
            This held item takes money from the Billionaire and shoots a fast moving coin. Shift+Right Click to change currency:
                                
            Pennies: Can be shot at rapid fire! Does 8 impact damage per shot (1$/Per Shot)
            Quarters: Does a powerful and concentrated straight shot. This shot does a devastating 30 Damage but has a 3s Cooldown. (5$/Per Shot)
            Silver Dollars: Acts as a rocket launcher and explodes surfaces and entities it makes contact with. Has a 5s Cooldown. (10$/Per Shot)"""),
            QuestInit.COIN_CANNON
    ));
    public static final RegistryObject<Skill> EXPLOIT_WORKING_CLASS = SKILLS.register("exploit_working_class", () -> new QuestSkill(
            Component.literal("Exploit The Working Class"),
            Component.literal(""),
            QuestInit.EXPLOIT_WORKING_CLASS
    ));
    public static final RegistryObject<Skill> GRAND_GIVEAWAY = SKILLS.register("the_grand_giveaway", () -> new QuestSkill(
            Component.literal("The Grand Giveaway"),
            Component.literal(""),
            QuestInit.GRAND_GIVEAWAY
    ));
    public static final RegistryObject<Skill> MARKET_CRASHER = SKILLS.register("market_crasher", () -> new QuestSkill(
            Component.literal("Market Crasher"),
            Component.literal(""),
            QuestInit.MARKET_CRASHER
    ));
    public static final RegistryObject<Skill> ROCKET_TO_MARS = SKILLS.register("rocket_to_mars", () -> new QuestSkill(
            Component.literal("Rocket To Mars"),
            Component.literal(""),
            QuestInit.ROCKET_TO_MARS
    ));

    public static final RegistryObject<MenuProvidingTree> COMBAT_TREE = SKILL_TREES.register("combat", () -> new CombatTree(
            Component.literal("Combat"),
            Arrays.asList(THE_COIN_CANNON, EXPLOIT_WORKING_CLASS, GRAND_GIVEAWAY, MARKET_CRASHER, ROCKET_TO_MARS)
    ));
    // Utility
    public static final RegistryObject<Skill> CRYPTO_MINER = SKILLS.register("crypto_miner", () -> new SimpleSkill(
            Component.literal("Crypto Miner"),
            Component.literal(""),
            Arrays.asList(
                    new ItemSkillRequirement(() -> Items.LAPIS_LAZULI, 16),
                    new ItemSkillRequirement(() -> Items.REPEATER, 8),
                    new ItemSkillRequirement(() -> Items.DIAMOND_PICKAXE, 1),
                    new MoneySkillRequirement(800)
            ),
            player -> {},
            player -> {
            }
    ));
    public static final RegistryObject<Skill> ROBO_BUTLER = SKILLS.register("robo_butler", () -> new SimpleSkill(
            Component.literal("AI Robo-Butler"),
            Component.literal(""),
            Arrays.asList(
                    new ItemSkillRequirement(() -> Items.ENDER_PEARL, 4),
                    new ItemSkillRequirement(() -> Items.MAGMA_BLOCK, 16),
                    new ItemSkillRequirement(() -> Items.TNT, 8),
                    new MoneySkillRequirement(60_000)
            ),
            player -> {},
            player -> {
            }
    ));
    public static final RegistryObject<Skill> STARLINKED_MINEBOOK_MARKETPLACE = SKILLS.register("starlinked_minebook_marketplace", () -> new SimpleSkill(
            Component.literal("Starlinked Minebook Marketplace"),
            Component.literal(""),
            Arrays.asList(
                    new ItemSkillRequirement(() -> Items.LIGHTNING_ROD, 10),
                    new ItemSkillRequirement(() -> Items.QUARTZ, 64),
                    new ItemTagSkillRequirement(() -> ItemTags.MUSIC_DISCS, 3, Component.literal("Music Disc")),
                    new MoneySkillRequirement(350_000)
            ),
            player -> {},
            player -> {
            }
    ));
    // Here
    public static final RegistryObject<Skill> GOLDEN_JETPACK = SKILLS.register("golden_jetpack", () -> new SimpleSkill(
            Component.literal("Golden Jetpack"),
            Component.literal(""),
            Arrays.asList(
                    new ArmorSetSkillRequirement(ArmorMaterials.GOLD, ItemStack::isEnchanted),
                    new ItemSkillRequirement(() -> Items.FIREWORK_ROCKET, 32),
                    new ItemSkillRequirement(() -> Items.HEART_OF_THE_SEA, 1),
                    new MoneySkillRequirement(6_666_999)
            ),
            player -> {},
            player -> {
            }
    ));
    public static final RegistryObject<Skill> BILLIONAIRE_CLUB = SKILLS.register("billionaire_club", () -> new SimpleSkill(
            Component.literal("Billionaire's Club"),
            Component.literal(""),
            List.of(
                    new MoneySkillRequirement(1_000_000_000)
            ),
            player -> {},
            player -> {
            }
    ));

    public static final RegistryObject<MenuProvidingTree> UTILITY_TREE = SKILL_TREES.register("utility", () -> new UtilityTree(
            Component.literal("Utility"),
            Arrays.asList(CRYPTO_MINER, ROBO_BUTLER, STARLINKED_MINEBOOK_MARKETPLACE, GOLDEN_JETPACK, BILLIONAIRE_CLUB)
    ));


    public static void unlockAbility(Player player, Ability abilityToUnlock) {
        AbilityHolderAttacher.getAbilityHolder(player).ifPresent(holder -> {
            int index = -1;
            boolean hasAbility = false;
            for (int i = 0; i < holder.getAbilitiesSize(); ++i) {
                Ability ability = holder.getAbility(i);
                if (index == -1 && ability == HMAAbilityInit.NONE.get()) {
                    index = i;
                }
                if (ability == abilityToUnlock) {
                    hasAbility = true;
                    break;
                }
            }
            if (index != -1 && !hasAbility) {
                holder.setAbility(index, abilityToUnlock);
            }
        });
    }

    public static void removeAbility(Player player, Ability ability) {
        AbilityHolderAttacher.getAbilityHolder(player).ifPresent(holder -> {
            int i = holder.getAbilities().indexOf(ability);
            if (i == -1) {
                return;
            }

            holder.setAbility(i, HMAAbilityInit.NONE.get());
        });
    }
}
