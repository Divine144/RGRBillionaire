package com.divinity.hmedia.rgrbillionaire.mixin;

import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Projectile.class)
public interface ProjectileAccessor {

    @Accessor("hasBeenShot")
    boolean getHasBeenShot();

    @Accessor("hasBeenShot")
    void setHasBeenShot(boolean hasBeenShot);

    @Accessor("leftOwner")
    boolean getLeftOwner();

    @Accessor("leftOwner")
    void setLeftOwner(boolean leftOwner);
}
