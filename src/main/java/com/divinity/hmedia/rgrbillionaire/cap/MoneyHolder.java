package com.divinity.hmedia.rgrbillionaire.cap;

import dev._100media.capabilitysyncer.core.ItemStackCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class MoneyHolder extends ItemStackCapability {

    private int amount;

    protected MoneyHolder(ItemStack itemStack) {
        super(itemStack);
        amount = 0;
    }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("amount", this.amount);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        amount = nbt.getInt("amount");
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
