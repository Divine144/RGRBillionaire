package com.divinity.hmedia.rgrbillionaire.entity;

import com.divinity.hmedia.rgrbillionaire.cap.ButlerGlobalLevelHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.entity.ai.ButlerMineBlockGoal;
import com.divinity.hmedia.rgrbillionaire.menu.ButlerInventoryMenu;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

public class AIRoboButlerEntity extends PathfinderMob implements GeoEntity, ContainerListener, HasCustomInventoryScreen {

    private ItemStack tool = new ItemStack(Items.DIAMOND_PICKAXE);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation MINE = RawAnimation.begin().thenLoop("mine");
    protected static final RawAnimation STANDBY = RawAnimation.begin().thenLoop("standby");
    protected static final RawAnimation GOING_STANDBY = RawAnimation.begin().thenLoop("going_standby");
    protected static final RawAnimation LEAVING_STANDBY = RawAnimation.begin().thenLoop("leaving_standby");
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(AIRoboButlerEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> DATA_MINING = SynchedEntityData.defineId(AIRoboButlerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SIT = SynchedEntityData.defineId(AIRoboButlerEntity.class, EntityDataSerializers.BOOLEAN);
    public static final int INV_CHEST_COUNT = 17;
    protected SimpleContainer inventory;
    private LazyOptional<?> itemHandler = null;

    public AIRoboButlerEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.createInventory();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        if (nbt.contains("Owner"))
            this.entityData.set(DATA_OWNERUUID_ID, Optional.of(nbt.getUUID("Owner")));
        tool = ItemStack.of(nbt.getCompound("tool"));
        this.createInventory();
        // Inventory
        ListTag listtag = nbt.getList("Items", 10);
        for(int i = 0; i < listtag.size(); ++i) {
            CompoundTag compoundtag = listtag.getCompound(i);
            int j = compoundtag.getByte("Slot") & 255;
            if (j >= 2 && j < this.inventory.getContainerSize()) {
                this.inventory.setItem(j, ItemStack.of(compoundtag));
            }
        }
        super.readAdditionalSaveData(nbt);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        if (this.getOwnerUUID() != null)
            nbt.putUUID("Owner", this.getOwnerUUID());
        nbt.put("tool", getTool().save(new CompoundTag()));
        // Inventory
        ListTag listtag = new ListTag();
        for(int i = 2; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (!itemstack.isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte)i);
                itemstack.save(compoundtag);
                listtag.add(compoundtag);
            }
        }
        nbt.put("Items", listtag);
        super.addAdditionalSaveData(nbt);
    }

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.core.Direction facing) {
        if (this.isAlive() && capability == ForgeCapabilities.ITEM_HANDLER && itemHandler != null)
            return itemHandler.cast();
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (itemHandler != null) {
            LazyOptional<?> oldHandler = itemHandler;
            itemHandler = null;
            oldHandler.invalidate();
        }
    }

    public boolean hasInventoryChanged(Container pInventory) {
        return this.inventory != pInventory;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
        this.entityData.define(DATA_MINING, false);
        this.entityData.define(DATA_SIT, true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ButlerMineBlockGoal(this, 0.8D, 15, 15, state -> !state.is(Blocks.BEDROCK) && state.isSolid()));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.5D, 0.5F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !AIRoboButlerEntity.this.isSitting();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !AIRoboButlerEntity.this.isSitting();
            }
        });
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !AIRoboButlerEntity.this.getMining();
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Move", this::animationController));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (pPlayer.isSecondaryUseActive()) {
            this.setSitting(!this.isSitting());
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        this.openCustomInventoryScreen(pPlayer);
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public void openCustomInventoryScreen(Player pPlayer) {
        if (!this.level().isClientSide && pPlayer instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer,
                    new SimpleMenuProvider((id, inv, player) -> new ButlerInventoryMenu(id, inv, this), Component.literal("Butler")),
                    buf -> buf.writeInt(this.getId()));
        }
    }

    @Override
    public void containerChanged(Container pContainer) {
        this.updateContainerEquipment();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        return !pSource.is(DamageTypes.GENERIC_KILL) && !pSource.is(DamageTypes.FELL_OUT_OF_WORLD);
    }

    @Override
    public void die(DamageSource pDamageSource) {
        ButlerGlobalLevelHolderAttacher.getGlobalLevelCapability(this.level()).ifPresent(cap -> {
            for(int i = 2; i < this.inventory.getContainerSize(); ++i) {
                cap.putItems(i, this.inventory.getItem(i));
            }
        });
        super.die(pDamageSource);
    }

    @Override
    @NotNull
    @MethodsReturnNonnullByDefault
    public SlotAccess getSlot(int pSlot) {
        return pSlot == 499 ? new SlotAccess() {
            public ItemStack get() {
                return ItemStack.EMPTY;
            }

            public boolean set(ItemStack stack) {
                return stack.is(Items.GOLDEN_APPLE);
            }
        } : super.getSlot(pSlot);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        createInventory();
        ButlerGlobalLevelHolderAttacher.getGlobalLevelCapability(pLevel.getLevel()).ifPresent(cap -> {
            if (!cap.getInventoryMap().isEmpty()) {
                for (int i = 2; i < this.inventory.getContainerSize(); ++i) {
                    ItemStack itemstack = cap.getInventoryMap().get(i);
                    if (itemstack != null && !itemstack.isEmpty()) {
                        this.inventory.setItem(i, itemstack.copy());
                    }
                }
            }
        });
        return pSpawnData;
    }

    protected <E extends AIRoboButlerEntity> PlayState animationController(final AnimationState<E> event) {
        if (event.isMoving()) {
            return event.setAndContinue(WALK);
        }
        return event.setAndContinue(IDLE);
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public void setMining(boolean isMining) {
        entityData.set(DATA_MINING, isMining);
    }

    public void setSitting(boolean isSitting) {
        entityData.set(DATA_SIT, isSitting);
    }

    public boolean isSitting() {
        return entityData.get(DATA_SIT);
    }

    public boolean getMining() {
        return entityData.get(DATA_MINING);
    }

    public void dismissButler() {
        ButlerGlobalLevelHolderAttacher.getGlobalLevelCapability(this.level()).ifPresent(cap -> {
            for(int i = 2; i < this.inventory.getContainerSize(); ++i) {
                cap.putItems(i, this.inventory.getItem(i));
            }
        });
        discard();
    }

    public ItemStack getTool() {
        return this.tool;
    }

    protected void createInventory() {
        SimpleContainer simplecontainer = this.inventory;
        this.inventory = new SimpleContainer(this.getInventorySize());
        if (simplecontainer != null) {
            simplecontainer.removeListener(this);
            int i = Math.min(simplecontainer.getContainerSize(), this.inventory.getContainerSize());

            for(int j = 0; j < i; ++j) {
                ItemStack itemstack = simplecontainer.getItem(j);
                if (!itemstack.isEmpty()) {
                    this.inventory.setItem(j, itemstack.copy());
                }
            }
        }

        this.inventory.addListener(this);
        this.updateContainerEquipment();
        this.itemHandler = LazyOptional.of(() -> new InvWrapper(this.inventory));
    }

    protected void updateContainerEquipment() {
        if (!this.level().isClientSide) {

        }
    }

    @Nullable
    public Player getOwner() {
        try {
            UUID uuid = this.getOwnerUUID();
            return uuid == null ? null : this.level().getPlayerByUUID(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID pUuid) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(pUuid));
    }

    public int getInventorySize() {
        return INV_CHEST_COUNT;
    }

    public int getInventoryColumns() {
        return 5;
    }
}
