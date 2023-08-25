package com.divinity.hmedia.rgrbillionaire.mixin;

import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.entity.AIRoboButlerEntity;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SummonCommand.class)
public class SummonCommandMixin {

    @Unique
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed"));
    @Unique
    private static final SimpleCommandExceptionType ERROR_DUPLICATE_UUID = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed.uuid"));
    @Unique
    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(Component.translatable("commands.summon.invalidPosition"));

    @Inject(
            method = "createEntity",
            at = @At(value = "HEAD"),
            cancellable = true

    )
    private static void createEntity(CommandSourceStack pSource, Holder.Reference<EntityType<?>> pType, Vec3 pPos, CompoundTag pTag, boolean pRandomizeProperties, CallbackInfoReturnable<Entity> cir) throws CommandSyntaxException {
        BlockPos blockpos = BlockPos.containing(pPos);
        if (!Level.isInSpawnableBounds(blockpos)) {
            throw INVALID_POSITION.create();
        } else {
            CompoundTag compoundtag = pTag.copy();
            compoundtag.putString("id", pType.key().location().toString());
            ServerLevel serverlevel = pSource.getLevel();
            Entity entity = EntityType.loadEntityRecursive(compoundtag, serverlevel, (p_138828_) -> {
                p_138828_.moveTo(pPos.x, pPos.y, pPos.z, p_138828_.getYRot(), p_138828_.getXRot());
                return p_138828_;
            });
            if (entity == null) {
                throw ERROR_FAILED.create();
            } else {
                if (pRandomizeProperties && entity instanceof Mob mob) {
                    if (mob instanceof AIRoboButlerEntity entity1) {
                        if (pSource.getEntity() instanceof Player player) {
                            BillionaireHolderAttacher.getHolder(player).ifPresent(p -> p.setLinkedEntityID(entity1.getId()));
                        }
                    }
                    ((Mob)entity).finalizeSpawn(pSource.getLevel(), pSource.getLevel().getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.COMMAND, (SpawnGroupData)null, (CompoundTag)null);
                }

                if (!serverlevel.tryAddFreshEntityWithPassengers(entity)) {
                    throw ERROR_DUPLICATE_UUID.create();
                } else {
                    cir.setReturnValue(entity);
                }
            }
        }
        cir.cancel();
    }
}
