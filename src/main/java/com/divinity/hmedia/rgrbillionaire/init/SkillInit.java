package com.divinity.hmedia.rgrbillionaire.init;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolder;
import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.entity.AIRoboButlerEntity;
import com.divinity.hmedia.rgrbillionaire.requirement.ArmorSetSkillRequirement;
import com.divinity.hmedia.rgrbillionaire.requirement.ItemTreasureMapSkillRequirement;
import com.divinity.hmedia.rgrbillionaire.requirement.ItemSkillRequirementSpecial;
import com.divinity.hmedia.rgrbillionaire.requirement.MoneySkillRequirement;
import com.divinity.hmedia.rgrbillionaire.skill.MorphSkill;
import com.divinity.hmedia.rgrbillionaire.skill.tree.CombatTree;
import com.divinity.hmedia.rgrbillionaire.skill.tree.EvolutionTree;
import com.divinity.hmedia.rgrbillionaire.skill.tree.UtilityTree;
import dev._100media.hundredmediaabilities.ability.Ability;
import dev._100media.hundredmediaabilities.capability.AbilityHolderAttacher;
import dev._100media.hundredmediaabilities.capability.MarkerHolderAttacher;
import dev._100media.hundredmediaabilities.init.HMAAbilityInit;
import dev._100media.hundredmediaquests.init.HMQSkillsInit;
import dev._100media.hundredmediaquests.skill.Skill;
import dev._100media.hundredmediaquests.skill.SkillTree;
import dev._100media.hundredmediaquests.skill.defaults.MenuProvidingTree;
import dev._100media.hundredmediaquests.skill.defaults.QuestSkill;
import dev._100media.hundredmediaquests.skill.defaults.SimpleSkill;
import dev._100media.hundredmediaquests.skill.requirements.ItemSkillRequirement;
import dev._100media.hundredmediaquests.skill.requirements.ItemTagSkillRequirement;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
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
            MorphInit.BROKE_BABY
    ));
    public static final RegistryObject<Skill> TIGHT_BUDGET_TEEN = SKILLS.register("tight_budget_teen", () -> new MorphSkill(
            Component.literal("Tight Budget Teen"),
            Component.literal("%s Hearts, Strength %s, Speed %s".formatted(15, "I", "I")),
            Arrays.asList(
                    new ItemSkillRequirement(() -> Items.MANGROVE_LEAVES, 64),
                    new ItemSkillRequirement(() -> Items.GOLD_INGOT, 32),
                    new ItemTreasureMapSkillRequirement(1)
            ),
            MorphInit.TIGHT_BUDGET_TEEN
    ));
    public static final RegistryObject<Skill> MIDDLE_CLASS_MAN = SKILLS.register("middle_class_man", () -> new MorphSkill(
            Component.literal("Middle Class Man"),
            Component.literal("%s Hearts, Strength %s, Speed %s".formatted(25, "II", "II")),
            Arrays.asList(
                    new ItemSkillRequirement(() -> Items.GLOW_BERRIES, 64),
                    new ItemSkillRequirement(() -> Items.PRISMARINE_SHARD, 32),
                    new ItemSkillRequirementSpecial(() -> Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, 1, "[Wild Armor Trim Smithing Template]")
            ),
            MorphInit.MIDDLE_CLASS_MAN
    ));
    public static final RegistryObject<Skill> MULTI_MILLIONAIRE = SKILLS.register("multi_millionaire", () -> new MorphSkill(
            Component.literal("Multi Millionaire"),
            Component.literal("%s Hearts, Strength %s, Speed %s".formatted(35, "III", "III")),
            Arrays.asList(
                    new ItemSkillRequirementSpecial(() -> Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, 1, "[Sentry Armor Trim Smithing Template]"),
                    new ItemSkillRequirement(() -> Items.TOTEM_OF_UNDYING, 2),
                    new ItemSkillRequirement(() -> Items.MAGMA_CREAM, 10)
            ),
            MorphInit.MULTI_MILLIONAIRE
    ));
    public static final RegistryObject<Skill> THE_BILLIONAIRE = SKILLS.register("the_billionaire", () -> new MorphSkill(
            Component.literal("The Billionaire"),
            Component.literal("%s Hearts, Strength %s, Speed %s".formatted(50, "IV", "IV")),
            Arrays.asList(
                    new ItemSkillRequirement(() -> Items.DRAGON_EGG, 1),
                    new ItemSkillRequirement(() -> Items.NETHER_STAR, 1),
                    new ItemSkillRequirementSpecial(() -> Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, 1, "[Eye Armor Smithing Template]")
            ),
            MorphInit.THE_BILLIONAIRE
    ));

    public static final RegistryObject<MenuProvidingTree> EVOLUTION_TREE = SKILL_TREES.register("evolution", () -> new EvolutionTree(
            Component.literal("Evolution"),
            Arrays.asList(BROKE_BABY, TIGHT_BUDGET_TEEN, MIDDLE_CLASS_MAN, MULTI_MILLIONAIRE, THE_BILLIONAIRE)
    ));
    // Combat
    public static final RegistryObject<Skill> THE_COIN_CANNON = SKILLS.register("the_coin_cannon", () -> new QuestSkill(
            Component.literal("The Coin Cannon"),
            Component.literal("""
            This held item takes money from you and shoots a fast moving coin. Shift+Right Click to change currency:
            Pennies: Rapid Fire and 8 damage/shot (1$/Per Shot)
            Quarters: Deals a Devastating 30 damage, with a 3s cooldown (5$/Per Shot)
            Silver Dollars: Explodes on contact. Has a 5s Cooldown. (10$/Per Shot)"""),
            QuestInit.COIN_CANNON
    ));
    public static final RegistryObject<Skill> EXPLOIT_WORKING_CLASS = SKILLS.register("exploit_working_class", () -> new QuestSkill(
            Component.literal("Exploit The Working Class"),
            Component.literal("""
                    You create a 6 block red ring aura around yourself.
                    Players that step within this aura have their life energy sucked out quickly and are given Weakness I.
                    The taken health of other players is given to the you, and you also generate $50/s per player within the aura.
                    """),
            QuestInit.EXPLOIT_WORKING_CLASS
    ));
    public static final RegistryObject<Skill> GRAND_GIVEAWAY = SKILLS.register("the_grand_giveaway", () -> new QuestSkill(
            Component.literal("The Grand Giveaway"),
            Component.literal("Summon a shower of Dollar Bills and Coins that rain in a 20 block area following you. " +
                    "Dollar bills do 10 impact damage and coins cause a small explosion upon impact, alongside 10 impact damage."),
            QuestInit.GRAND_GIVEAWAY
    ));
    public static final RegistryObject<Skill> MARKET_CRASHER = SKILLS.register("market_crasher", () -> new QuestSkill(
            Component.literal("Market Crasher"),
            Component.literal("A slug shotgun that shoots one long red line that looks like a rising stock graph. This projectile line knocks the entities it comes into contact with upwards and backwards into the air 60+ blocks."),
            QuestInit.MARKET_CRASHER
    ));
    public static final RegistryObject<Skill> ROCKET_TO_MARS = SKILLS.register("rocket_to_mars", () -> new QuestSkill(
            Component.literal("Rocket To Mars"),
            Component.literal("The Ultimate Pay 2 Win Protocol."),
            QuestInit.ROCKET_TO_MARS
    ));

    public static final RegistryObject<MenuProvidingTree> COMBAT_TREE = SKILL_TREES.register("combat", () -> new CombatTree(
            Component.literal("Combat"),
            Arrays.asList(THE_COIN_CANNON, EXPLOIT_WORKING_CLASS, GRAND_GIVEAWAY, MARKET_CRASHER, ROCKET_TO_MARS)
    ));
    // Utility
    public static final RegistryObject<Skill> CRYPTO_MINER = SKILLS.register("crypto_miner", () -> new SimpleSkill(
            Component.literal("Crypto Miner"),
            Component.literal("""
                    Place these server towers in the world to increase the rate you make money.
                    Feeding Materials to them increases the production rate:
                    Standard = 1$/s
                    Iron Miner = 3$/s (Use 1 Iron Ingot)
                    Gold Miner = 5$/s (Use 1 Gold Ingot)
                    Diamond Miner = 7$/s (Use 1 Diamond)
                    Netherite Miner = 10$/s (Use 1 Netherite Scrap)
                    Omni Miner = 1000$/s (Use 1 Dragon Egg / The Omni Miner is Unbreakable)     
                    """),
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
            Component.literal("Summon a diligent Mining Butler who tirelessly auto mines ores within a 10-block radius of him, including through walls." +
                    "This Butler can be summoned or de-summoned as you wish, and also has his own inventory to store mined goods."),
            Arrays.asList(
                    new ItemSkillRequirement(() -> Items.ENDER_PEARL, 4),
                    new ItemSkillRequirement(() -> Items.MAGMA_BLOCK, 16),
                    new ItemSkillRequirement(() -> Items.TNT, 8),
                    new MoneySkillRequirement(60_000)
            ),
            player -> {
                ServerLevel level = player.serverLevel();
                var butler = EntityInit.BUTLER_ENTITY.get().create(level);
                if (butler != null) {
                    butler.setPos(player.position());
                    butler.setOwnerUUID(player.getUUID());
                    BillionaireHolderAttacher.getHolder(player).ifPresent(h -> h.setLinkedEntityID(butler.getId()));
                    level.addFreshEntity(butler);
                }
            },
            player -> {
                ServerLevel level = player.serverLevel();
                int id = BillionaireHolderAttacher.getHolder(player).map(BillionaireHolder::getLinkedEntityID).orElse(-1);
                Entity entity = level.getEntity(id);
                if (entity instanceof AIRoboButlerEntity butler) {
                    butler.dismissButler();
                }
                BillionaireHolderAttacher.getHolder(player).ifPresent(h -> h.setLinkedEntityID(-1));
            }
    ));
    public static final RegistryObject<Skill> STARLINKED_MINEBOOK_MARKETPLACE = SKILLS.register("starlinked_minebook_marketplace", () -> new SimpleSkill(
            Component.literal("Starlinked Minebook Marketplace"),
            Component.literal("An online market that allows you to buy and sell extra items and materials. " +
                    "Right click an offer to change it to a sell offer. " +
                    "Shift+Scroll on an offer to cycle through variants (if applicable)"),
            Arrays.asList(
                    new ItemSkillRequirement(() -> Items.LIGHTNING_ROD, 10),
                    new ItemSkillRequirement(() -> Items.QUARTZ, 64),
                    new ItemTagSkillRequirement(() -> ItemTags.MUSIC_DISCS, 3, Component.literal("Music Disc")),
                    new MoneySkillRequirement(350_000)
            ),
            player -> player.getInventory().add(ItemInit.MARKETPLACE.get().getDefaultInstance()),
            player -> {

            }
    ));
    // Here
    public static final RegistryObject<Skill> GOLDEN_JETPACK = SKILLS.register("golden_jetpack", () -> new SimpleSkill(
            Component.literal("Golden Jetpack"),
            Component.literal("""
                    A Golden Jetpack that takes the chest armor slot and acts as an unbreakable Netherite Chestplate.
                    The Golden Jetpack allows you to fly as if they are in creative mode
                    While equipped, every kill on any entities give you $100,000 and all attacks are considered crits.
                    """),
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
            player -> {
                MarkerHolderAttacher.getMarkerHolder(player).ifPresent(m -> {
                    m.addMarker(MarkerInit.BILLIONAIRES_CLUB.get(), true);
                });
            },
            player -> {
                MarkerHolderAttacher.getMarkerHolder(player).ifPresent(m -> {
                    m.removeMarker(MarkerInit.BILLIONAIRES_CLUB.get(), true);
                });
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
