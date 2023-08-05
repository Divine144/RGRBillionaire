package com.divinity.hmedia.rgrbillionaire.entity.special;

import com.divinity.hmedia.rgrbillionaire.cap.MoneyHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.init.ItemInit;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

import javax.annotation.Nullable;
import java.util.List;

public class DummyMerchant implements Merchant {
    private final Player source;
    private MerchantOffers offers = new MerchantOffers();
    private int xp;

    public DummyMerchant(Player pSource, List<MerchantOffer> offers) {
        this.source = pSource;
        this.offers.addAll(offers); // Buy Trades
        offers.forEach(offer -> {
            if (offer.getResult().getItem() == ItemInit.MONEY.get()) {
                ItemStack stack = new ItemStack(ItemInit.MONEY.get());
                MoneyHolderAttacher.getItemStackCapability(offer.getResult()).ifPresent(oH -> {
                    MoneyHolderAttacher.getItemStackCapability(stack).ifPresent(h -> h.setAmount(oH.getAmount() / 2));
                });
                this.offers.add(new MerchantOffer(stack, offer.getCostA(), offer.getMaxUses(), offer.getXp(), offer.getPriceMultiplier())); // Sell trades
            }
            else this.offers.add(new MerchantOffer(offer.getResult(), offer.getCostA(), offer.getMaxUses(), offer.getXp(), offer.getPriceMultiplier())); // Sell trades
        });
    }

    public Player getTradingPlayer() {
        return this.source;
    }

    public void setTradingPlayer(@Nullable Player pPlayer) {
        ItemStack itemstack = new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 2);
        offers.add(new MerchantOffer(itemstack, new ItemStack(Items.EMERALD), 3, 3, 3));
    }

    public MerchantOffers getOffers() {
        return offers;
    }

    public void overrideOffers(MerchantOffers pOffers) {
        this.offers = pOffers;
    }

    public void notifyTrade(MerchantOffer pOffer) {
        pOffer.increaseUses();
    }

    /**
     * Notifies the merchant of a possible merchant recipe being fulfilled or not. Usually, this is just a sound byte
     * being played depending on whether the suggested {@link net.minecraft.world.item.ItemStack} is not empty.
     */
    public void notifyTradeUpdated(ItemStack pStack) {

    }

    public boolean isClientSide() {
        return this.source.level().isClientSide;
    }

    public int getVillagerXp() {
        return this.xp;
    }

    public void overrideXp(int pXp) {
        this.xp = pXp;
    }

    public boolean showProgressBar() {
        return true;
    }

    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.EXPERIENCE_ORB_PICKUP;
    }
}
