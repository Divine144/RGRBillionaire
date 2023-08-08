package com.divinity.hmedia.rgrbillionaire.entity.special;

import com.divinity.hmedia.rgrbillionaire.cap.MoneyHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.init.ItemInit;
import com.divinity.hmedia.rgrbillionaire.menu.offer.CustomMerchantOffer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DummyMerchant implements Merchant {
    private final Player source;
    private MerchantOffers offers = new MerchantOffers();
    private int xp;
    private boolean shouldListSellItems;

    public DummyMerchant(Player pSource, List<MerchantOffer> offers) {
        this.source = pSource;
        offers.forEach(offer -> {
            if (offer instanceof CustomMerchantOffer customMerchantOffer) {
                if (offer.getResult().getItem() == ItemInit.MONEY.get()) {
                    ItemStack stack = new ItemStack(ItemInit.MONEY.get());
                    MoneyHolderAttacher.getItemStackCapability(offer.getResult()).ifPresent(oH -> {
                        MoneyHolderAttacher.getItemStackCapability(stack).ifPresent(h -> h.setAmount(oH.getAmount()));
                    });
                    var customOffer = new CustomMerchantOffer(stack, offer.getCostA(), offer.getMaxUses(), offer.getXp(), offer.getPriceMultiplier());
                    if (customMerchantOffer.hasSellOffer()) {
                        customOffer.markSellOffer();
                    }
                    if (customOffer.getResult().getItem() instanceof EnchantedBookItem) {
                        customOffer.setNextSelection(() -> {
                            var map = EnchantmentHelper.getEnchantments(customOffer.getResult());
                            var enchantment1 = map.keySet().toArray()[0];
                            var level = map.values().toArray()[0];
                            if (enchantment1 instanceof Enchantment e && level instanceof Integer i) {
                                ItemStack book1 = new ItemStack(Items.ENCHANTED_BOOK);
                                if (i + 1 > e.getMaxLevel()) {
                                    i = e.getMinLevel();
                                }
                                else i += 1;
                                EnchantedBookItem.addEnchantment(book1, new EnchantmentInstance(e, i));
                                int finalI = i;
                                ItemStack moneyStack = customOffer.getCostA().copy();
                                MoneyHolderAttacher.getItemStackCapability(moneyStack).ifPresent(h -> h.setAmount(finalI * 1000));
                                customOffer.setCostA(moneyStack);
                                customOffer.setResult(book1);
                                return book1;
                            }
                            return ItemStack.EMPTY;
                        });
                    }
                    else if (customOffer.getResult().getItem() instanceof PotionItem) {
                        customOffer.setNextSelection(() -> {
                            if (customOffer.getResult().getItem() instanceof PotionItem) {
                                ItemStack lastStack = customOffer.getResult().copy();
                                Potion lastPotion = PotionUtils.getPotion(lastStack);
                                if (!lastPotion.getEffects().isEmpty()) {
                                    var oldInstance = lastPotion.getEffects().get(0);
                                    boolean shouldBeLong = false, shouldBeStrong = false;
                                    if (oldInstance.getAmplifier() < 1 && oldInstance.getDuration() < 4800 && (oldInstance.getDuration() != 1800 || lastPotion.getName("").contains("slow_falling"))) {
                                        shouldBeLong = true;
                                    }
                                    else if (oldInstance.getAmplifier() < 1 && !lastPotion.getName("").contains("strong")) {
                                        shouldBeStrong = true;
                                    }

                                    Item potionType = lastStack.getItem();

                                    if (lastStack.is(Items.POTION)) {
                                        if (!shouldBeLong && !shouldBeStrong) {
                                            potionType = Items.SPLASH_POTION;
                                        }
                                    }
                                    else if (lastStack.is(Items.SPLASH_POTION)) {
                                        if (!shouldBeLong && !shouldBeStrong) {
                                            potionType = Items.LINGERING_POTION;
                                        }
                                    }
                                    else {
                                        if (!shouldBeLong && !shouldBeStrong) {
                                            potionType = Items.POTION;
                                        }
                                    }
                                    String potName = lastPotion.getName("");
                                    if (shouldBeLong) {
                                        potName = lastPotion.getName("long_");
                                    }
                                    else if (shouldBeStrong) {
                                        potName = lastPotion.getName("strong_");
                                    }
                                    ItemStack pot = PotionUtils.setPotion(new ItemStack(potionType), Potion.byName(potName));
                                    if (PotionUtils.getPotion(pot).getEffects().isEmpty()) {
                                        pot = PotionUtils.setPotion(new ItemStack(potionType), Potion.byName(lastPotion.getName("strong_")));
                                        if (PotionUtils.getPotion(pot).getEffects().isEmpty()) {
                                            if (lastStack.is(Items.POTION)) {
                                                potionType = Items.SPLASH_POTION;
                                            }
                                            else if (lastStack.is(Items.SPLASH_POTION)) {
                                                potionType = Items.LINGERING_POTION;
                                            }
                                            else {
                                                potionType = Items.POTION;
                                            }
                                            pot = PotionUtils.setPotion(new ItemStack(potionType), Potion.byName(lastPotion.getName("")));
                                        }
                                    }
                                    int total = 1;
                                    if (!lastPotion.getEffects().isEmpty()) {
                                        MobEffectInstance instance1 = lastPotion.getEffects().get(0);
                                        total = 1000 + (instance1.getDuration() >= 4800 && !pot.is(Items.LINGERING_POTION) ? 100 : 0) + (instance1.getAmplifier() > 0 ? 100 : 0);
                                        if (pot.is(Items.LINGERING_POTION)) {
                                            total += 1000;
                                        }
                                        else if (pot.is(Items.SPLASH_POTION)) {
                                            total += 100;
                                        }
                                    }
                                    int finalAmount = total;

                                    ItemStack moneyStack = customOffer.getCostA().copy();
                                    MoneyHolderAttacher.getItemStackCapability(moneyStack).ifPresent(h -> h.setAmount(finalAmount));
                                    customOffer.setCostA(moneyStack);
                                    customOffer.setResult(pot);
                                    return pot;
                                }
                            }
                            return ItemStack.EMPTY;
                        });
                    }
                    this.offers.add(customOffer); // Buy Trades
                }
            }
        });
    }

    public Player getTradingPlayer() {
        return this.source;
    }

    public void setTradingPlayer(@Nullable Player pPlayer) {
    }

    public MerchantOffers getOffers() {
        return offers;
    }

    @Nullable
    public CustomMerchantOffer getOfferAt(int index) {
        return offers.get(index) instanceof CustomMerchantOffer offer ? offer : null;
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
