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

    public int getProductionRate() {
        var holder = MarkerHolderAttacher.getMarkerHolderUnwrap(this.entity);
        if (holder != null && holder.hasMarker(MarkerInit.NO_ADDED_PRODUCTION_RATE.get())) {
            return 0;
        }
        return this.productionRate;
    }

    public void increaseProductionRate(int staggerAmount) {
        productionRate += staggerAmount;
    }

    public void decreaseProductionRate(int staggerAmount) {
        productionRate -= staggerAmount;
    }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("money", this.money);
        tag.putInt("productionRate", this.productionRate);
        tag.putInt("moneyCap", this.moneyCap);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        this.money = nbt.getInt("money");
        this.productionRate = nbt.getInt("productionRate");
        this.moneyCap = nbt.getInt("moneyCap");
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
