package com.divinity.hmedia.rgrbillionaire.entity;

import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.util.BillionaireUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class StockGraphEntity extends ThrowableProjectile implements GeoEntity {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation GO = RawAnimation.begin().thenLoop("go");
    private float yHeadRot;

    public StockGraphEntity(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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
        super.onHit(pResult);
        if (!level().isClientSide) {
            discard();
        }
    }

    @Override
    public float getYHeadRot() {
        return this.yHeadRot;
    }


    @Override
    public void setYHeadRot(float pRotation) {
        this.yHeadRot = pRotation;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!level().isClientSide && pResult.getEntity() instanceof LivingEntity living) {
            if (living instanceof ServerPlayer player) {
                player.hurtMarked = true;
            }
            if (getOwner() instanceof Player player) {
                living.move(MoverType.PLAYER, player.getLookAngle().multiply(60, 1, 60).add(0, 55, 0));
                living.hurt(this.damageSources().mobProjectile(this, player), 2);
                BillionaireUtils.addMoney(player, 5000);
            }
        }
    }


    @Override
    protected void defineSynchedData() {}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Go", e -> e.setAndContinue(GO)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
