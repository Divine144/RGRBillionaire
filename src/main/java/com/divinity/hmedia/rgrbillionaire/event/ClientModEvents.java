package com.divinity.hmedia.rgrbillionaire.event;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.block.be.CryptoMinerBlockEntity;
import com.divinity.hmedia.rgrbillionaire.client.gui.ConfettiGuiOverlay;
import com.divinity.hmedia.rgrbillionaire.client.gui.MoneyExplosionGuiOverlay;
import com.divinity.hmedia.rgrbillionaire.client.renderer.*;
import com.divinity.hmedia.rgrbillionaire.client.screen.ButlerInventoryScreen;
import com.divinity.hmedia.rgrbillionaire.client.screen.MarketplaceScreen;
import com.divinity.hmedia.rgrbillionaire.client.screen.MinebookScreen;
import com.divinity.hmedia.rgrbillionaire.client.screen.TaxForumScreen;
import com.divinity.hmedia.rgrbillionaire.entity.RocketEntity;
import com.divinity.hmedia.rgrbillionaire.init.*;
import com.divinity.hmedia.rgrbillionaire.item.DollarFishingPoleItem;
import com.divinity.hmedia.rgrbillionaire.util.BillionaireUtils;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import dev._100media.hundredmediageckolib.client.animatable.IHasGeoRenderer;
import dev._100media.hundredmediageckolib.client.animatable.MotionAttackAnimatable;
import dev._100media.hundredmediageckolib.client.model.SimpleGeoPlayerModel;
import dev._100media.hundredmediageckolib.client.renderer.GeoPlayerRenderer;
import dev._100media.hundredmediageckolib.client.renderer.layer.GeoPlayerArmorLayer;
import dev._100media.hundredmediageckolib.client.renderer.layer.GeoSeparatedEntityRenderLayer;
import dev._100media.hundredmediamorphs.capability.AnimationHolderAttacher;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import dev._100media.hundredmediamorphs.client.model.AdvancedGeoPlayerModel;
import dev._100media.hundredmediamorphs.client.renderer.AdvancedGeoPlayerRenderer;
import dev._100media.hundredmediamorphs.client.renderer.MorphRenderers;
import dev._100media.hundredmediamorphs.morph.Morph;
import dev._100media.hundredmediaquests.client.screen.QuestSkillScreen;
import dev._100media.hundredmediaquests.client.screen.SkillScreen;
import dev._100media.hundredmediaquests.client.screen.TreeScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BuiltInModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.Arrays;
import java.util.Collection;

@Mod.EventBusSubscriber(modid = RGRBillionaire.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    public static final KeyMapping SKILL_TREE_KEY = new KeyMapping("key." + RGRBillionaire.MODID + ".skill_tree", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.category." + RGRBillionaire.MODID);
    public static final KeyMapping MONEY_EXPLOSION_KEY = new KeyMapping("key." + RGRBillionaire.MODID + ".money_explosion", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, "key.category." + RGRBillionaire.MODID);
    public static final KeyMapping BUTLER_RECALL_KEY = new KeyMapping("key." + RGRBillionaire.MODID + ".butler_recall", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, "key.category." + RGRBillionaire.MODID);


    @SubscribeEvent
    public static void registerKeybind(RegisterKeyMappingsEvent event) {
        event.register(SKILL_TREE_KEY);
        event.register(MONEY_EXPLOSION_KEY);
        event.register(BUTLER_RECALL_KEY);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.PENNY_ENTITY.get(), ctx -> new ShootableCoinRenderer(ctx, "penny_entity"));
        event.registerEntityRenderer(EntityInit.QUARTER_ENTITY.get(), ctx -> new ShootableCoinRenderer(ctx, "quarter_entity"));
        event.registerEntityRenderer(EntityInit.SILVER_DOLLAR_ENTITY.get(), ctx -> new ShootableCoinRenderer(ctx, "silver_dollar_entity"));
        event.registerEntityRenderer(EntityInit.BUTLER_ENTITY.get(), AIRobotButlerEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.STOCK_GRAPH_ENTITY.get(), StockGraphEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.DOLLAR_BOBBER_ENTITY.get(), DollarFishingHookRenderer::new);
        createSimpleMorphRenderer(MorphInit.BROKE_BABY.get(), "broke_baby", new MotionAttackAnimatable() {
            @Override
            protected PlayState attackAnimationEvent(AnimationState<? extends MotionAttackAnimatable> state) {
                AnimationController<?> controller = state.getController();
                if (state.getData(DataTickets.ENTITY) instanceof AbstractClientPlayer player) {
                    controller.transitionLength(0);
                    if (player.swingTime > 0) {
                        controller.setAnimation(RawAnimation.begin().thenLoop("attack"));
                        return PlayState.CONTINUE;
                    }
                    motionAnimationEvent(state);
                }
                return PlayState.CONTINUE;
            }

            @Override
            protected PlayState motionAnimationEvent(AnimationState<? extends MotionAttackAnimatable> state) {
                AnimationController<?> controller = state.getController();
                if (state.getData(DataTickets.ENTITY) instanceof AbstractClientPlayer player) {
                    controller.transitionLength(0);
                    if (player.getVehicle() != null) {
                        controller.setAnimation(RawAnimation.begin().thenLoop("sit"));
                    }
                    else if (player.isShiftKeyDown()) {
                        controller.setAnimation(RawAnimation.begin().thenLoop("crouch"));
                    }
                     else if (state.isMoving()) {
                        controller.setAnimation(RawAnimation.begin().thenLoop("walk"));
                    }
                     else {
                        controller.setAnimation(RawAnimation.begin().thenLoop("idle"));
                    }
                }
                return PlayState.CONTINUE;
            }
        }, 1.5f);
        createAdvancedMorphRenderer(MorphInit.TIGHT_BUDGET_TEEN.get(), "tight_budget_teen", new MotionAttackAnimatable(), 1f);
        createAdvancedMorphRenderer(MorphInit.MIDDLE_CLASS_MAN.get(), "middle_class_man", new MotionAttackAnimatable(), 1f);
        createAdvancedMorphRenderer(MorphInit.MULTI_MILLIONAIRE.get(), "multi_millionaire", new MotionAttackAnimatable(), 1f);
        createBillionaireMorph(MorphInit.THE_BILLIONAIRE.get(), "the_billionaire", new MotionAttackAnimatable() {
            @Override
            protected PlayState attackAnimationEvent(AnimationState<? extends MotionAttackAnimatable> state) {
                AnimationController<?> controller = state.getController();
                if (state.getData(DataTickets.ENTITY) instanceof AbstractClientPlayer player) {
                    controller.transitionLength(0);
                    if (player.swingTime > 0) {
                        controller.setAnimation(RawAnimation.begin().thenLoop("attack"));
                        return PlayState.CONTINUE;
                    }
                    motionAnimationEvent(state);
                }
                return PlayState.CONTINUE;
            }

            @Override
            protected PlayState motionAnimationEvent(AnimationState<? extends MotionAttackAnimatable> state) {
                AnimationController<?> controller = state.getController();
                if (state.getData(DataTickets.ENTITY) instanceof AbstractClientPlayer player) {
                    controller.transitionLength(0);
                    if (player.getVehicle() != null) {
                        controller.setAnimation(RawAnimation.begin().thenLoop("sit"));
                    }
                    else if (state.isMoving()) {
                        controller.setAnimation(RawAnimation.begin().thenLoop(player.isSprinting() && !player.isCrouching() ? "run" : "walk"));
                    }
                    else {
                        controller.setAnimation(RawAnimation.begin().thenLoop("idle"));
                    }
                }
                return PlayState.CONTINUE;
            }
        }, 1.5f);

        event.registerEntityRenderer(EntityInit.DOLLAR_COIN_ENTITY.get(),ctx -> new ShootableCoinRenderer(ctx, "dollar_coin_entity"));
        event.registerEntityRenderer(EntityInit.DOLLAR_BILL_ENTITY.get(),ctx -> new ShootableCoinRenderer(ctx, "dollar_bill_entity"));
        event.registerEntityRenderer(EntityInit.ROCKET_ENTITY.get(), RocketEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockInit.MINER_BLOCK_ENTITY.get(), ctx -> new GeoBlockRenderer<>(
                new DefaultedBlockGeoModel<>(new ResourceLocation(RGRBillionaire.MODID, "miner_block_entity")) {
                    private static final ResourceLocation IRON_MINER = new ResourceLocation(RGRBillionaire.MODID, "textures/block/iron_miner.png");
                    private static final ResourceLocation GOLD_MINER = new ResourceLocation(RGRBillionaire.MODID, "textures/block/gold_miner.png");
                    private static final ResourceLocation DIAMOND_MINER = new ResourceLocation(RGRBillionaire.MODID, "textures/block/diamond_miner.png");
                    private static final ResourceLocation NETHERITE_MINER = new ResourceLocation(RGRBillionaire.MODID, "textures/block/netherite_miner.png");
                    private static final ResourceLocation OMNI_MINER = new ResourceLocation(RGRBillionaire.MODID, "textures/block/omni_miner.png");
                    @Override
                    public ResourceLocation getTextureResource(CryptoMinerBlockEntity animatable) {
                        return switch (animatable.amount) {
                            case 3 -> IRON_MINER;
                            case 5 -> GOLD_MINER;
                            case 7 -> DIAMOND_MINER;
                            case 10 -> NETHERITE_MINER;
                            case 1000 -> OMNI_MINER;
                            default -> super.getTextureResource(animatable);
                        };
                    }

                }
        ));
    }

    @SubscribeEvent
    public static void initClient(FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(BlockInit.UNBREAKABLE_IRON_BARS.get(), RenderType.cutout());
        ItemProperties.register(ItemInit.DOLLAR_FISHING_ROD.get(), new ResourceLocation("dollar"), (p_174585_, p_174586_, p_174587_, p_174588_) -> {
            if (p_174587_ == null) {
                return 0.0F;
            } else {
                boolean flag = p_174587_.getMainHandItem() == p_174585_;
                boolean flag1 = p_174587_.getOffhandItem() == p_174585_;
                if (p_174587_.getMainHandItem().getItem() instanceof DollarFishingPoleItem) {
                    flag1 = false;
                }
                return (flag || flag1) && p_174587_ instanceof Player && ((Player)p_174587_).fishing != null ? 1.0F : 0.0F;
            }
        });
        MenuScreens.register(MenuInit.BUTLER_MENU.get(), ButlerInventoryScreen::new);
        MenuScreens.register(MenuInit.MARKET_MENU.get(), MarketplaceScreen::new);
        MenuScreens.register(MenuInit.MINEBOOK_SCREEN.get(), MinebookScreen::new);
        MenuScreens.register(MenuInit.TAX_FORUM_SCREEN.get(), TaxForumScreen::new);
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

    @SubscribeEvent
    public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
        MoneyExplosionGuiOverlay.initializeGuiOverlays();
        for (int i = 0; i < MoneyExplosionGuiOverlay.INSTANCES.length; i++) {
            event.registerAboveAll("money_explosion_" + i, MoneyExplosionGuiOverlay.INSTANCES[i]);
        }
        event.registerAboveAll("confetti", ConfettiGuiOverlay.INSTANCE);
    }

    private static <T extends IHasGeoRenderer & GeoAnimatable> void createSimpleMorphRenderer(Morph morph, String name, T animatable, float scale) {
        MorphRenderers.registerPlayerMorphRenderer(morph, context -> new GeoPlayerRenderer<>(context, new SimpleGeoPlayerModel<>(RGRBillionaire.MODID, name) {
            @Override
            public ResourceLocation getTextureResource(T animatable1, @Nullable AbstractClientPlayer player) {
                return new ResourceLocation(RGRBillionaire.MODID, "textures/entity/" + name + ".png");
            }
        }, animatable) {

            @Override
            public void render(AbstractClientPlayer player, T animatable1, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
                if (player.hasEffect(MobEffects.INVISIBILITY))
                    return;
                poseStack.pushPose();
                poseStack.scale(scale, scale, scale);
                if (player.getVehicle() != null) {
                    poseStack.translate(0, 0.6, 0);
                    super.render(player, animatable1, entityYaw, partialTick, poseStack, bufferSource, packedLight);
                }
                else super.render(player, animatable1, entityYaw, partialTick, poseStack, bufferSource, packedLight);
                poseStack.popPose();
            }
        });
    }

    private static <T extends IHasGeoRenderer & GeoAnimatable> void createAdvancedMorphRenderer(Morph morph, String name, T animatable, float scale) {
        MorphRenderers.registerPlayerMorphRenderer(morph, context -> {
            var renderer = new AdvancedGeoPlayerRenderer<>(context, new AdvancedGeoPlayerModel<>(RGRBillionaire.MODID, name) {
                @Override
                public ResourceLocation getTextureResource(T animatable, @Nullable AbstractClientPlayer player) {
                    return new ResourceLocation(RGRBillionaire.MODID, "textures/entity/" + name + ".png");
                }
            }, animatable, scale, true, false) {

                @Override
                public void render(AbstractClientPlayer player, T animatable1, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
                    if (player.hasEffect(MobEffects.INVISIBILITY) || player.getVehicle() instanceof RocketEntity)
                        return;
                   super.render(player, animatable1, entityYaw, partialTick, poseStack, bufferSource, packedLight);
                }
            };
            renderer.addRenderLayer(new GeoPlayerArmorLayer<>(renderer) {
                @Override
                public @Nullable VertexConsumer renderRecursivelyPre(GeoBone bone, PoseStack poseStack, AbstractClientPlayer entity, T animatable, VertexConsumer vertexConsumer, int packedLight, int overlay, float red, float green, float blue, float alpha) {
                    var armorData = this.armorBoneMap.get(bone.getName());
                    if (!armorData.isEmpty()) {
                        for (var armorPair : armorData) {
                            GeoBone armorBone = armorPair.getLeft();
                            if (armorBone.getName().equals("chestplate")) {
                                GeoBone fireBone = BillionaireUtils.getChildBoneOfName("fire", armorBone);
                                if (fireBone != null) {
                                    fireBone.setHidden(!entity.getAbilities().flying);
                                }
                            }
                        }
                    }
                    return super.renderRecursivelyPre(bone, poseStack, entity, animatable, vertexConsumer, packedLight, overlay, red, green, blue, alpha);
                }
            });
            return renderer;
        });
    }

    private static <T extends IHasGeoRenderer & GeoAnimatable> void createBillionaireMorph(Morph morph, String name, T animatable, float scale) {
        MorphRenderers.registerPlayerMorphRenderer(morph, context -> {
            var renderer = new AdvancedGeoPlayerRenderer<>(context, new AdvancedGeoPlayerModel<>(RGRBillionaire.MODID, name) {
                @Override
                public ResourceLocation getTextureResource(T animatable, @Nullable AbstractClientPlayer player) {
                    return new ResourceLocation(RGRBillionaire.MODID, "textures/entity/" + name + ".png");
                }

                @Override
                public void setupBones() {
                    super.setupBones();
                    this.body = getAnimationProcessor().getBone("body2");
                }

                @Override
                public void setupHeadAnim(AbstractClientPlayer pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
                    boolean flag = pEntity.getFallFlyingTicks() > 4;
                    boolean flag1 = pEntity.isVisuallySwimming();
                    this.head.setRotY(-pNetHeadYaw * ((float) Math.PI / 180F));
                    if (flag) {
                        this.head.setRotX(-(-(float) Math.PI / 4F));
                    }
                    else if (this.swimAmount > 0.0F) {
                        if (flag1) {
                            this.head.setRotX(-this.rotlerpRad(this.swimAmount, this.head.getRotX(), (-(float) Math.PI / 4F)));
                        } else {
                            this.head.setRotX(-this.rotlerpRad(this.swimAmount, this.head.getRotX(), pHeadPitch * ((float) Math.PI / 180F)));
                        }
                    }
                    else {
                        this.head.setRotX(pHeadPitch * ((float) Math.PI / 180F));
                    }
                }

                @Override
                public void setupAnim(AbstractClientPlayer pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
                    if (this.crouching) {
                        this.body.setRotX(0.5F);
                        this.body.setPosY(0F);
                    }
                    else {
                        this.body.setRotX(0.0F);
                        this.rightLeg.setPosZ(0.0F);
                        this.leftLeg.setPosZ(0.0F);
                        this.body.setPosY(0.0F);
                    }
                }
            }, animatable, scale, true, false) {

                @Override
                public void render(AbstractClientPlayer player, T animatable1, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
                    if (player.hasEffect(MobEffects.INVISIBILITY) || player.getVehicle() instanceof RocketEntity)
                        return;
                    if (player.getVehicle() != null) {
                        poseStack.pushPose();
                        poseStack.translate(0, 0.6, 0);
                        super.render(player, animatable1, entityYaw, partialTick, poseStack, bufferSource, packedLight);
                        poseStack.popPose();
                    }
                    else super.render(player, animatable1, entityYaw, partialTick, poseStack, bufferSource, packedLight);
                }
            };
            renderer.getEntityRenderLayers().clear();
            renderer.addRenderLayer(new GeoPlayerArmorLayer<>(renderer) {
                @Override
                public @Nullable VertexConsumer renderRecursivelyPre(GeoBone bone, PoseStack poseStack, AbstractClientPlayer entity, T animatable, VertexConsumer vertexConsumer, int packedLight, int overlay, float red, float green, float blue, float alpha) {
                    var armorData = this.armorBoneMap.get(bone.getName());
                    if (!armorData.isEmpty()) {
                        for (var armorPair : armorData) {
                            GeoBone armorBone = armorPair.getLeft();
                            if (armorBone.getName().equals("chestplate")) {
                                GeoBone fireBone = BillionaireUtils.getChildBoneOfName("fire", armorBone);
                                if (fireBone != null) {
                                    fireBone.setHidden(!entity.getAbilities().flying);
                                }
                            }
                        }
                    }
                    var morph = MorphHolderAttacher.getCurrentMorphUnwrap(entity);
                    if (morph != null) {
                        if (morph == MorphInit.THE_BILLIONAIRE.get()) {
                            poseStack.pushPose();
                            poseStack.translate(0, 1, -0.78);
                            VertexConsumer consumer = super.renderRecursivelyPre(bone, poseStack, entity, animatable, vertexConsumer, packedLight, overlay, red, green, blue, alpha);
                            poseStack.popPose();
                            return consumer;
                        }
                    }
                    return super.renderRecursivelyPre(bone, poseStack, entity, animatable, vertexConsumer, packedLight, overlay, red, green, blue, alpha);
                }
            });
            return renderer;
        });
    }
}
