package com.divinity.hmedia.rgrbillionaire.cap;

import com.divinity.hmedia.rgrbillionaire.network.NetworkHandler;
import com.google.common.collect.Maps;
import dev._100media.capabilitysyncer.core.GlobalLevelCapability;
import dev._100media.capabilitysyncer.network.LevelCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleLevelCapabilityStatusPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;

public class GlobalLevelHolder extends GlobalLevelCapability {

    private final Map<Integer, ItemStack> inventoryMap = new HashMap<>();
    private int productionRate;

    private int rocketTimer = 1200 * 20; // 24000 ticks = 20 minutes which is the length of an MC day

    private int rocketYLevel = 200;

    private int moneyBarX = 0;
    private int moneyBarY = 0;

    private BlockPos initialDestructionPos = BlockPos.ZERO;

    private boolean isDestroying = false;

    private int destroyRadius = 10;

    protected GlobalLevelHolder(Level level) {
        super(level);
    }

    public Map<Integer, ItemStack> getInventoryMap() {
        return Maps.newHashMap(inventoryMap);
    }

    public void putItems(int slot, ItemStack item) {
        inventoryMap.put(slot, item);
    }

    public int getProductionRate() {
        return this.productionRate;
    }

    public int getRocketTimer() {
        return rocketTimer;
    }

    public void setRocketTimer(int timer) {
        this.rocketTimer = timer * 20;
    }

    public int getRocketYLevel() {
        return rocketYLevel;
    }

    public void setRocketYLevel(int rocketYLevel) {
        this.rocketYLevel = rocketYLevel;
    }

    public void increaseProductionRate(int staggerAmount) {
        int oldAmount = productionRate;
        productionRate += staggerAmount;
        if (productionRate < 0) productionRate = 0;
        if (oldAmount != productionRate) this.updateTracking();
    }

    public void decreaseProductionRate(int staggerAmount) {
        increaseProductionRate(-staggerAmount);
    }

    public int getMoneyBarY() {
        return moneyBarY;
    }

    public void setMoneyBarY(int moneyBarY) {
        this.moneyBarY = moneyBarY;
        updateTracking();
    }

    public int getMoneyBarX() {
        return moneyBarX;
    }

    public void setMoneyBarX(int moneyBarX) {
        this.moneyBarX = moneyBarX;
        updateTracking();
    }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag nbt = new CompoundTag();
        ListTag slotsNBT = new ListTag();
        ListTag stacksNBT = new ListTag();
        nbt.putInt("inventoryMapSize", inventoryMap.size());
        inventoryMap.keySet().forEach(id -> slotsNBT.add(IntTag.valueOf(id)));
        inventoryMap.values().forEach(stack -> stacksNBT.add(stack.save(new CompoundTag())));
        nbt.put("slotIds", slotsNBT);
        nbt.put("stacks", stacksNBT);
        nbt.putInt("productionRate", this.productionRate);
        nbt.putInt("rocketTimer", this.rocketTimer);
        nbt.putInt("rocketYLevel", this.rocketYLevel);
        nbt.putInt("moneyBarX", this.moneyBarX);
        nbt.putInt("moneyBarY", this.moneyBarY);
        nbt.put("blockPos", NbtUtils.writeBlockPos(this.initialDestructionPos));
        nbt.putBoolean("isDestroying", this.isDestroying);
        nbt.putInt("destroyRadius", this.destroyRadius);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        int inventoryMapSize = nbt.getInt("inventoryMapSize");
        ListTag slotsNBT = (ListTag) nbt.get("slotIds");
        ListTag stacksNBT = (ListTag) nbt.get("stacks");
        if (slotsNBT != null && stacksNBT != null && inventoryMapSize != 0) {
            for (int i = 0; i < inventoryMapSize; i++) {
                Tag slot = slotsNBT.get(i);
                Tag stack = stacksNBT.get(i);
                if (slot instanceof IntTag intTag && stack instanceof CompoundTag compoundTag) {
                    inventoryMap.put(intTag.getAsInt(), ItemStack.of(compoundTag));
                }
            }
        }
        this.productionRate = nbt.getInt("productionRate");
        this.rocketTimer = nbt.getInt("rocketTimer");
        this.rocketYLevel = nbt.getInt("rocketYLevel");
        this.moneyBarX = nbt.getInt("moneyBarX");
        this.moneyBarY = nbt.getInt("moneyBarY");
        if (nbt.get("blockPos") instanceof CompoundTag tag) {
            this.initialDestructionPos = NbtUtils.readBlockPos(tag);
        }
        this.isDestroying = nbt.getBoolean("isDestroying");
        this.destroyRadius = nbt.getInt("destroyRadius");
    }

    @Override
    public LevelCapabilityStatusPacket createUpdatePacket() {
        return new SimpleLevelCapabilityStatusPacket(GlobalLevelHolderAttacher.EXAMPLE_GLOBAL_LEVEL_CAPABILITY_RL, this);
    }

    @Override
    public SimpleChannel getNetworkChannel() {
        return NetworkHandler.INSTANCE;
    }

    public BlockPos getInitialDestructionPos() {
        return initialDestructionPos;
    }

    public void setInitialDestructionPos(BlockPos initialDestructionPos) {
        this.initialDestructionPos = initialDestructionPos;
    }

    public boolean isDestroying() {
        return isDestroying;
    }

    public void setDestroying(boolean destroying) {
        isDestroying = destroying;
    }

    public int getDestroyRadius() {
        return destroyRadius;
    }

    public void setDestroyRadius(int destroyRadius) {
        this.destroyRadius = destroyRadius;
    }
}
