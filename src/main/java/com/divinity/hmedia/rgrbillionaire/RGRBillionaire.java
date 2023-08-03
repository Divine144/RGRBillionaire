package com.divinity.hmedia.rgrbillionaire;

import com.divinity.hmedia.rgrbillionaire.cap.CannonHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.config.ExampleClientConfig;
import com.divinity.hmedia.rgrbillionaire.config.ExampleConfig;
import com.divinity.hmedia.rgrbillionaire.datagen.*;
import com.divinity.hmedia.rgrbillionaire.init.*;
import com.divinity.hmedia.rgrbillionaire.network.NetworkHandler;
import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RGRBillionaire.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RGRBillionaire {
    public static final String MODID = "rgrbillionaire";
    public static final Logger LOGGER = LogManager.getLogger();

    public RGRBillionaire() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ExampleConfig.CONFIG_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ExampleClientConfig.CLIENT_SPEC);


        ItemInit.ITEMS.register(modBus);
        EntityInit.ENTITIES.register(modBus);
        BlockInit.BLOCKS.register(modBus);
        BlockInit.BLOCK_ENTITIES.register(modBus);
        MenuInit.MENUS.register(modBus);
        CreativeModeTabInit.CREATIVE_MODE_TABS.register(modBus);
        MorphInit.MORPHS.register(modBus);
        AbilityInit.ABILITIES.register(modBus);
        MarkerInit.MARKERS.register(modBus);
        QuestInit.QUESTS.register(modBus);
        SkillInit.SKILLS.register(modBus);
        SkillInit.SKILL_TREES.register(modBus);
        BillionaireHolderAttacher.register();
        CannonHolderAttacher.register();
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        NetworkHandler.register();
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        boolean includeServer = event.includeServer();
        boolean includeClient = event.includeClient();
        generator.addProvider(includeServer, new ModRecipeProvider(packOutput));
        generator.addProvider(includeServer, new ModLootTableProvider(packOutput));
        generator.addProvider(includeServer, new ModSoundProvider(packOutput, existingFileHelper));
        generator.addProvider(includeServer, new ModTagProvider.ModBlockTags(packOutput,event.getLookupProvider(), existingFileHelper));
        generator.addProvider(includeServer, new ModTagProvider.ModItemTags(packOutput,event.getLookupProvider(), existingFileHelper));
        generator.addProvider(includeClient, new ModItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(includeClient, new ModBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(includeClient, new ModLangProvider(packOutput));
        generator.addProvider(includeServer, new ModDamageTypeProvider(packOutput, event.getLookupProvider()));
    }
}
