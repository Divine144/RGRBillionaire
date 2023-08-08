package com.divinity.hmedia.rgrbillionaire.menu.offer;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.function.Supplier;

public class CustomMerchantOffer extends MerchantOffer {

    private ItemStack baseCostA;
    /** The second input for this offer. */
    private final ItemStack costB;
    /** The output of this offer. */
    private ItemStack result;
    private int uses;
    private final int maxUses;
    private boolean rewardExp = true;
    private int specialPriceDiff;
    private int demand;
    private float priceMultiplier;
    private int xp = 1;

    private boolean sellOffer;

    private Supplier<ItemStack> onScroll = () -> ItemStack.EMPTY;

    public CustomMerchantOffer(ItemStack pBaseCostA, ItemStack pResult, int pMaxUses, int pXp, float pPriceMultiplier) {
        this(pBaseCostA, ItemStack.EMPTY, pResult, pMaxUses, pXp, pPriceMultiplier);
    }

    public CustomMerchantOffer(ItemStack pBaseCostA, ItemStack pCostB, ItemStack pResult, int pMaxUses, int pXp, float pPriceMultiplier) {
        this(pBaseCostA, pCostB, pResult, 0, pMaxUses, pXp, pPriceMultiplier);
    }

    public CustomMerchantOffer(ItemStack pBaseCostA, ItemStack pCostB, ItemStack pResult, int pUses, int pMaxUses, int pXp, float pPriceMultiplier) {
        this(pBaseCostA, pCostB, pResult, pUses, pMaxUses, pXp, pPriceMultiplier, 0);
    }

    public CustomMerchantOffer(ItemStack pBaseCostA, ItemStack pCostB, ItemStack pResult, int pUses, int pMaxUses, int pXp, float pPriceMultiplier, int pDemand) {
        super(pBaseCostA, pCostB, pResult, pUses, pMaxUses, pXp, pPriceMultiplier, pDemand);
        this.baseCostA = pBaseCostA;
        this.costB = pCostB;
        this.result = pResult;
        this.uses = pUses;
        this.maxUses = pMaxUses;
        this.xp = pXp;
        this.priceMultiplier = pPriceMultiplier;
        this.demand = pDemand;
    }

    public ItemStack getBaseCostA() {
        return this.baseCostA;
    }

    public ItemStack getCostA() {
        if (this.baseCostA.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            int i = this.baseCostA.getCount();
            int j = Math.max(0, Mth.floor((float)(i * this.demand) * this.priceMultiplier));
            return this.baseCostA.copyWithCount(Mth.clamp(i + j + this.specialPriceDiff, 1, this.baseCostA.getMaxStackSize()));
        }
    }

    public ItemStack getCostB() {
        return this.costB;
    }

    public ItemStack getResult() {
        return this.result;
    }

    /**
     * Calculates the demand with following formula: demand = demand + uses - maxUses - uses
     */
    public void updateDemand() {
        this.demand = this.demand + this.uses - (this.maxUses - this.uses);
    }

    public ItemStack assemble() {
        return this.result.copy();
    }

    public int getUses() {
        return this.uses;
    }

    public void resetUses() {
        this.uses = 0;
    }

    public int getMaxUses() {
        return this.maxUses;
    }

    public void increaseUses() {
        ++this.uses;
    }

    public int getDemand() {
        return this.demand;
    }

    public void addToSpecialPriceDiff(int pAdd) {
        this.specialPriceDiff += pAdd;
    }

    public void resetSpecialPriceDiff() {
        this.specialPriceDiff = 0;
    }

    public int getSpecialPriceDiff() {
        return this.specialPriceDiff;
    }

    public void setSpecialPriceDiff(int pPrice) {
        this.specialPriceDiff = pPrice;
    }

    public float getPriceMultiplier() {
        return this.priceMultiplier;
    }

    public int getXp() {
        return this.xp;
    }

    public boolean isOutOfStock() {
        return this.uses >= this.maxUses;
    }

    public void setToOutOfStock() {
        this.uses = this.maxUses;
    }

    public boolean needsRestock() {
        return this.uses > 0;
    }

    public boolean shouldRewardExp() {
        return this.rewardExp;
    }

    public ItemStack getNextStack() {
        return onScroll.get();
    }

    public boolean hasSellOffer() {
        return this.sellOffer;
    }

    public CustomMerchantOffer markSellOffer() {
        this.sellOffer = true;
        return this;
    }

    public void setResult(ItemStack stack) {
        this.result = stack.copy();
    }

    public void setCostA(ItemStack stack) {
        this.baseCostA = stack;
    }

    public void setNextSelection(Supplier<ItemStack> supplier) {
        this.onScroll = supplier;
    }

    public Supplier<ItemStack> getNextSelection() {
        return this.onScroll;
    }
}
