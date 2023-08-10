package com.divinity.hmedia.rgrbillionaire.item;

import com.divinity.hmedia.rgrbillionaire.client.renderer.GoldenJetpackArmorRenderer;
import com.divinity.hmedia.rgrbillionaire.init.AbilityInit;
import com.divinity.hmedia.rgrbillionaire.init.ItemInit;
import com.divinity.hmedia.rgrbillionaire.init.SkillInit;
import dev._100media.hundredmediaabilities.capability.AbilityHolderAttacher;
import dev._100media.hundredmediaabilities.capability.MarkerHolderAttacher;
import dev._100media.hundredmediaabilities.init.HMAMarkerInit;
import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
import net.minecraft.client.model.HumanoidModel;
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
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class GoldenJetpackArmorItem extends ArmorItem implements GeoItem {

    protected final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation opening = RawAnimation.begin().thenPlayAndHold("opening");
    private final RawAnimation idleAndRun = RawAnimation.begin().thenLoop("idle/run");
    private boolean hasPlayedOpening = false;

    public GoldenJetpackArmorItem(AnimatedItemProperties properties) {
        super(ArmorMaterials.NETHERITE, Type.CHESTPLATE, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null)
                    this.renderer = new GoldenJetpackArmorRenderer();
                // This prepares our GeoArmorRenderer for the current render frame.
                // These parameters may be null however, so we don't do anything further with them
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
}
