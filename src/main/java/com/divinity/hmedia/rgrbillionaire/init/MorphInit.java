package com.divinity.hmedia.rgrbillionaire.init;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import dev._100media.hundredmediamorphs.HundredMediaMorphsMod;
import dev._100media.hundredmediamorphs.morph.Morph;
import dev._100media.hundredmediamorphs.skin.SkinType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MorphInit {
    public static final DeferredRegister<Morph> MORPHS = DeferredRegister.create(new ResourceLocation(HundredMediaMorphsMod.MODID, "morphs"), RGRBillionaire.MODID);

    public static final RegistryObject<Morph> BROKE_BABY = MORPHS.register("broke_baby", () -> new Morph(new Morph.Properties<>()
            .maxHealth(10)
            .swingDuration(7)
            .dimensions(0.75f, 0.75f)
            .eyeHeight(0.85f)
            .morphedTo(entity -> {
                BillionaireHolderAttacher.getHolder(entity).ifPresent(h -> h.setMoneyCap(1_000));
            })
    ));
    public static final RegistryObject<Morph> TIGHT_BUDGET_TEEN = MORPHS.register("tight_budget_teen", () -> new Morph(new Morph.Properties<>()
            .maxHealth(30)
            .swingDuration(7)
            .dimensions(0.6f, 1.8f)
            .morphedTo(entity -> {
                BillionaireHolderAttacher.getHolder(entity).ifPresent(h -> h.setMoneyCap(75_000));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 0, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 0, false, false, false));
            })
            .demorph(entity -> {
                entity.removeEffect(MobEffects.MOVEMENT_SPEED);
                entity.removeEffect(MobEffects.DAMAGE_BOOST);
            })
    ));
    public static final RegistryObject<Morph> MIDDLE_CLASS_MAN = MORPHS.register("middle_class_man", () -> new Morph(new Morph.Properties<>()
            .maxHealth(50)
            .swingDuration(7)
            .dimensions(0.6f, 1.8f)
            .morphedTo(entity -> {
                BillionaireHolderAttacher.getHolder(entity).ifPresent(h -> h.setMoneyCap(500_000));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 1, false, false, false));
            })
            .demorph(entity -> {
                entity.removeEffect(MobEffects.MOVEMENT_SPEED);
                entity.removeEffect(MobEffects.DAMAGE_BOOST);
            })
    ));
    public static final RegistryObject<Morph> MULTI_MILLIONAIRE = MORPHS.register("multi_millionaire", () -> new Morph(new Morph.Properties<>()
            .maxHealth(70)
            .swingDuration(7)
            .dimensions(0.6f, 1.8f)
            .morphedTo(entity -> {
                BillionaireHolderAttacher.getHolder(entity).ifPresent(h -> h.setMoneyCap(10_000_000));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 2, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 2, false, false, false));
            })
            .demorph(entity -> {
                entity.removeEffect(MobEffects.MOVEMENT_SPEED);
                entity.removeEffect(MobEffects.DAMAGE_BOOST);
            })
    ));
    public static final RegistryObject<Morph> THE_BILLIONAIRE = MORPHS.register("the_billionaire", () -> new Morph(new Morph.Properties<>()
            .maxHealth(100)
            .swingDuration(7)
            .dimensions(2, 5)
            .morphedTo(entity -> {
                BillionaireHolderAttacher.getHolder(entity).ifPresent(h -> h.setMoneyCap(1_000_000_000));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 3, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 3, false, false, false));
                var reachDistance = entity.getAttribute(ForgeMod.BLOCK_REACH.get());
                if (reachDistance != null) {
                    reachDistance.setBaseValue(reachDistance.getAttribute().getDefaultValue() + 3);
                }
            })
            .demorph(entity -> {
                entity.removeEffect(MobEffects.MOVEMENT_SPEED);
                entity.removeEffect(MobEffects.DAMAGE_BOOST);
                var reachDistance = entity.getAttribute(ForgeMod.BLOCK_REACH.get());
                if (reachDistance != null) {
                    reachDistance.setBaseValue(reachDistance.getAttribute().getDefaultValue());
                }
            })
    ));
}
