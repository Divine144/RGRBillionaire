package com.divinity.hmedia.rgrbillionaire.util;

import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.cap.MoneyHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.client.screen.MarketplaceScreen;
import com.divinity.hmedia.rgrbillionaire.init.ItemInit;
import com.divinity.hmedia.rgrbillionaire.menu.offer.CustomMerchantOffer;
import com.divinity.hmedia.rgrbillionaire.network.NetworkHandler;
import com.divinity.hmedia.rgrbillionaire.network.serverbound.UpdateMarketOfferPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public final class BillionaireUtils {

    public static List<MerchantOffer> blockOffers;
    public static List<MerchantOffer> gemOffers;
    public static List<MerchantOffer> foodOffers;
    public static List<MerchantOffer> enchantedBookOffers;
    public static List<MerchantOffer> potionAndOtherOffers;

    public static List<List<MerchantOffer>> allOffers;

    public static final List<Item> potionCycleOrder = List.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);

    public static void initializeLists() {
        if (blockOffers == null) {
            blockOffers = List.of(
                    createListing(new ItemStack(Blocks.DIRT.asItem(), 16), 40).markSellOffer(),
                    createListing(new ItemStack(Blocks.COBBLESTONE.asItem(), 16), 60).markSellOffer()
            );
            gemOffers = List.of(
                    createListing(new ItemStack(Items.REDSTONE), 40).markSellOffer(),
                    createListing(new ItemStack(Items.LAPIS_LAZULI), 80).markSellOffer(),
                    createListing(new ItemStack(Items.COPPER_INGOT), 20).markSellOffer(),
                    createListing(new ItemStack(Items.COAL), 60).markSellOffer(),
                    createListing(new ItemStack(Items.IRON_INGOT), 60).markSellOffer(),
                    createListing(new ItemStack(Items.GOLD_INGOT), 200).markSellOffer(),
                    createListing(new ItemStack(Items.DIAMOND), 500).markSellOffer(),
                    createListing(new ItemStack(Items.EMERALD), 600).markSellOffer(),
                    createListing(new ItemStack(Items.NETHERITE_SCRAP), 8000).markSellOffer()
            );
            foodOffers = List.of(
                    createListing(new ItemStack(Items.COOKED_BEEF, 16), 500).markSellOffer(),
                    createListing(new ItemStack(Items.GOLDEN_CARROT), 1000).markSellOffer(),
                    createListing(new ItemStack(Items.GOLDEN_APPLE), 5000).markSellOffer(),
                    createListing(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE), 50000).markSellOffer()
            );
            enchantedBookOffers = new ArrayList<>();
            for (ResourceLocation key : ForgeRegistries.ENCHANTMENTS.getKeys()) {
                Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(key);
                if (enchantment != null && !enchantment.isCurse() && !enchantment.isTreasureOnly()) {
                    EnchantmentInstance instance = new EnchantmentInstance(enchantment, 1);
                    ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                    EnchantedBookItem.addEnchantment(book, instance);
                    CustomMerchantOffer offer = createListing(book, instance.level * 1000);
                    enchantedBookOffers.add(offer);
                }
            }
            potionAndOtherOffers = new ArrayList<>();
            for (ResourceLocation key : ForgeRegistries.POTIONS.getKeys()) {
                Potion potion = ForgeRegistries.POTIONS.getValue(key);
                if (potion != null && !potion.getEffects().isEmpty() && !key.toString().contains("strong") && !key.toString().contains("long")) {
                    MobEffectInstance instance = potion.getEffects().get(0);
                    ItemStack regular = PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
                    int amount = 1000 + (instance.getDuration() >= 4800 ? 100 : 0) + (instance.getAmplifier() > 0 ? 100 : 0);
                    CustomMerchantOffer offer = createListing(regular, amount);
                    potionAndOtherOffers.add(offer);
                }
            }
            potionAndOtherOffers.add(createListing(new ItemStack(Items.ENDER_PEARL), 400).markSellOffer());
            potionAndOtherOffers.add(createListing(new ItemStack(ItemInit.HEART.get()), 5000));
            allOffers = List.of(blockOffers, gemOffers, foodOffers, enchantedBookOffers, potionAndOtherOffers);
        }
    }

    public static void createHelix(DustParticleOptions type, LivingEntity player, ServerLevel pLevel, float radius) {
        Vec3 loc = player.position();
        for (double y = 0; y <= 1; y += 0.001) {
            double x = radius * Math.cos(9 * y);
            double z = radius * Math.sin(9 * y);
            pLevel.sendParticles(type, loc.x() + x, loc.y() + 1.1, loc.z() + z, 0, 0, 0, 0, 0);
        }
    }

    public static boolean hasEnoughMoney(Player player, int amount) {
        var holder = BillionaireHolderAttacher.getHolderUnwrap(player);
        if (holder != null) {
            return holder.getMoney() >= amount;
        }
        return false;
    }

    public static void takeMoney(Player player, int amount) {
        var holder = BillionaireHolderAttacher.getHolderUnwrap(player);
        if (holder != null) {
            holder.addMoney(-amount);
        }
    }

    // Snipped from HundredDaysStory
    public static Direction findHorizontalDirection(BlockPos pos, Vec3 vector) {
        Vec3 center = Vec3.atCenterOf(pos);
        Vec3 direction = vector.subtract(center);
        boolean eastWest = (Math.abs(direction.x()) > Math.abs(direction.z()));
        if (eastWest) {
            if (direction.x >= 0) {
                return Direction.EAST;
            } else {
                return Direction.WEST;
            }
        } else {
            if (direction.z >= 0) {
                return Direction.SOUTH;
            } else {
                return Direction.NORTH;
            }
        }
    }

    public static ItemStack getMoneyForAmount(int amount) {
        Item temp = ItemInit.MONEY.get();
        ItemStack tempStack = new ItemStack(temp);
        MoneyHolderAttacher.getItemStackCapability(tempStack).ifPresent(h -> h.setAmount(amount));
        return tempStack;
    }

    private static CustomMerchantOffer createListing(ItemStack stack, int buyAmount) {
        return new CustomMerchantOffer(stack, getMoneyForAmount(buyAmount), Integer.MAX_VALUE, 0, 0);
    }
}
