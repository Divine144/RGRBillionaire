package com.divinity.hmedia.rgrbillionaire.datagen;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.init.ItemInit;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput generator, ExistingFileHelper existingFileHelper) {
        super(generator, RGRBillionaire.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
         Stream.of(ItemInit.SWORD_OF_TRUTH)
                 .map(Supplier::get)
                 .forEach(this::simpleHandHeldModel);

         this.simpleGeneratedModel(ItemInit.ITEMS.getEntries().stream().filter(p -> p.getId().toString().contains("crypto")).findFirst().orElseThrow().get());

        Stream.of(ItemInit.MONEY, ItemInit.STACKABLE_MONEY, ItemInit.MARKETPLACE, ItemInit.HEART, ItemInit.GOLDEN_JETPACK, ItemInit.JAIL, ItemInit.ROCKET_TO_MARS)
                .map(Supplier::get)
                .forEach(this::simpleGeneratedModel);

        // Stream.of()
        //         .map(Supplier::get)
        //         .forEach(this::simpleBlockItemModel);
    }

    protected ItemModelBuilder simpleBlockItemModel(Block block) {
        String name = getName(block);
        return withExistingParent(name, modLoc("block/" + name));
    }

    protected ItemModelBuilder simpleGeneratedModel(Item item) {
        return simpleModel(item, mcLoc("item/generated"));
    }

    protected ItemModelBuilder simpleHandHeldModel(Item item) {
        return simpleModel(item, mcLoc("item/handheld"));
    }

    protected ItemModelBuilder simpleModel(Item item, ResourceLocation parent) {
        String name = getName(item);
        return singleTexture(name, parent, "layer0", modLoc("item/" + name));
    }

    protected String getName(Item item) {
        return ForgeRegistries.ITEMS.getKey(item).getPath();
    }

    protected String getName(Block item) {
        return ForgeRegistries.BLOCKS.getKey(item).getPath();
    }
}
