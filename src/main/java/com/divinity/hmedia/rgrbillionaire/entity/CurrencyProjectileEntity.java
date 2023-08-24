package com.divinity.hmedia.rgrbillionaire.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.BiConsumer;

public class CurrencyProjectileEntity extends ThrowableProjectile implements GeoEntity {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final int damage;
    private BiConsumer<CurrencyProjectileEntity, HitResult> onHit = (s, h) -> {};

    public CurrencyProjectileEntity(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.damage = 0;
    }

    public CurrencyProjectileEntity(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel, int damage) {
        super(pEntityType, pLevel);
        this.damage = damage;
    }

    public CurrencyProjectileEntity(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel, int damage, BiConsumer<CurrencyProjectileEntity, HitResult> onHit) {
        this(pEntityType, pLevel, damage);
        this.onHit = onHit;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            if (this.getOwner() != null && this.distanceTo(getOwner()) >= 50) {
                discard();
            }
        }
    }

    @Override
    protected void onHit(HitResult pResult) {
        if (pResult instanceof EntityHitResult result && result.getEntity() == this.getOwner()) return;
        super.onHit(pResult);
        if (!level().isClientSide) {
            this.onHit.accept(this, pResult);
            discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!level().isClientSide && pResult.getEntity() != this.getOwner() && pResult.getEntity() instanceof LivingEntity living) {
            living.hurt(this.damageSources().mobProjectile(this, getOwner() instanceof LivingEntity l ? l : null), damage);
        }
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
