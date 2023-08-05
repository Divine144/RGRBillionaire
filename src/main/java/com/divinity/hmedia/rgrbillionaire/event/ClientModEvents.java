package com.divinity.hmedia.rgrbillionaire.event;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.client.renderer.AIRobotButlerEntityRenderer;
import com.divinity.hmedia.rgrbillionaire.client.renderer.ShootableCoinRenderer;
import com.divinity.hmedia.rgrbillionaire.client.screen.ButlerInventoryScreen;
import com.divinity.hmedia.rgrbillionaire.client.screen.MarketplaceScreen;
import com.divinity.hmedia.rgrbillionaire.entity.AIRoboButlerEntity;
import com.divinity.hmedia.rgrbillionaire.init.EntityInit;
import com.divinity.hmedia.rgrbillionaire.init.MenuInit;
import com.divinity.hmedia.rgrbillionaire.init.SkillInit;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import dev._100media.hundredmediageckolib.client.model.SimpleGeoEntityModel;
import dev._100media.hundredmediaquests.client.screen.QuestSkillScreen;
import dev._100media.hundredmediaquests.client.screen.SkillScreen;
import dev._100media.hundredmediaquests.client.screen.TreeScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.awt.*;
import java.util.Arrays;

@Mod.EventBusSubscriber(modid = RGRBillionaire.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    public static final KeyMapping SKILL_TREE_KEY = new KeyMapping("key." + RGRBillionaire.MODID + ".skill_tree", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.category." + RGRBillionaire.MODID);

    @SubscribeEvent
    public static void registerKeybind(RegisterKeyMappingsEvent event) {
        event.register(SKILL_TREE_KEY);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.PENNY_ENTITY.get(), ctx -> new ShootableCoinRenderer(ctx, "penny_entity"));
        event.registerEntityRenderer(EntityInit.QUARTER_ENTITY.get(), ctx -> new ShootableCoinRenderer(ctx, "quarter_entity"));
        event.registerEntityRenderer(EntityInit.SILVER_DOLLAR_ENTITY.get(), ctx -> new ShootableCoinRenderer(ctx, "silver_dollar_entity"));
        event.registerEntityRenderer(EntityInit.BUTLER_ENTITY.get(), AIRobotButlerEntityRenderer::new);
    }

    @SubscribeEvent
    public static void initClient(FMLClientSetupEvent event) {
        MenuScreens.register(MenuInit.BUTLER_MENU.get(), ButlerInventoryScreen::new);
        MenuScreens.register(MenuInit.MARKET_MENU.get(), MarketplaceScreen::new);
        MenuScreens.register(MenuInit.SKILL_TREE.get(), (AbstractContainerMenu menu, Inventory inv, Component title) -> new TreeScreen(menu, inv, title,
                new ResourceLocation(RGRBillionaire.MODID, "textures/gui/screen/skill_tree.png"), 21, 22,
                Arrays.asList(
                        new Pair<>(SkillInit.EVOLUTION_TREE, new Pair<>(56, 80)),
                        new Pair<>(SkillInit.COMBAT_TREE, new Pair<>(115, 80)),
                        new Pair<>(SkillInit.UTILITY_TREE, new Pair<>(180, 80))
                ), 256, 256, 256, 165
        ));
        MenuScreens.register(MenuInit.EVOLUTION_TREE.get(), (AbstractContainerMenu menu, Inventory inv, Component title) -> new SkillScreen(menu, inv, title,
                new ResourceLocation(RGRBillionaire.MODID, "textures/gui/screen/evolution.png"), 35, 36,
                Arrays.asList(
                        new Pair<>(11, 128),
                        new Pair<>(59, 128),
                        new Pair<>(107, 128),
                        new Pair<>(155, 128),
                        new Pair<>(203, 128)
                ), SkillInit.EVOLUTION_TREE.get(), 256, 256, 256, 230
        ));
        MenuScreens.register(MenuInit.COMBAT_TREE.get(), (AbstractContainerMenu menu, Inventory inv, Component title) -> new QuestSkillScreen(menu, inv, title,
                new ResourceLocation(RGRBillionaire.MODID, "textures/gui/screen/combat.png"), 23, 23,
                Arrays.asList(
                        new Pair<>(56, 43),
                        new Pair<>(85, 43),
                        new Pair<>(114, 43),
                        new Pair<>(143, 43),
                        new Pair<>(172, 43)
                ), SkillInit.COMBAT_TREE.get(), 256, 256, 256, 189
        ));
        MenuScreens.register(MenuInit.UTILITY_TREE.get(), (AbstractContainerMenu menu, Inventory inv, Component title) -> new SkillScreen(menu, inv, title,
                new ResourceLocation(RGRBillionaire.MODID, "textures/gui/screen/utility.png"), 32, 32,
                Arrays.asList(
                        new Pair<>(54, 53),
                        new Pair<>(112, 53),
                        new Pair<>(170, 53),
                        new Pair<>(83, 102),
                        new Pair<>(141, 102)
                ), SkillInit.UTILITY_TREE.get(), 256, 256, 256, 192
        ));
    }
}
