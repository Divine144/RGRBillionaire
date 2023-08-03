package com.divinity.hmedia.rgrbillionaire.cap;

import dev._100media.capabilitysyncer.core.ItemStackCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class CannonHolder extends ItemStackCapability {

    private int cycle;

    protected CannonHolder(ItemStack itemStack) {
        super(itemStack);
        cycle = 0;
    }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("cycle", this.cycle);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        cycle = nbt.getInt("cycle");
    }

    public int getCycle() {
        return cycle;
    }

    public void cycleSelection() {
        if (++cycle > 2) {
            cycle = 0;
        }
    }
}
