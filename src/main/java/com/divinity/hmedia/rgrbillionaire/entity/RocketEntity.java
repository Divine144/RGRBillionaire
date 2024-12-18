package com.divinity.hmedia.rgrbillionaire.entity;

import com.divinity.hmedia.rgrbillionaire.cap.GlobalLevelHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.init.SoundInit;
import com.divinity.hmedia.rgrbillionaire.util.BillionaireUtils;
import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Date;

public class RocketEntity extends PathfinderMob implements GeoEntity {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private static int CONSTRUCTION_TIME = 1;
    protected static final EntityDataAccessor<Boolean> CAN_TAKEOFF = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> CAUSED_DESTRUCTION = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.BOOLEAN);

    public RocketEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel level) {
            var holder = GlobalLevelHolderAttacher.getGlobalLevelCapabilityUnwrap(level);
            if (holder != null) {
                if (CONSTRUCTION_TIME != holder.getRocketTimer()) {
                    CONSTRUCTION_TIME = holder.getRocketTimer();
                }
            }

            if (this.tickCount == CONSTRUCTION_TIME) {
                entityData.set(CAN_TAKEOFF, true);
            }
            if (this.isVehicle() && entityData.get(CAN_TAKEOFF) && holder != null && this.getY() >= holder.getRocketYLevel()) {
                if (!entityData.get(CAUSED_DESTRUCTION)) {
                    level.setDayTime(13000);
                    level.getAllEntities().forEach(entity -> {
                        if (entity instanceof LivingEntity livingEntity) {
                            if (livingEntity instanceof Player player) {
                                if (!BillionaireUtils.hasAnyMorph(player)) {
                                    MinecraftServer server = level.getServer();
                                    if (!server.isSingleplayer()) {
                                        UserBanList userbanlist = server.getPlayerList().getBans();
                                        GameProfile gameprofile = player.getGameProfile();
                                        if (!userbanlist.isBanned(gameprofile)) {
                                            UserBanListEntry userbanlistentry = new UserBanListEntry(gameprofile, (Date) null, "(Billionaire)", (Date) null, "You are too poor");
                                            userbanlist.add(userbanlistentry);
                                            ServerPlayer serverplayer = server.getPlayerList().getPlayer(gameprofile.getId());
                                            if (serverplayer != null) {
                                                serverplayer.connection.disconnect(Component.translatable("multiplayer.disconnect.banned"));
                                            }
                                        }
                                    }
                                }
                            }
                            else {
                                if (livingEntity != RocketEntity.this) {
                                    livingEntity.kill();
                                }
                            }
                        }
                    });
                    var player = this.getControllingPassenger();
                    if (player instanceof ServerPlayer player1) {
                        player1.sendSystemMessage(Component.literal("Space Rocket: ").withStyle(ChatFormatting.WHITE)
                                .append(Component.literal("Protocol Initiated: Destroy Everything")
                                        .withStyle(ChatFormatting.RED, ChatFormatting.BOLD)));
                        if (!holder.isDestroying()) {
                            holder.setDestroying(true);
                        }
                    }
                    entityData.set(CAUSED_DESTRUCTION, true);
                }
            }
            if (this.isVehicle()) {
                if (this.tickCount % 20 == 0) {
                    level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundInit.ROCKET_READY.get(), SoundSource.PLAYERS, 1, 1);
                }
                Vec3 vector3d1 = this.getDeltaMovement();
                level.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX() - vector3d1.x, this.getY() - 0.6 - vector3d1.y, this.getZ() + 0.6 - vector3d1.z, 5, 0, 0, 0, 0);
            }

        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CAN_TAKEOFF, false);
        this.entityData.define(CAUSED_DESTRUCTION, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("takeOff", entityData.get(CAN_TAKEOFF));
        pCompound.putBoolean("causedKill", entityData.get(CAUSED_DESTRUCTION));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        entityData.set(CAN_TAKEOFF, pCompound.getBoolean("takeOff"));
        entityData.set(CAUSED_DESTRUCTION, pCompound.getBoolean("causedKill"));
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!this.level().isClientSide && pHand == InteractionHand.MAIN_HAND) {
            if (!entityData.get(CAN_TAKEOFF)) {
                if (!this.isVehicle() && pPlayer instanceof ServerPlayer player) {
                    player.sendSystemMessage(Component.literal("Space Rocket: ").withStyle(ChatFormatting.WHITE)
                            .append(Component.literal("Under Construction! %s seconds left".formatted((CONSTRUCTION_TIME - tickCount) / 20))
                                    .withStyle(ChatFormatting.RED, ChatFormatting.BOLD)));
                }
            }
            else {
                if (BillionaireUtils.hasAnyMorph(pPlayer)) {
                    if (BillionaireUtils.hasEnoughMoney(pPlayer, 1_000_000_000)) {
                        pPlayer.sendSystemMessage(Component.literal("Space Rocket: ").withStyle(ChatFormatting.WHITE)
                                .append(Component.literal("Ready For Takeoff!").withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD)));
                        pPlayer.startRiding(this);
                    }
                    else {
                        pPlayer.sendSystemMessage(Component.literal("Space Rocket: ").withStyle(ChatFormatting.WHITE)
                                .append(Component.literal("Access Denied: Too Poor!").withStyle(ChatFormatting.RED, ChatFormatting.BOLD)));
                    }
                }
                else {
                    pPlayer.sendSystemMessage(Component.literal("Space Rocket: ").withStyle(ChatFormatting.WHITE)
                            .append(Component.literal("Access Denied!").withStyle(ChatFormatting.RED, ChatFormatting.BOLD)));
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return passenger instanceof Player && getPassengers().isEmpty();
    }

    @Nullable
    @Override
    public Player getControllingPassenger() {
        return getFirstPassenger() instanceof Player player ? player : null;
    }

    @Override
    public boolean isAlwaysTicking() {
        return true;
    }

    @Override
    public double getPassengersRidingOffset() {
        return getBbHeight();
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isAlive()) {
            LivingEntity livingentity = this.getControllingPassenger();
            if (this.isVehicle() && livingentity != null) {
                this.fallDistance = 0;
                if (this.isControlledByLocalInstance()) {
                    if (entityData.get(CAN_TAKEOFF)) {
                        super.travel(new Vec3(0, 1, 0));
                        this.setDeltaMovement(0, 1, 0);
                    }
                }
                else {
                    this.setDeltaMovement(Vec3.ZERO);
                }
                this.calculateEntityAnimation(false);
                this.tryCheckInsideBlocks();
            }
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        return !pSource.is(DamageTypes.GENERIC_KILL) && !pSource.is(DamageTypes.FELL_OUT_OF_WORLD);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected void registerGoals() {}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Build", this::animationController));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    protected <E extends RocketEntity> PlayState animationController(final AnimationState<E> event) {
        if (event.getData(DataTickets.ENTITY) instanceof RocketEntity entity) {
            if (entity.tickCount < (int) (CONSTRUCTION_TIME * 0.50)) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("1"));
            }
            if (entity.tickCount == (int) (CONSTRUCTION_TIME * 0.50)) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("2"));
            }
            if (entity.tickCount == (int) (CONSTRUCTION_TIME * 0.75)) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("3"));
            }
            if (entity.tickCount == CONSTRUCTION_TIME) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("4"));
            }
        }
        return PlayState.CONTINUE;
    }

    public boolean canTakeOff() {
        return entityData.get(CAN_TAKEOFF);
    }
}

