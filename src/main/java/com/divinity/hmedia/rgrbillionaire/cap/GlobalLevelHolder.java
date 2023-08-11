package com.divinity.hmedia.rgrbillionaire.cap;

import com.divinity.hmedia.rgrbillionaire.network.NetworkHandler;
import com.google.common.collect.Maps;
import dev._100media.capabilitysyncer.core.GlobalLevelCapability;
import dev._100media.capabilitysyncer.network.LevelCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleLevelCapabilityStatusPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;

public class GlobalLevelHolder extends GlobalLevelCapability {

    private final Map<Integer, ItemStack> inventoryMap = new HashMap<>();
    private int productionRate;

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

    public void increaseProductionRate(int staggerAmount) {
        int oldAmount = productionRate;
        productionRate += staggerAmount;
        if (productionRate < 0) productionRate = 0;
        if (oldAmount != productionRate) updateTracking();
    }

    public void decreaseProductionRate(int staggerAmount) {
        increaseProductionRate(-staggerAmount);
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
    }

    @Override
    public LevelCapabilityStatusPacket createUpdatePacket() {
        return new SimpleLevelCapabilityStatusPacket(GlobalLevelHolderAttacher.EXAMPLE_GLOBAL_LEVEL_CAPABILITY_RL, this);
    }

    @Override
    public SimpleChannel getNetworkChannel() {
        return NetworkHandler.INSTANCE;
    }
}
