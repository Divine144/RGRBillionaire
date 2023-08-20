package com.divinity.hmedia.rgrbillionaire.item;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.client.renderer.GoldenJetpackArmorRenderer;
import com.divinity.hmedia.rgrbillionaire.init.AbilityInit;
import com.divinity.hmedia.rgrbillionaire.init.ItemInit;
import com.divinity.hmedia.rgrbillionaire.init.SkillInit;
import dev._100media.hundredmediaabilities.capability.AbilityHolderAttacher;
import dev._100media.hundredmediaabilities.capability.MarkerHolderAttacher;
import dev._100media.hundredmediaabilities.init.HMAMarkerInit;
import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
import dev._100media.hundredmediageckolib.item.armor.IGeoSeparatedEntityArmorItem;
import net.minecraft.client.CameraType;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class GoldenJetpackArmorItem extends ArmorItem implements GeoItem, IGeoSeparatedEntityArmorItem {

    protected final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation opening = RawAnimation.begin().thenPlayAndHold("opening");
    private final RawAnimation idleAndRun = RawAnimation.begin().thenLoop("idle/run");
    private boolean hasPlayedOpening = false;

    private static final ResourceLocation ARMOR_LOCATION = new ResourceLocation(RGRBillionaire.MODID, "geo/item/armor/golden_jetpack.geo.json");
    private static final ResourceLocation ARMOR_TEXTURE_LOCATION = new ResourceLocation(RGRBillionaire.MODID, "textures/item/armor/golden_jetpack.png");

    public GoldenJetpackArmorItem(AnimatedItemProperties properties) {
        super(ArmorMaterials.NETHERITE, Type.CHESTPLATE, properties);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null)
                    this.renderer = new GoldenJetpackArmorRenderer();
                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }
        });
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if (pEntity instanceof ServerPlayer player) {
            if (player.getItemBySlot(EquipmentSlot.CHEST).is(ItemInit.GOLDEN_JETPACK.get())) {
                MarkerHolderAttacher.getMarkerHolder(player).ifPresent(cap -> {
                    if (!cap.hasMarker(HMAMarkerInit.FLIGHT.get())) {
                        cap.addMarker(HMAMarkerInit.FLIGHT.get(), true);
                        AbilityHolderAttacher.getAbilityHolder(player).ifPresent(a -> {
                            SkillInit.unlockAbility(player, AbilityInit.GOLDEN_JETPACK.get());
                        });
                    }
                });
            }
            else {
                MarkerHolderAttacher.getMarkerHolder(player).ifPresent(cap -> {
                    if (cap.hasMarker(HMAMarkerInit.FLIGHT.get())) {
                        cap.removeMarker(HMAMarkerInit.FLIGHT.get(), true);
                        AbilityHolderAttacher.getAbilityHolder(player).ifPresent(a -> {
                            SkillInit.removeAbility(player, AbilityInit.GOLDEN_JETPACK.get());
                        });
                    }
                });
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event -> event.setAndContinue(idleAndRun)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    @Override
    public @Nullable <T extends GeoAnimatable> BakedGeoModel getArmorModel(EquipmentSlot slot, ItemStack itemStack, CameraType cameraType, LivingEntity entity, T animatable) {
        return GeckoLibCache.getBakedModels().get(ARMOR_LOCATION);
    }

    @Override
    public @Nullable <T extends GeoAnimatable> ResourceLocation getTextureLocation(EquipmentSlot slot, ItemStack itemStack, CameraType cameraType, LivingEntity entity, T animatable) {
        return ARMOR_TEXTURE_LOCATION;
    }

    @Override
    public @Nullable <T extends GeoAnimatable> List<String> getRootArmorBones(EquipmentSlot slot, ItemStack itemStack, CameraType cameraType, LivingEntity entity, T animatable) {
        return List.of("armorBody");
    }
}
