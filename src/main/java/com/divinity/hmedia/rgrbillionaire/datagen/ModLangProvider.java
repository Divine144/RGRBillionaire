package com.divinity.hmedia.rgrbillionaire.datagen;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.init.*;
import com.google.common.collect.ImmutableMap;
import dev._100media.hundredmediaabilities.ability.Ability;
import dev._100media.hundredmediaabilities.marker.Marker;
import dev._100media.hundredmediamorphs.morph.Morph;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ModLangProvider extends LanguageProvider {
    protected static final Map<String, String> REPLACE_LIST = ImmutableMap.of(
            "tnt", "TNT",
            "sus", ""
    );

    public ModLangProvider(PackOutput gen) {
        super(gen, RGRBillionaire.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        EntityInit.ENTITIES.getEntries().forEach(this::entityLang);
        ItemInit.ITEMS.getEntries().stream().filter(p -> p != ItemInit.STACKABLE_MONEY).forEach(this::itemLang);
        BlockInit.BLOCKS.getEntries().forEach(this::blockLang);
        AbilityInit.ABILITIES.getEntries().forEach(this::abilityLang);
        MarkerInit.MARKERS.getEntries().forEach(this::markerLang);
        MorphInit.MORPHS.getEntries().forEach(this::morphLang);

        add("key.rgrbillionaire.skill_tree", "Open Skill Tree");
        add("key.rgrbillionaire.money_explosion", "Money Explosion");
        add("key.rgrbillionaire.butler_recall", "Recall Butler");
        add("key.category.rgrbillionaire", "RGRBillionaire");

        add("item.rgrbillionaire.stackable_money", "Money");

        add("itemGroup.hundredMediaTab", "100 Media");
        // Quest Descriptions
        add("quest.goal.rgrbillionaire.loot_desert_temple_goal.description", "Discover and loot a Desert Temple");
        add("quest.goal.rgrbillionaire.loot_end_ship_goal.description", "Discover and loot an End Ship");
        add("quest.goal.rgrbillionaire.steal_money_goal.description", "Steal $2,500 from players using Exploit The Working Class");
        add("quest.goal.rgrbillionaire.new_look_advancement_goal.description", "Earn the Advancement \"Crafting a New Look\"");
        add("quest.goal.rgrbillionaire.kill_player_coin_cannon.description", "Kill 3 players with the Coin Cannon");
        add("quest.goal.rgrbillionaire.summon_iron_golem_advancement_goal.description", "Earn the Advancement \"Hired Help\"");
        add("quest.goal.rgrbillionaire.light_as_a_rabbit_advancement_goal.description", "Earn the Advancement \"Light as a Rabbit\"");
        add("quest.goal.rgrbillionaire.kill_player_grand_giveaway.description", "Kill 3 Players with the Grand Giveaway");
        add("quest.goal.rgrbillionaire.cure_zombie_villager_advancement_goal.description", "Earn the Advancement \"Zombie Doctor\"");
        add("quest.goal.rgrbillionaire.kill_player_fall_damage.description", "Kill 5 Players with fall damage");
        add("quest.goal.rgrbillionaire.smells_interesting_advancement_goal.description", "Earn the Advancement \"Smells Interesting\"");
        add("quest.goal.rgrbillionaire.harvest_spawner.description", "Harvest two Spawners of any kind");

        // Quest Display Descriptions
        add("quest.goal.rgrbillionaire.loot_desert_temple_goal", "Discover and loot a Desert Temple");
        add("quest.goal.rgrbillionaire.loot_end_ship_goal", "Discover and loot an End Ship");
        add("quest.goal.rgrbillionaire.steal_money_goal", "Steal $2,500 from players using Exploit The Working Class");
        add("quest.goal.rgrbillionaire.new_look_advancement_goal", "Earn the Advancement \"Crafting a New Look\"");
        add("quest.goal.rgrbillionaire.kill_player_coin_cannon", "Kill 3 players with the Coin Cannon");
        add("quest.goal.rgrbillionaire.summon_iron_golem_advancement_goal", "Earn the Advancement \"Hired Help\"");
        add("quest.goal.rgrbillionaire.light_as_a_rabbit_advancement_goal", "Earn the Advancement \"Light as a Rabbit\"");
        add("quest.goal.rgrbillionaire.kill_player_grand_giveaway", "Kill 3 Players with the Grand Giveaway");
        add("quest.goal.rgrbillionaire.cure_zombie_villager_advancement_goal", "Earn the Advancement \"Zombie Doctor\"");
        add("quest.goal.rgrbillionaire.kill_player_fall_damage", "Kill 5 Players with fall damage");
        add("quest.goal.rgrbillionaire.smells_interesting_advancement_goal", "Earn the Advancement \"Smells Interesting\"");
        add("quest.goal.rgrbillionaire.harvest_spawner", "Harvest Two Spawners");
    }

    protected void itemLang(RegistryObject<Item> entry) {
        if (!(entry.get() instanceof BlockItem) || entry.get() instanceof ItemNameBlockItem) {
            addItem(entry, checkReplace(entry));
        }
    }

    protected void blockLang(RegistryObject<Block> entry) {
        addBlock(entry, checkReplace(entry));
    }

    protected void entityLang(RegistryObject<EntityType<?>> entry) {
        addEntityType(entry, checkReplace(entry));
    }

    protected void abilityLang(RegistryObject<Ability> entry) {
        add(entry.get().getDescriptionId(), checkReplace(entry));
    }

    protected void markerLang(RegistryObject<Marker> entry) {
        add(entry.get().getDescriptionId(), checkReplace(entry));
    }

    protected void morphLang(RegistryObject<Morph> entry) {
        add(entry.get().getDescriptionId(), checkReplace(entry));
    }

    protected String checkReplace(RegistryObject<?> registryObject) {
        return Arrays.stream(registryObject.getId().getPath().split("_"))
                .map(this::checkReplace)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "))
                .trim();
    }

    protected String checkReplace(String string) {
        return REPLACE_LIST.containsKey(string) ? REPLACE_LIST.get(string) : StringUtils.capitalize(string);
    }
}
