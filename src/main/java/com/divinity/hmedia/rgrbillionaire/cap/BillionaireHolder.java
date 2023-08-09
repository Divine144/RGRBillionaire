package com.divinity.hmedia.rgrbillionaire.cap;

import com.divinity.hmedia.rgrbillionaire.init.MarkerInit;
import com.divinity.hmedia.rgrbillionaire.network.NetworkHandler;
import dev._100media.capabilitysyncer.core.EntityCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import dev._100media.hundredmediaabilities.capability.MarkerHolderAttacher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.simple.SimpleChannel;

public class BillionaireHolder extends EntityCapability {

    private int money;
    private int moneyCap;
    private int productionRate;

    private int linkedEntityID = -1;

    protected BillionaireHolder(Entity entity) {
        super(entity);
        moneyCap = 10000;
    }

    public int getMoney() {
        return money;
    }

    public int getMoneyCap() {
        return moneyCap;
    }

    public void setMoneyCap(int moneyCap) {
        this.moneyCap = moneyCap;
        updateTracking();
    }

    public void setMoney(int money) {
        this.money = money;
        updateTracking();
    }

    public void addMoney(int amount) {
        int old = this.money;
        this.money += amount;
        if (money > moneyCap) money = moneyCap;
        else if (money < 0) money = 0;
        if (money != old) updateTracking();
    }

    public int getLinkedEntityID() {
        return linkedEntityID;
    }

    public void setLinkedEntityID(int id) {
        this.linkedEntityID = id;
    }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("money", this.money);
        tag.putInt("productionRate", this.productionRate);
        tag.putInt("moneyCap", this.moneyCap);
        tag.putInt("id", this.linkedEntityID);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        this.money = nbt.getInt("money");
        this.productionRate = nbt.getInt("productionRate");
        this.moneyCap = nbt.getInt("moneyCap");
        this.linkedEntityID = nbt.getInt("id");
    }

    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.entity.getId(), BillionaireHolderAttacher.EXAMPLE_RL, this);
    }

    @Override
    public SimpleChannel getNetworkChannel() {
        return NetworkHandler.INSTANCE;
    }
}
